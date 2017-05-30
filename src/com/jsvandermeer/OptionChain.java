package com.jsvandermeer;


import java.math.BigDecimal;
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
        private final ZonedDateTime expiry;
        private final SortedMap<Option, Market> options;

        Strip(ZonedDateTime expiry, SortedMap<Option, Market> options) {
            this.expiry = expiry;
            this.options = options;
        }

        SortedSet<BigDecimal> getStrikes() {
            SortedSet<BigDecimal> strikes = new TreeSet<>();
            for (Option option : options.keySet()) {
                strikes.add(option.strike);
            }
            return strikes;
        }

        Double getPremiumDifferenceAtStrike(BigDecimal strike) {
            Option call = new Option(strike, true);
            Option put = new Option(strike, false);
            if (options.containsKey(call) && options.containsKey(put)) {
                return options.get(call).calculateMid() - options.get(put).calculateMid();
            }
            return null;
        }

        ZonedDateTime getExpiry() {
            return expiry;
        }

        double strikeRange() {
            return Math.abs(options.lastKey().strike.subtract(options.firstKey().strike).doubleValue());
        }

        Market getMarket(Option option) {
            return options.get(option);
        }
    }

    static class Option implements Comparable<Option> {
        private final BigDecimal strike;
        private final boolean isCall;

        Option(BigDecimal strike, boolean isCall) {
            this.strike = strike;
            this.isCall = isCall;
        }

        @Override public boolean equals(Object other) {
            if (!(other instanceof Option)) return false;
            Option otherOption = (Option) other;
            return ((strike.equals(otherOption.strike)) && (isCall == otherOption.isCall));
        }

        @Override public int hashCode() {
            return strike.hashCode() * (isCall ? 1 : -1);
        }

        @Override public int compareTo(Option other) {
            if (strike.compareTo(other.strike) < 0) {
                return -1;
            } else if (strike.compareTo(other.strike) > 0) {
                return 1;
            } else {
                if (isCall == other.isCall) {
                    return 0;
                } else if (isCall) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    }

}
