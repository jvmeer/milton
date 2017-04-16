package com.jsvandermeer;

import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by Jacob on 3/25/2017.
 */
public class Utils {

    private static final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'HH':'mm':'ss'['VV']'");

    static final String DATABASE_PATH = "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\historical_data.db";
    static final String LOCAL_FILES_PATH = "C:\\Users\\Jacob\\Dropbox\\Code\\milton\\live_vol_files\\";

    static final String VIX_TICKER = "VIX";
    static final String SPX_TICKER = "SPX";
    static final String US_MORNING_EXPIRY_TIME = "09:30:00";
    static final String US_AFTERNOON_EXPIRY_TIME = "16:00:00";
    static final String US_TZID = "America/New_York";
    static final int VIX_DAYS = 30;

    static String dateToString(ZonedDateTime date) {
        return date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    static ZonedDateTime stringToDate(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'HH':'mm':'ss'['VV']'");
        return ZonedDateTime.parse(date, dateTimeFormatter);
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
