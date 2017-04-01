package com.jsvandermeer;


import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.MessageIterator;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Jacob on 3/18/2017.
 */

public class Strip {
    String underlier;
    ZonedDateTime asOf;
    ZonedDateTime expiry;
    double forward;
    SortedSet<Option> options;

    public Strip(String underlier, ZonedDateTime asOf, Map<ZonedDateTime, Double> forwards, Map<String, Market> markets) {
        this.underlier = underlier;
        this.asOf = asOf;
        chain = new HashMap<>();
        expiries = new TreeSet<>();
        spot = forwards.get(asOf);
        markets.forEach((ticker, market) -> {
            ZonedDateTime expiry = Utils.expiryFromTicker(ticker);
            expiries.add(expiry);
            if (chain.containsKey(expiry)) {
                chain.get(expiry).strip.put(ticker, market);
            } else {
                chain.put(expiry, new Link(forwards.get(expiry), ticker, market));
            }
        });
    }



    private class Link {
        private double forward;
        Map<String, Market> strip;
        public Link(double forward, String ticker, Market market) {
            this.forward = forward;
            strip = new HashMap<>();
            strip.put(ticker, market);
        }
    }


    private static class Option {
        String identifier;
        Market market;
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
