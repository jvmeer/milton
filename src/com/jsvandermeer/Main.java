package com.jsvandermeer;


import com.bloomberglp.blpapi.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {



    public static void main(String[] args) {

        ZonedDateTime startDate = Utils.stringToDate("20170103");
        ZonedDateTime endDate = Utils.stringToDate("20170103");

        Bloomberg.loadForwards(Utils.stringToDate("20170103"), Utils.stringToDate("20170103"),
                "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\history.db");

//        History history = new History(startDate, endDate, "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\history.db");
//        history.test();


    }





}
