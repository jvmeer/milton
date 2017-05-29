package com.jsvandermeer;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Jacob on 4/1/2017.
 */
public class History {

    final Replication.Specification specification;
    private Map<ZonedDateTime, Replication.Basis> bases;


    History(Replication.Specification specification) {
        this.specification = specification;
        bases = new HashMap<>();
    }

    void addBasis(ZonedDateTime asOf, Replication.Basis basis) {
        bases.put(asOf, basis);
    }


    void plotBases() {
        for (ZonedDateTime asOf : bases.keySet()) {
            System.out.println(asOf.toString() + ": " + bases.get(asOf).basis);
        }
    }


}
