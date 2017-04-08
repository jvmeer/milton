package com.jsvandermeer;


import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.MessageIterator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Created by Jacob on 3/18/2017.
 */

class OptionChain extends Chain {
    Map<ZonedDateTime, Strip> strips;

    OptionChain(String underlier, ZonedDateTime asOf, Connection connection) {
        this.underlier = underlier;
        this.asOf = asOf;
        expiries = new TreeSet<>();
        strips = new HashMap<>();

        try {
            String expiriesQuery = "select distinct expiry from (select * from options where as_of=" + asOf.toString();
            ResultSet expiriesSet = connection.createStatement().executeQuery(expiriesQuery);
            while (expiriesSet.next()) {
                expiries.add(Utils.stringToDate(expiriesSet.getString("expiry")));
            }
            for (ZonedDateTime expiry : expiries) {
                String stripQuery = "select strike, is_call, bid_price, ask_price, bid_size, ask_size from options " +
                        "where as_of=" + asOf.toString() + " and expiry=" + expiry.toString();
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
