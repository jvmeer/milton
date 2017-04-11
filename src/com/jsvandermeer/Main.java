package com.jsvandermeer;


import com.bloomberglp.blpapi.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

public class Main {



    public static void main(String[] args) {

        ZonedDateTime startDate = Utils.stringToDate("2017-04-19T08:30:00-06:00");
        ZonedDateTime endDate = Utils.stringToDate("2017-04-21T08:29:59-06:00");

        long interval = DAYS.between(startDate, endDate);
        System.out.println(interval);


//        Bloomberg.loadSpxForwards(Utils.stringToDate("20170103"), Utils.stringToDate("20170106"),
//                "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\history.db");


//        History history = new History(startDate, endDate, "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\history.db");
//        history.test();


    }





}
