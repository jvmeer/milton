package com.jsvandermeer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jacob on 4/8/2017.
 */
public class ForwardChain extends Chain {
    Map<ZonedDateTime, Double> forwards;

    ForwardChain(String underlier, ZonedDateTime asOf, Connection connection) {
        super(underlier, asOf, connection, "forwards");
        forwards = new HashMap<>();
        try {
            for (ZonedDateTime expiry : expiries) {
                String forwardQuery = "select bid_price, ask_price, bid_size, ask_size from forwards where underlier=" +
                        underlier + " and as_of=" + asOf.toString() + " and expiry=" + expiry.toString();
                ResultSet forwardSet = connection.createStatement().executeQuery(forwardQuery);
                if (forwardSet.next()) {
                    forwards.put(expiry, forwardSet.getDouble("forward"));
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
