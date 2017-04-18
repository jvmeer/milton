package com.jsvandermeer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.jsvandermeer.Utils.dateToString;


/**
 * Created by Jacob on 4/6/2017.
 */
abstract class Chain {
    String underlier;
    ZonedDateTime asOf;
    NavigableSet<ZonedDateTime> expiries;


    Chain(String underlier, ZonedDateTime asOf, Connection connection, String table) {
        this.underlier = underlier;
        this.asOf = asOf;
        expiries = new TreeSet<>();
        try {
            String expiriesQuery = "select distinct expiry from (select * from " + table + " where underlier=" +
                    underlier + " and as_of=" + dateToString(asOf);
            ResultSet expiriesSet = connection.createStatement().executeQuery(expiriesQuery);
            while (expiriesSet.next()) {
                expiries.add(Utils.stringToDate(expiriesSet.getString("expiry")));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    NavigableSet<ZonedDateTime> getExpiries() {
        return expiries;
    }

    static class Market {
        final Double bidPrice;
        final Double askPrice;
        final Integer bidSize;
        final Integer askSize;

        Market(Double bidPrice, Double askPrice, Integer bidSize, Integer askSize) {
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
