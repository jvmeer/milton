package com.jsvandermeer;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jacob on 3/26/2017.
 */
public class Replication implements Comparable<Replication> {
    ZonedDateTime vixDate;
    ZonedDateTime spxFrontDate;
    ZonedDateTime spxBackDate;
    Map<ZonedDateTime, Double> spxPrices;
    Map<ZonedDateTime, Double> vixPrices;

    public Replication(ZonedDateTime vixDate, ZonedDateTime spxFrontDate, ZonedDateTime spxBackDate) {
        this.vixDate = vixDate;
        this.spxFrontDate = spxFrontDate;
        this.spxBackDate = spxBackDate;
        spxPrices = new HashMap<>();
        vixPrices = new HashMap<>();
    }

    public void addSpxPrice(ZonedDateTime asOf, double price) {
        spxPrices.put(asOf, price);
    }

    public void addVixPrice(ZonedDateTime asOf, double price) {
        vixPrices.put(asOf, price);
    }

    @Override public boolean equals(Object other) {
        if (!(other instanceof Replication)) return false;
        Replication otherReplication = (Replication) other;
        return (vixDate.equals(otherReplication.vixDate) && spxFrontDate.equals(otherReplication.spxFrontDate) &&
                spxBackDate.equals(otherReplication.spxBackDate));
    }

    @Override public int hashCode() {
        return vixDate.hashCode() * spxFrontDate.hashCode() * spxBackDate.hashCode();
    }

    @Override public int compareTo(Replication other) {
        if (spxFrontDate.isBefore(other.spxFrontDate)) {
            return -1;
        } else if (spxFrontDate.equals(other.spxFrontDate)) {
            if (spxBackDate.isBefore(other.spxBackDate)) {
                return -1;
            } else if (spxBackDate.equals(other.spxBackDate)) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }


}
