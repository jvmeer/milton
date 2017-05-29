package com.jsvandermeer;


import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

public class Main {



    public static void main(String[] args) {
        DataInterface dataInterface = DataInterface.getInstance();
        Collection<LocalDate> holidays = dataInterface.retrieveHolidays();



        ZonedDateTime startDate = Utils.stringToZonedDateTime("2017-04-19T16:15:00[America/New_York]");
        ZonedDateTime endDate = Utils.stringToZonedDateTime("2017-04-21T08:29:59[America/New_York]");

        LocalDate startLocalDate = Utils.stringToLocalDate("2017-05-22");
        LocalDate endLocalDate = Utils.stringToLocalDate("2017-05-26");

        long interval = DAYS.between(startDate, endDate);
        System.out.println(interval);

        Backtester backtester = new Backtester(startLocalDate, endLocalDate, Utils.Underlier.SPX, Utils.Underlier.VIX);
        Map<Replication.Specification, History> histories = backtester.generateHistories();
        for(Replication.Specification specification : histories.keySet()) {
            histories.get(specification).plotBases();
        }


//        FutureChain futureChain = new FutureChain(Utils.Underlier.VIX, startDate);
//
//        for (ZonedDateTime expiry : futureChain.getExpiries()) {
//            System.out.println(expiry.toString());
//            System.out.println(futureChain.getFuture(expiry).toString());
//        }


//        DataLoader.retrieveFilesFromLiveVol("order_000002004/item_000003163");
//        DataLoader.loadOptionsFromLocal();

//        DataLoader.loadHolidays();


//        DataLoader.loadFuturesFromBloomberg(startLocalDate, endLocalDate);


//        History history = new History(startDate, endDate, "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\history.db");
//        history.test();


    }


}
