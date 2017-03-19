package com.jsvandermeer;


import java.time.ZonedDateTime;
import java.util.HashMap;

/**
 * Created by Jacob on 3/18/2017.
 */
public class Chain {
    ZonedDateTime asOf;
    HashMap<ZonedDateTime, HashMap<String, Market>> chain;



    private class Market {
        double bidPrice;
        double askPrice;
        long bidSize;
        long askSize;
    }

}
