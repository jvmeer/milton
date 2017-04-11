package com.jsvandermeer;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

/**
 * Created by Jacob on 3/26/2017.
 */
public class Replication implements Comparable<Replication> {
    final static int DAY_TOLERANCE = 3;
    String futureUnderlier;
    String forwardUnderlier;
    ZonedDateTime asOf;
    SortedSet<ZonedDateTime> futureExpiries;
    ZonedDateTime frontExpiry;
    ZonedDateTime backExpiry;
    SortedSet<OptionChain.Strip> futureStrips;
    OptionChain.Strip frontStrip;
    OptionChain.Strip backStrip;
    Map<ZonedDateTime, Chain.Market> futures;
    double frontForward;
    double backForward;

    Replication(String futureUnderlier, String forwardUnderlier, ZonedDateTime asOf,
                SortedSet<ZonedDateTime> futureExpiries, ZonedDateTime frontExpiry, ZonedDateTime backExpiry,
                SortedSet<OptionChain.Strip> futureStrips, OptionChain.Strip frontStrip, OptionChain.Strip backStrip,
                Map<ZonedDateTime, Chain.Market> futures, double frontForward, double backForward) {
        this.futureUnderlier = futureUnderlier;
        this.forwardUnderlier = forwardUnderlier;
        this.asOf = asOf;
        this.futureExpiries = futureExpiries;
        this.frontExpiry = frontExpiry;
        this.backExpiry = backExpiry;
        this.futureStrips = futureStrips;
        this.frontStrip = frontStrip;
        this.backStrip = backStrip;
        this.futures = futures;
        this.frontForward = frontForward;
        this.backForward = backForward;
    }

    double futureStrikeBid() {
        return 0.0;
    }

    double futureStrikeMide() {
        return 0.0;
    }

    double futureStrikeAsk() {
        return 0.0;
    }

    double forwardStrikeMid() {
        return 0.0;
    }

    @Override public boolean equals(Object other) {
        if (!(other instanceof Replication)) return false;
        Replication otherReplication = (Replication) other;
        return (frontExpiry.equals(otherReplication.frontExpiry) &&
                backExpiry.equals(otherReplication.backExpiry));
    }

    @Override public int hashCode() {
        return frontExpiry.hashCode() * backExpiry.hashCode();
    }

    @Override public int compareTo(Replication other) {
        if (frontExpiry.isBefore(other.frontExpiry)) {
            return -1;
        } else if (frontExpiry.equals(other.frontExpiry)) {
            if (backExpiry.isBefore(other.backExpiry)) {
                return -1;
            } else if (backExpiry.equals(other.backExpiry)) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }
}
