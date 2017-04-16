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

        ZonedDateTime startDate = Utils.stringToDate("2017-04-19T08:30:00[America/New_York]");
        ZonedDateTime endDate = Utils.stringToDate("2017-04-21T08:29:59[America/New_York]");

        long interval = DAYS.between(startDate, endDate);
        System.out.println(interval);

        String[] underliers = {Utils.SPX_TICKER, Utils.VIX_TICKER};

//        DataLoader.retrieveFilesFromLiveVol("order_000001515/item_000002548");
        DataLoader.loadOptionsFromLocal(startDate, endDate, underliers);


//        History history = new History(startDate, endDate, "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\history.db");
//        history.test();


    }





}
