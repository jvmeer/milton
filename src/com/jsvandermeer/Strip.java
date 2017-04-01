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

public class Strip {
    String underlier;
    ZonedDateTime asOf;
    ZonedDateTime expiry;
    double forward;
    SortedSet<Option> options;

    public Strip(String underlier, ZonedDateTime asOf, ZonedDateTime expiry, double forward, ResultSet resultSet) {
        this.underlier = underlier;
        this.asOf = asOf;
        this.expiry = expiry;
        this.forward = forward;
        options = new TreeSet<>();
        try {
            while (resultSet.next()) {
                String identifier = resultSet.getString("identifier");
                String strike = resultSet.getString("strike");
                Utils.OptionType optionType = null;
                switch (resultSet.getString("optionType")) {
                    case "P":
                        optionType = Utils.OptionType.PUT;
                    case "C":
                        optionType = Utils.OptionType.CALL;
                }
                double bidPrice = resultSet.getDouble("bidPrice");
                double askPrice = resultSet.getDouble("askPrice");
                long bidSize = resultSet.getLong("bidSize");
                long askSize = resultSet.getLong("askSize");

                options.add(new Option(identifier, strike, optionType, new Market(bidPrice, askPrice, bidSize, askSize)));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static class Option implements Comparable<Option> {
        final String identifier;
        final String strike;
        final Utils.OptionType optionType;
        final Market market;

        public Option(String identifier, String strike, Utils.OptionType optionType, Market market) {
            this.identifier = identifier;
            this.strike = strike;
            this.optionType = optionType;
            this.market = market;
        }

        @Override public boolean equals(Object other) {
            Option otherOption = (Option) other;
            return identifier.equals(otherOption.identifier);
        }

        @Override public int hashCode() {
            return identifier.hashCode();
        }

        @Override public int compareTo(Option other) {
            if (strike.equals(other.strike)) {
                if (optionType.equals(other.optionType)) {
                    return 0;
                } else if (optionType.equals(Utils.OptionType.PUT)) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (Double.parseDouble(strike) < Double.parseDouble(other.strike)) {
                return -1;
            } else {
                return 1;
            }
        }

    }

    public static class Market {
        final double bidPrice;
        final double askPrice;
        final long bidSize;
        final long askSize;

        public Market(double bidPrice, double askPrice, long bidSize, long askSize) {
            this.bidPrice = bidPrice;
            this.askPrice = askPrice;
            this.bidSize = bidSize;
            this.askSize = askSize;
        }

        @Override
        public String toString() {
            return "(" + bidPrice + " " + bidSize + "," + askPrice + " " + askSize + ")";
        }
    }



}
