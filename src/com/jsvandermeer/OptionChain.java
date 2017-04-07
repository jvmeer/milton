package com.jsvandermeer;


import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.MessageIterator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Created by Jacob on 3/18/2017.
 */

class OptionChain extends Chain {
    Map<ZonedDateTime, Strip> strips;


    static class Strip {
        ZonedDateTime expiry;
        Map<Option, Market> options;
    }

//    public OptionChain(String underlier, ZonedDateTime asOf, ZonedDateTime expiry, double forward, ResultSet resultSet) {
//        this.underlier = underlier;
//        this.asOf = asOf;
//        this.expiry = expiry;
//        this.forward = forward;
//        options = new TreeSet<>();
//        try {
//            while (resultSet.next()) {
//                String identifier = resultSet.getString("identifier");
//                String strike = resultSet.getString("strike");
//                Utils.OptionType optionType = null;
//                switch (resultSet.getString("optionType")) {
//                    case "P":
//                        optionType = Utils.OptionType.PUT;
//                    case "C":
//                        optionType = Utils.OptionType.CALL;
//                }
//                double bidPrice = resultSet.getDouble("bidPrice");
//                double askPrice = resultSet.getDouble("askPrice");
//                long bidSize = resultSet.getLong("bidSize");
//                long askSize = resultSet.getLong("askSize");
//
//                options.add(new Option(identifier, strike, optionType, new Market(bidPrice, askPrice, bidSize, askSize)));
//            }
//        } catch (SQLException exception) {
//            exception.printStackTrace();
//        }
//    }

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
