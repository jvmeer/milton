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


//        DataLoader.retrieveFilesFromLiveVol("order_000001515/item_000002548");
//        DataLoader.loadOptionsFromLocal();

        BloombergInterface bloombergInterface = BloombergInterface.getInstance();
        Set<DataInterface.FutureLine> futureLines = bloombergInterface.retrieveFutureLines(startLocalDate,
                endLocalDate, Utils.Underlier.VIX);

        for (DataInterface.FutureLine futureLine : futureLines) {
            System.out.println(futureLine.toString());
        }


//        History history = new History(startDate, endDate, "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\history.db");
//        history.test();


    }


    static LocalDate stringToDate(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuuMMdd");
        return LocalDate.parse(date, dateTimeFormatter);
    }


}
