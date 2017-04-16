package com.jsvandermeer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        String tableStatement = "CREATE TABLE IF NOT EXISTS options(underlier TEXT, expiry TEXT, strike REAL, " +
                "is_call BOOLEAN, as_of TEXT, bid_price REAL, ask_price REAL, bid_size INTEGER, ask_size INTEGER)";
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
                preparedStatement.setDouble(6, optionLine.bidPrice);
                preparedStatement.setDouble(7, optionLine.askPrice);
                preparedStatement.setInt(8, optionLine.bidSize);
                preparedStatement.setInt(9, optionLine.askSize);

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
        String insertStatement = "INSERT INTO futures(underlier, expiry, as_of, bid_price, ask_price, bid_size, " +
                "ask_size) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try {
            for (FutureLine futureLine : futureLines) {
                PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
                preparedStatement.setString(1, futureLine.underlier);
                preparedStatement.setString(2, futureLine.expiry);
                preparedStatement.setString(3, futureLine.asOf);
                preparedStatement.setDouble(4, futureLine.bidPrice);
                preparedStatement.setDouble(5, futureLine.askPrice);
                preparedStatement.setInt(6, futureLine.bidSize);
                preparedStatement.setInt(7, futureLine.askSize);

                preparedStatement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    void insertForwards(Collection<ForwardLine> forwardLines) {
        String insertStatement = "INSERT INTO forwards(underlier, expiry, as_of, forward) VALUES(?, ?, ?, ?)";
        try {
            for (ForwardLine forwardLine : forwardLines) {
                PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
                preparedStatement.setString(1, forwardLine.underlier);
                preparedStatement.setString(2, forwardLine.expiry);
                preparedStatement.setString(3, forwardLine.asOf);
                preparedStatement.setDouble(4, forwardLine.forward);

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

        Line(String underlier, String expiry, String asOf) {
            this.underlier = underlier;
            this.expiry = expiry;
            this.asOf = asOf;
        }
    }

    static class OptionLine extends Line {
        final double strike;
        final boolean isCall;
        final double bidPrice;
        final double askPrice;
        final int bidSize;
        final int askSize;

        OptionLine (String underlier, String expiry, double strike, boolean isCall, String asOf,
                           double bidPrice, double askPrice, int bidSize, int askSize) {
            super(underlier, expiry, asOf);
            this.strike = strike;
            this.isCall = isCall;
            this.bidPrice = bidPrice;
            this.askPrice = askPrice;
            this.bidSize = bidSize;
            this.askSize = askSize;
        }
    }

    static class FutureLine extends Line {
        final double bidPrice;
        final double askPrice;
        final int bidSize;
        final int askSize;

        FutureLine(String underlier, String expiry, String asOf, double bidPrice, double askPrice, int bidSize,
                   int askSize) {
            super(underlier, expiry, asOf);
            this.bidPrice = bidPrice;
            this.askPrice = askPrice;
            this.bidSize = bidSize;
            this.askSize = askSize;
        }
    }

    static class ForwardLine extends Line {
        final double forward;

        ForwardLine(String underlier, String expiry, String asOf, double forward) {
            super(underlier, expiry, asOf);
            this.forward = forward;
        }
    }
}
