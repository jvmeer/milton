package com.jsvandermeer;

import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by Jacob on 3/25/2017.
 */
public class Utils {

    static final String DATABASE_PATH = "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\historical_data.db";

    static final String VIX_TICKER = "VIX Index";
    static final String SPX_TICKER = "SPX Index";
    static final String EXPIRY_TIME = "131500";
    static final String TIME_ZONE = "America/Chicago";
    static final int VIX_DAYS = 30;

    static String dateToString(ZonedDateTime date) {
        return date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    static ZonedDateTime stringToDate(String date) {
        return ZonedDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    static ZonedDateTime addCalendarDays(ZonedDateTime date, int days) {
        return date.plusDays(days);
    }

    static ZonedDateTime addBusinessDays(ZonedDateTime date, int days) {
        return date;
    }

    static double absoluteCalendarDaysBetween(ZonedDateTime date1, ZonedDateTime date2) {
        return Math.abs(DAYS.between(date1, date2));
    }

    public static ZonedDateTime expiryFromTicker(String ticker) {
        String[] tokens = ticker.split(" ");
        String[] monthDayYear = tokens[2].split("/");
        return stringToDate(monthDayYear[2] + monthDayYear[0] + monthDayYear[1]);
    }


}
