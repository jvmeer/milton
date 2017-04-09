package com.jsvandermeer;

import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Jacob on 3/25/2017.
 */
public class Utils {

    static final String DATABASE_PATH = "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\history.db";

    public static final String VIX_TICKER = "VIX Index";
    public static final String SPX_TICKER = "SPX Index";
    public static final String EXPIRY_TIME = "131500";
    public static final String TIME_ZONE = "America/Chicago";

    public static String dateToString(ZonedDateTime date) {
        return date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static ZonedDateTime stringToDate(String date) {
        return ZonedDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static ZonedDateTime expiryFromTicker(String ticker) {
        String[] tokens = ticker.split(" ");
        String[] monthDayYear = tokens[2].split("/");
        return stringToDate(monthDayYear[2] + monthDayYear[0] + monthDayYear[1]);
    }

    public enum OptionType {CALL, PUT}

}
