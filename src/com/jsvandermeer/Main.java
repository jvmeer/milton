package com.jsvandermeer;


import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import static java.time.temporal.ChronoUnit.DAYS;

public class Main {



    public static void main(String[] args) {

        ZonedDateTime startDate = Utils.stringToZonedDateTime("2017-04-19T08:30:00[America/New_York]");
        ZonedDateTime endDate = Utils.stringToZonedDateTime("2017-04-21T08:29:59[America/New_York]");

        LocalDate startLocalDate = Utils.stringToLocalDate("2017-04-03");
        LocalDate endLocalDate = Utils.stringToLocalDate("2017-04-05");

        long interval = DAYS.between(startDate, endDate);
        System.out.println(interval);


//        DataLoader.retrieveFilesFromLiveVol("order_000002004/item_000003163");
//        DataLoader.loadOptionsFromLocal();

//        DataLoader.loadHolidays();

        DataInterface dataInterface = DataInterface.getInstance();
        Collection<LocalDate> holidays = dataInterface.retrieveHolidays();

        for (LocalDate holiday: holidays) {
            System.out.println(holiday.toString());
        }

//        DataLoader.loadFuturesFromBloomberg(startLocalDate, endLocalDate);


//        History history = new History(startDate, endDate, "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\history.db");
//        history.test();


    }


}
