package com.jsvandermeer;


import com.bloomberglp.blpapi.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

public class Main {



    public static void main(String[] args) {

        ZonedDateTime startDate = Utils.stringToDate("2017-04-19T08:30:00[America/New_York]");
        ZonedDateTime endDate = Utils.stringToDate("2017-04-21T08:29:59[America/New_York]");

        LocalDate startLocalDate = stringToDate("20170403");
        LocalDate endLocalDate = stringToDate("20170405");

        long interval = DAYS.between(startDate, endDate);
        System.out.println(interval);


        DataLoader.retrieveFilesFromLiveVol("order_000002004/item_000003163");
        DataLoader.loadOptionsFromLocal();

        DataLoader.loadFuturesFromBloomberg(startLocalDate, endLocalDate);


//        History history = new History(startDate, endDate, "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\history.db");
//        history.test();


    }


    static LocalDate stringToDate(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuuMMdd");
        return LocalDate.parse(date, dateTimeFormatter);
    }


}
