package com.jsvandermeer;

import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Jacob on 3/25/2017.
 */
public class Utils {

    public static final String VIX_TICKER = "VIX Index";
    public static final String SPX_TICKER = "SPX Index";
    public static final String EXPIRY_TIME = "131500";
    public static final String TIME_ZONE = "America/Chicago";

    public static String dateToString(ZonedDateTime date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        return year + (month < 10 ? "0" : "") + month + (day < 10 ? "0" : "") + day;
    }

    public static ZonedDateTime stringToDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuuMMdd HHmmss VV");
        return ZonedDateTime.parse(date + " " + EXPIRY_TIME + " " + TIME_ZONE, formatter);
    }

    public static ZonedDateTime expiryFromTicker(String ticker) {
        String[] tokens = ticker.split(" ");
        String[] monthDayYear = tokens[2].split("/");
        return stringToDate(monthDayYear[2] + monthDayYear[0] + monthDayYear[1]);
    }

    public enum OptionType {CALL, PUT}

}
