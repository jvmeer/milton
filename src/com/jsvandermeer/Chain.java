package com.jsvandermeer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.NavigableSet;
import java.util.TreeSet;

import static com.jsvandermeer.Utils.zonedDateTimeToString;


/**
 * Created by Jacob on 4/6/2017.
 */
abstract class Chain {
    private final Utils.Underlier underlier;
    private final ZonedDateTime asOf;
    protected NavigableSet<ZonedDateTime> expiries;
    protected final DataInterface dataInterface;


    Chain(Utils.Underlier underlier, ZonedDateTime asOf) {
        this.underlier = underlier;
        this.asOf = asOf;
        expiries = new TreeSet<>();
        dataInterface = DataInterface.getInstance();
    }

    NavigableSet<ZonedDateTime> getExpiries() {
        return expiries;
    }

    static class Market {
        final Double bidPrice;
        final Double askPrice;
        final Long bidSize;
        final Long askSize;

        Market(Double bidPrice, Double askPrice, Long bidSize, Long askSize) {
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
