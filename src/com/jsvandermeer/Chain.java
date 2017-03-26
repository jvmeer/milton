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

public class Chain {
    String underlier;
    ZonedDateTime asOf;
    Map<ZonedDateTime, Link> chain;
    private SortedSet<ZonedDateTime> expiries;
    double spot;

    public Chain(String underlier, ZonedDateTime asOf, Map<ZonedDateTime, Double> forwards, Map<String, Market> markets) {
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

    public static class Market {
        final double bidPrice;
        final double askPrice;
//        long bidSize;
//        long askSize;

        public Market(double bidPrice, double askPrice) {
            this.bidPrice = bidPrice;
            this.askPrice = askPrice;
        }

        @Override
        public String toString() {
            return "(" + bidPrice + "," + askPrice + ")";
        }
    }



}
