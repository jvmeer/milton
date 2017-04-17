package com.jsvandermeer;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.*;

import static com.jsvandermeer.Utils.dateToString;

/**
 * Created by Jacob on 4/4/2017.
 */
public class Backtester {
    ZonedDateTime startDate;
    ZonedDateTime endDate;
    Connection connection;


    Backtester(ZonedDateTime startDate, ZonedDateTime endDate, Connection connection) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.connection = connection;
    }

    void populateStrikeHistory(Utils.Underlier indexUnderlier, Utils.Underlier volUnderlier) {
        List<ZonedDateTime> asOfs = new ArrayList<>();
        String asOfsStatement = "select distinct as_of from vix_futures where as_of>=" + dateToString(startDate) +
                " and as_of <" + dateToString(endDate);
        try {
            ResultSet asOfsSet = connection.createStatement().executeQuery(asOfsStatement);
            while (asOfsSet.next()) {
                asOfs.add(Utils.stringToDate(asOfsSet.getString("as_of")));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        for (ZonedDateTime asOf : asOfs) {
            FutureChain futureChain = new FutureChain(volUnderlier.ticker, asOf, connection);
            OptionChain futureOptionChain = new OptionChain(volUnderlier.ticker, asOf, connection);
            OptionChain forwardOptionChain = new OptionChain(indexUnderlier.ticker, asOf, connection);
            for (ZonedDateTime startFutureExpiry : futureChain.getExpiries()) {
                for (ZonedDateTime endFutureExpiry : futureChain.getExpiries().tailSet(startFutureExpiry)) {
                    NavigableSet<ZonedDateTime> futureExpiries = futureChain.getExpiries().subSet(startFutureExpiry, true,
                            endFutureExpiry, true);
                    SortedSet<OptionChain.Strip> futureStrips = new TreeSet<>();
                    Map<ZonedDateTime, Chain.Market> futures = new HashMap<>();
                    for (ZonedDateTime futureExpiry : futureExpiries) {
                        String futureStripStatement = "select strike, is_call, bid_price, ask_price, bid_size, " +
                                "ask_size from options where underlier=" + volUnderlier.ticker + " and expiry=" +
                                Utils.dateToString(futureExpiry) + " and as_of=" + Utils.dateToString(asOf);
                        ResultSet futureStripSet = null;
                        try {
                            futureStripSet = connection.createStatement().executeQuery(futureStripStatement);
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                        futureStrips.add(new OptionChain.Strip(futureExpiry, futureStripSet));
                        String futureStatement = "select bid_price, ask_price, bid_size, ask_size from futures where " +
                                "underlier=" + volUnderlier.ticker + " and expiry=" + Utils.dateToString(futureExpiry) +
                                " and as_of=" + Utils.dateToString(asOf);
                        ResultSet futureSet = null;
                        try {
                            futureSet = connection.createStatement().executeQuery(futureStatement);
                            if (futureSet.next()) {
                                futures.put(futureExpiry, new Chain.Market(futureSet.getDouble("bid_price"),
                                        futureSet.getDouble("ask_price"), futureSet.getInt("bid_size"),
                                        futureSet.getInt("ask_size")));
                            }
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }

                    }
                    Set<ZonedDateTime> frontExpiries =
                            forwardOptionChain.getExpiries().subSet(startFutureExpiry.minusDays(Replication.DAY_TOLERANCE),
                                    true, startFutureExpiry.plusDays(Replication.DAY_TOLERANCE), true);
                    Set<ZonedDateTime> backExpiries =
                            forwardOptionChain.getExpiries().subSet(endFutureExpiry.minusDays(Replication.DAY_TOLERANCE),
                                    true, endFutureExpiry.plusDays(Replication.DAY_TOLERANCE), true);
                    for (ZonedDateTime frontExpiry : frontExpiries) {
                        for (ZonedDateTime backExpiry : backExpiries) {
                            String frontStripStatement = "select strike, is_call, bid_price, ask_price, bid_size, " +
                                    "ask_size from options where underlier =" + indexUnderlier.ticker + " and expiry=" +
                                    Utils.dateToString(frontExpiry) + " and as_of=" + Utils.dateToString(asOf);
                            ResultSet frontStripSet = null;
                            try {
                                frontStripSet = connection.createStatement().executeQuery(frontStripStatement);
                            } catch (SQLException exception) {
                                exception.printStackTrace();
                            }
                            OptionChain.Strip frontStrip = new OptionChain.Strip(frontExpiry, frontStripSet);
                            String backStripStatement = "select strike, is_call, bid_price, ask_price, bid_size, " +
                                    "ask_size from options where underlier =" + indexUnderlier.ticker + " and expiry=" +
                                    Utils.dateToString(backExpiry) + " and as_of=" + Utils.dateToString(asOf);
                            ResultSet backStripSet = null;
                            try {
                                backStripSet = connection.createStatement().executeQuery(frontStripStatement);
                            } catch (SQLException exception) {
                                exception.printStackTrace();
                            }
                            OptionChain.Strip backStrip = new OptionChain.Strip(frontExpiry, frontStripSet);

                            String frontForwardStatement = "select forward from forwards where underlier=" +
                                    indexUnderlier.ticker + " and expiry=" + Utils.dateToString(frontExpiry) + " and " +
                                    "as_of=" + Utils.dateToString(asOf);
                            double frontForward = 0.0;
                            try {
                                ResultSet frontForwardSet = connection.createStatement().executeQuery(frontForwardStatement);
                                if (frontForwardSet.next()) {
                                    frontForward = frontForwardSet.getDouble("forward");
                                }
                            } catch (SQLException exception) {
                                exception.printStackTrace();
                            }

                            String backForwardStatement = "select forward from forwards where underlier=" +
                                    indexUnderlier.ticker + " and expiry=" + Utils.dateToString(backExpiry) + " and " +
                                    "as_of=" + Utils.dateToString(asOf);
                            double backForward = 0.0;
                            try {
                                ResultSet backForwardSet = connection.createStatement().executeQuery(frontForwardStatement);
                                if (backForwardSet.next()) {
                                    backForward = backForwardSet.getDouble("forward");
                                }
                            } catch (SQLException exception) {
                                exception.printStackTrace();
                            }



                            Replication replication = new Replication(indexUnderlier.ticker, volUnderlier.ticker, asOf,
                                    futureExpiries, frontExpiry, backExpiry, futureStrips, frontStrip, backStrip,
                                    futures, frontForward, backForward);


                            String replicationStatement = "insert into replications (index_underlier, vol_underlier," +
                                    " front_expiry, back_expiry, as_of, index_mid, vix_bid, vix_ask) values (?, ?, ?," +
                                    " ?, ?, ?, ?, ?)";
                            try {
                                PreparedStatement preparedStatement = connection.prepareStatement(replicationStatement);
                                preparedStatement.setString(1, indexUnderlier.ticker);
                                preparedStatement.setString(2, volUnderlier.ticker);
                                preparedStatement.setString(3, Utils.dateToString(frontExpiry));
                                preparedStatement.setString(4, Utils.dateToString(backExpiry));
                                preparedStatement.setString(5, Utils.dateToString(asOf));
                                preparedStatement.setDouble(6, replication.indexMidStrike());
                                preparedStatement.setDouble(7, replication.vixBidStrike());
                                preparedStatement.setDouble(8, replication.vixAskStrike());
                            } catch (SQLException exception) {
                                exception.printStackTrace();
                            }

                        }
                    }
                }
            }
        }
    }
}
