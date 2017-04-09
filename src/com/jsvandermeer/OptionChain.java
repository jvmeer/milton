package com.jsvandermeer;


import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.MessageIterator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;

import static com.jsvandermeer.Utils.dateToString;

/**
 * Created by Jacob on 3/18/2017.
 */

class OptionChain extends Chain {
    Map<ZonedDateTime, Strip> strips;

    OptionChain(String underlier, ZonedDateTime asOf, Connection connection) {
        super(underlier, asOf, connection, "options");
        strips = new HashMap<>();

        try {
            for (ZonedDateTime expiry : expiries) {
                String stripQuery = "select strike, is_call, bid_price, ask_price, bid_size, ask_size from options " +
                        "where underlier=" + underlier + " and as_of=" + dateToString(asOf) + " and expiry=" +
                        dateToString(expiry);
                ResultSet stripSet = connection.createStatement().executeQuery(stripQuery);
                Strip strip = new Strip(expiry, stripSet);
                strips.put(expiry, strip);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    static class Strip {
        ZonedDateTime expiry;
        Map<Option, Market> options;

        Strip(ZonedDateTime expiry, ResultSet resultSet) {
            this.expiry = expiry;
            options = new HashMap<>();
            try {
                while (resultSet.next()) {
                    Option option = new Option(resultSet.getDouble("strike"),
                            resultSet.getBoolean("is_call"));
                    Market market = new Market(resultSet.getDouble("bid_price"),
                            resultSet.getDouble("ask_price"), resultSet.getInt("bid_size"),
                            resultSet.getInt("ask_size"));
                    options.put(option, market);
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    static class Option {
        double strike;
        boolean isCall;

        Option(double strike, boolean isCall) {
            this.strike = strike;
            this.isCall = isCall;
        }

        @Override public int hashCode() {
            return (int)(strike * 10) * (isCall ? 1 : -1);
        }
    }

}
