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
public class FutureChain extends Chain {
    private final Map<ZonedDateTime, Market> futures;

    FutureChain(Utils.Underlier underlier, ZonedDateTime asOf) {
        super(underlier, asOf);
        expiries = dataInterface.retrieveExpiries(underlier, asOf, "futures");
        futures = dataInterface.retrieveFutures(underlier, asOf);
    }

    Market getFuture(ZonedDateTime expiry) {
        return futures.get(expiry);
    }

}
