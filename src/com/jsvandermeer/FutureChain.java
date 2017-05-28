package com.jsvandermeer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.jsvandermeer.Utils.zonedDateTimeToString;

/**
 * Created by Jacob on 4/8/2017.
 */
public class FutureChain extends Chain {
    Map<ZonedDateTime, Market> futures;

    FutureChain(String underlier, ZonedDateTime asOf, Connection connection) {
        super(underlier, asOf, connection, "futures");
        futures = new HashMap<>();
        try {
            for (ZonedDateTime expiry : expiries) {
                String futureQuery = "select bid_price, ask_price, bid_size, ask_size from futures where underlier=" +
                        underlier + " and as_of=" + zonedDateTimeToString(asOf) + " and expiry=" + zonedDateTimeToString(expiry);
                ResultSet futureSet = connection.createStatement().executeQuery(futureQuery);
                if (futureSet.next()) {
                    Market market = new Market(futureSet.getDouble("bid_price"),
                            futureSet.getDouble("ask_price"), futureSet.getInt("bid_size"),
                            futureSet.getInt("ask_size"));
                    futures.put(expiry, market);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
