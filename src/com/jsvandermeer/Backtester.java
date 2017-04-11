package com.jsvandermeer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    void populateStrikeHistory() {
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
            FutureChain futureChain = new FutureChain(Utils.VIX_TICKER, asOf, connection);
            ForwardChain forwardChain = new ForwardChain(Utils.SPX_TICKER, asOf, connection);
            OptionChain futureOptionChain = new OptionChain(Utils.VIX_TICKER, asOf, connection);
            OptionChain forwardOptionChain = new OptionChain(Utils.SPX_TICKER, asOf, connection);
            for (ZonedDateTime startFutureExpiry : futureChain.getExpiries()) {
                for (ZonedDateTime endFutureExpiry : futureChain.getExpiries().tailSet(startFutureExpiry)) {
                    NavigableSet<ZonedDateTime> futureExpiries = futureChain.getExpiries().subSet(startFutureExpiry, true,
                            endFutureExpiry, true);
                    SortedSet<OptionChain.Strip> futureStrips = new TreeSet<>();
                    for (ZonedDateTime futureExpiry : futureExpiries) {
                        String futureStripStatement = "select strike, is_call, bid_price, ask_price, bid_size, " +
                                "ask_size from options where underlier=" + Utils.VIX_TICKER + " and expiry=" +
                                Utils.dateToString(futureExpiry) + " and as_of=" + Utils.dateToString(asOf);
                        ResultSet futureStripSet = null;
                        try {
                            futureStripSet = connection.createStatement().executeQuery(futureStripStatement);
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                        futureStrips.add(new OptionChain.Strip(futureExpiry, futureStripSet));
                    }
                    Set<ZonedDateTime> frontExpiries =
                            forwardChain.getExpiries().subSet(startFutureExpiry.minusDays(Replication.DAY_TOLERANCE),
                                    true, startFutureExpiry.plusDays(Replication.DAY_TOLERANCE), true);
                    Set<ZonedDateTime> backExpiries =
                            forwardChain.getExpiries().subSet(endFutureExpiry.minusDays(Replication.DAY_TOLERANCE),
                                    true, endFutureExpiry.plusDays(Replication.DAY_TOLERANCE), true);
                    for (ZonedDateTime frontExpiry : frontExpiries) {
                        for (ZonedDateTime backExpiry : backExpiries) {
                            Replication replication = new Replication(Utils.VIX_TICKER, Utils.SPX_TICKER, asOf,
                                    futureExpiries, frontExpiry, backExpiry, )
                        }
                    }

//                    Replication replication = new Replication(Utils.VIX_TICKER, Utils.SPX_TICKER, asOf, futureExpiries,
//                            )
                }
            }
        }
    }

    private Set<ZonedDateTime> getExpiriesWithinDays(NavigableSet<ZonedDateTime> expiries, ZonedDateTime date,
                                                     int days) {

    }



}
