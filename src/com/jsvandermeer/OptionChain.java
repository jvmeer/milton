package com.jsvandermeer;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;

import static com.jsvandermeer.Utils.zonedDateTimeToString;

/**
 * Created by Jacob on 3/18/2017.
 */

class OptionChain extends Chain {
    private final Map<ZonedDateTime, Strip> strips;

    OptionChain(Utils.Underlier underlier, ZonedDateTime asOf) {
        super(underlier, asOf);
        expiries = dataInterface.retrieveExpiries(underlier, asOf, "options");
        strips = dataInterface.retrieveStrips(underlier, asOf);
    }

    Strip getStrip(ZonedDateTime expiry) {
        return strips.get(expiry);
    }

    public static class Strip {
        ZonedDateTime expiry;
        Map<Option, Market> options;

        Strip(ZonedDateTime expiry, Map<Option, Market> options) {
            this.expiry = expiry;
            this.options = options;
        }
    }

    static class Option {
        double strike;
        boolean isCall;

        Option(double strike, boolean isCall) {
            this.strike = strike;
            this.isCall = isCall;
        }

        @Override public int hashCode() {
            return (int)(strike * 10) * (isCall ? 1 : -1);
        }
    }

}
