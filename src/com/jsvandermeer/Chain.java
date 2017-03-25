package com.jsvandermeer;


import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.MessageIterator;

import java.time.ZonedDateTime;
import java.util.HashMap;

/**
 * Created by Jacob on 3/18/2017.
 */

public class Chain {
    String underlier;
    ZonedDateTime asOf;
    HashMap<ZonedDateTime, Link> chain = null;
    double spot;

    private class Link {
        double forward;
        HashMap<String, Market> strip;
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

    public void setAsOf(ZonedDateTime asOf) {
        this.asOf = asOf;
    }

    public void setSymbols(MessageIterator iter) {
        while (iter.hasNext()) {
            Message message = iter.next();
        }
    }

}
