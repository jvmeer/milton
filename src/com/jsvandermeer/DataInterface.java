package com.jsvandermeer;

import java.sql.*;
import java.util.Collection;
import java.util.List;

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

    void insertOptions(Collection<OptionLine> optionLines) {
        String tableStatement = "CREATE TABLE IF NOT EXISTS options (underlier TEXT, expiry TEXT, strike REAL, " +
                "is_call BOOLEAN, as_of TEXT, bid_price REAL, ask_price REAL, bid_size INTEGER, ask_size INTEGER) " +
                "PRIMARY KEY (underlier, expiry, strike, is_call, as_of)";
        String insertStatement = "INSERT INTO options(underlier, expiry, strike, is_call, as_of, " +
                "bid_price, ask_price, bid_size, ask_size) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            connection.createStatement().executeUpdate(tableStatement);
            int counter = 1;
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
                    preparedStatement.setInt(8, optionLine.bidSize);
                }
                if (optionLine.askSize == null) {
                    preparedStatement.setNull(9, Types.INTEGER);
                } else {
                    preparedStatement.setInt(9, optionLine.askSize);
                }

                preparedStatement.executeUpdate();
                counter++;
                if (counter % 100 == 0) System.out.println(counter);
            }
            connection.commit();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    void insertFutures(Collection<FutureLine> futureLines) {
        String tableStatement = "CREATE TABLE IF NOT EXISTS futures (underlier TEXT, expiry TEXT, as_of TEXT, " +
                "bid_price REAL, ask_price REAL, bid_size INTEGER, ask_size INTEGER) PRIMARY KEY (underlier, " +
                "expiry, as_of)";
        String insertStatement = "INSERT INTO futures(underlier, expiry, as_of, bid_price, ask_price, bid_size, " +
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
                    preparedStatement.setInt(6, futureLine.bidSize);
                }
                if (futureLine.askSize == null) {
                    preparedStatement.setNull(7, Types.INTEGER);
                } else {
                    preparedStatement.setInt(7, futureLine.askSize);
                }

                preparedStatement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    static abstract class Line {
        final String underlier;
        final String expiry;
        final String asOf;
        final Double bidPrice;
        final Double askPrice;
        final Integer bidSize;
        final Integer askSize;

        Line(String underlier, String expiry, String asOf, Double bidPrice, Double askPrice, Integer bidSize,
             Integer askSize) {
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
                           Double bidPrice, Double askPrice, Integer bidSize, Integer askSize) {
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
        FutureLine(String ticker, String expiry, String asOf, Double bidPrice, Double askPrice, Integer bidSize,
                   Integer askSize) {
            super(ticker, expiry, asOf, bidPrice, askPrice, bidSize, askSize);
        }

        @Override public String toString() {
            return "(" + underlier + ", " + expiry + ", " + asOf + ", " + bidPrice + ", " + askPrice + ", " +
                    bidSize + ", " + askSize + ")";
        }
    }
}
