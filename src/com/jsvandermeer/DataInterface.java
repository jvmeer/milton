package com.jsvandermeer;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Created by Jacob on 4/14/2017.
 */
public class DataInterface {
    private static DataInterface dataInterface;
    private Connection connection;

    private DataInterface() {
        try {
            connection = DriverManager.getConnection(Utils.DATABASE_PATH);
            connection.setAutoCommit(false);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    static DataInterface getInstance() {
        if (dataInterface == null) {
            dataInterface = new DataInterface();
        }
        return dataInterface;
    }

    void insertHolidays(Collection<LocalDate> holidays) {
        String tableStatement = "CREATE TABLE IF NOT EXISTS holidays (holiday TEXT NOT NULL PRIMARY KEY)";
        String insertStatement = "INSERT OR REPLACE INTO holidays(holiday) VALUES(?)";
        try {
            connection.createStatement().executeUpdate(tableStatement);
            for (LocalDate holiday : holidays) {
                PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
                preparedStatement.setString(1, Utils.localDateToString(holiday));
                preparedStatement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    void insertFutures(Collection<FutureLine> futureLines) {
        String tableStatement = "CREATE TABLE IF NOT EXISTS futures (underlier TEXT NOT NULL, expiry TEXT NOT NULL, " +
                "as_of TEXT NOT NULL, bid_price REAL, ask_price REAL, bid_size INTEGER, ask_size INTEGER, " +
                "PRIMARY KEY (underlier, expiry, as_of))";
        String insertStatement = "INSERT OR REPLACE INTO futures(underlier, expiry, as_of, bid_price, ask_price, bid_size, " +
                "ask_size) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try {
            connection.createStatement().executeUpdate(tableStatement);
            for (FutureLine futureLine : futureLines) {
                PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
                preparedStatement.setString(1, futureLine.underlier);
                preparedStatement.setString(2, futureLine.expiry);
                preparedStatement.setString(3, futureLine.asOf);
                if (futureLine.bidPrice == null) {
                    preparedStatement.setNull(4, Types.REAL);
                } else {
                    preparedStatement.setDouble(4, futureLine.bidPrice);
                }
                if (futureLine.askPrice == null) {
                    preparedStatement.setNull(5, Types.REAL);
                } else {
                    preparedStatement.setDouble(5, futureLine.askPrice);
                }
                if (futureLine.bidSize == null) {
                    preparedStatement.setNull(6, Types.INTEGER);
                } else {
                    preparedStatement.setLong(6, futureLine.bidSize);
                }
                if (futureLine.askSize == null) {
                    preparedStatement.setNull(7, Types.INTEGER);
                } else {
                    preparedStatement.setLong(7, futureLine.askSize);
                }

                preparedStatement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    void insertOptions(Collection<OptionLine> optionLines) {
        String tableStatement = "CREATE TABLE IF NOT EXISTS options (underlier TEXT NOT NULL, expiry TEXT NOT NULL, " +
                "strike REAL NOT NULL, is_call BOOLEAN NOT NULL, as_of TEXT NOT NULL, bid_price REAL, " +
                "ask_price REAL, bid_size INTEGER, ask_size INTEGER, PRIMARY KEY (underlier, expiry, strike, " +
                "is_call, as_of))";
        String insertStatement = "INSERT OR REPLACE INTO options(underlier, expiry, strike, is_call, as_of, " +
                "bid_price, ask_price, bid_size, ask_size) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            connection.createStatement().executeUpdate(tableStatement);
            for (OptionLine optionLine : optionLines) {
                PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
                preparedStatement.setString(1, optionLine.underlier);
                preparedStatement.setString(2, optionLine.expiry);
                preparedStatement.setDouble(3, optionLine.strike);
                preparedStatement.setBoolean(4, optionLine.isCall);
                preparedStatement.setString(5, optionLine.asOf);
                if (optionLine.bidPrice == null) {
                    preparedStatement.setNull(6, Types.REAL);
                } else {
                    preparedStatement.setDouble(6, optionLine.bidPrice);
                }
                if (optionLine.askPrice == null) {
                    preparedStatement.setNull(7, Types.REAL);
                } else {
                    preparedStatement.setDouble(7, optionLine.askPrice);
                }
                if (optionLine.bidSize == null) {
                    preparedStatement.setNull(8, Types.INTEGER);
                } else {
                    preparedStatement.setLong(8, optionLine.bidSize);
                }
                if (optionLine.askSize == null) {
                    preparedStatement.setNull(9, Types.INTEGER);
                } else {
                    preparedStatement.setLong(9, optionLine.askSize);
                }

                preparedStatement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    Collection<LocalDate> retrieveHolidays() {
        Collection<LocalDate> holidays = new HashSet<>();
        String holidaysQuery = "SELECT holiday FROM holidays";
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(holidaysQuery);
            while (resultSet.next()) {
                holidays.add(Utils.stringToLocalDate(resultSet.getString("holiday")));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return holidays;
    }

    Set<ZonedDateTime> retrieveAsOfs(Utils.Underlier underlier, ZonedDateTime startDate, ZonedDateTime endDate,
                                     String table) {
        Set<ZonedDateTime> asOfs = new HashSet<>();
        String asOfsQuery = "SELECT DISTINCT as_of FROM " + table + " WHERE underlier=? AND as_of>=? AND as_of<?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(asOfsQuery);
            preparedStatement.setString(1, underlier.ticker);
            preparedStatement.setString(2, Utils.zonedDateTimeToString(startDate));
            preparedStatement.setString(3, Utils.zonedDateTimeToString(endDate));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                asOfs.add(Utils.stringToZonedDateTime(resultSet.getString("as_of")));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return asOfs;
    }

    NavigableSet<ZonedDateTime> retrieveExpiries(Utils.Underlier underlier, ZonedDateTime asOf, String table) {
        NavigableSet<ZonedDateTime> expiries = new TreeSet<>();
        String expiriesQuery = "SELECT expiry FROM " + table + " WHERE underlier=? AND as_of=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(expiriesQuery);
            preparedStatement.setString(1, underlier.ticker);
            preparedStatement.setString(2, Utils.zonedDateTimeToString(asOf));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                expiries.add(Utils.stringToZonedDateTime(resultSet.getString("expiry")));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return expiries;
    }

    Map<ZonedDateTime, Chain.Market> retrieveFutures(Utils.Underlier underlier, ZonedDateTime asOf) {
        Map<ZonedDateTime, Chain.Market> futures = new HashMap<>();
        String futuresQuery = "SELECT expiry, bid_price, ask_price, bid_size, ask_size FROM futures WHERE " +
                "underlier=? AND as_of=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(futuresQuery);
            preparedStatement.setString(1, underlier.ticker);
            preparedStatement.setString(2, Utils.zonedDateTimeToString(asOf));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ZonedDateTime expiry = Utils.stringToZonedDateTime(resultSet.getString("expiry"));
                Chain.Market market = new Chain.Market(resultSet.getDouble("bid_price"),
                        resultSet.getDouble("ask_price"), resultSet.getLong("bid_size"),
                        resultSet.getLong("ask_size"));
                futures.put(expiry, market);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return futures;
    }

    Map<ZonedDateTime, OptionChain.Strip> retrieveStrips(Utils.Underlier underlier, ZonedDateTime asOf) {
        Map<ZonedDateTime, OptionChain.Strip> strips = new HashMap<>();
        Set<ZonedDateTime> expiries = retrieveExpiries(underlier, asOf, "options");
        for (ZonedDateTime expiry : expiries) {
            SortedMap<OptionChain.Option, Chain.Market> options = new TreeMap<>();
            String stripsQuery = "SELECT strike, is_call, bid_price, ask_price, bid_size, ask_size FROM options " +
                    "WHERE expiry=? AND underlier=? AND as_of=?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(stripsQuery);
                preparedStatement.setString(1, Utils.zonedDateTimeToString(expiry));
                preparedStatement.setString(2, underlier.ticker);
                preparedStatement.setString(3, Utils.zonedDateTimeToString(asOf));
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    BigDecimal strike = new BigDecimal(resultSet.getDouble("strike"));
                    strike.setScale(Utils.STRIKE_SCALE, BigDecimal.ROUND_HALF_UP);
                    OptionChain.Option option = new OptionChain.Option(strike,
                            resultSet.getBoolean("is_call"));
                    Chain.Market market = new Chain.Market(resultSet.getDouble("bid_price"),
                            resultSet.getDouble("ask_price"), resultSet.getLong("bid_size"),
                            resultSet.getLong("ask_size"));
                    options.put(option, market);
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            OptionChain.Strip strip = new OptionChain.Strip(expiry, options);
            strips.put(expiry, strip);
        }
        return strips;
    }

    static abstract class Line {
        final String underlier;
        final String expiry;
        final String asOf;
        final Double bidPrice;
        final Double askPrice;
        final Long bidSize;
        final Long askSize;

        Line(String underlier, String expiry, String asOf, Double bidPrice, Double askPrice, Long bidSize,
             Long askSize) {
            this.underlier = underlier;
            this.expiry = expiry;
            this.asOf = asOf;
            this.bidPrice = bidPrice;
            this.askPrice = askPrice;
            this.bidSize = bidSize;
            this.askSize = askSize;
        }
    }

    static class OptionLine extends Line {
        final double strike;
        final boolean isCall;

        OptionLine (String ticker, String expiry, double strike, boolean isCall, String asOf,
                           Double bidPrice, Double askPrice, Long bidSize, Long askSize) {
            super(ticker, expiry, asOf, bidPrice, askPrice, bidSize, askSize);
            this.strike = strike;
            this.isCall = isCall;
        }

        @Override public String toString() {
            return "(" + underlier + ", " + expiry + ", " + strike + ", " + isCall + ", " + asOf + ", " + bidPrice +
                    ", " + askPrice + ", " + bidSize + ", " + askSize + ")";
        }
    }

    static class FutureLine extends Line {
        FutureLine(String ticker, String expiry, String asOf, Double bidPrice, Double askPrice, Long bidSize,
                   Long askSize) {
            super(ticker, expiry, asOf, bidPrice, askPrice, bidSize, askSize);
        }

        @Override public String toString() {
            return "(" + underlier + ", " + expiry + ", " + asOf + ", " + bidPrice + ", " + askPrice + ", " +
                    bidSize + ", " + askSize + ")";
        }
    }
}
