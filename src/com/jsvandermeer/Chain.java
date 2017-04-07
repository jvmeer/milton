package com.jsvandermeer;

import java.time.ZonedDateTime;
import java.util.SortedSet;


/**
 * Created by Jacob on 4/6/2017.
 */
abstract class Chain {
    String underlier;
    ZonedDateTime asOf;
    SortedSet<ZonedDateTime> expiries;

    static class Market {
        final double bidPrice;
        final double askPrice;
        final int bidSize;
        final int askSize;

        Market(double bidPrice, double askPrice, int bidSize, int askSize) {
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
