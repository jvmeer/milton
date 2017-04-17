package com.jsvandermeer;

import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by Jacob on 3/25/2017.
 */
public class Utils {

    private static final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'HH':'mm':'ss'['VV']'");

    static enum Underlier {
        SPX("SPX", "SPX Index", "ES1 Index", "America/New_York", "09:30:00", "16:00:00", "16:15:00"),
        VIX("VIX", "VIX Index", "UX1 Index", "America/New_York", "09:30:00", null, "16:15:00");
        final String ticker;
        final String bloombergTicker;
        final String bloombergFutureTicker;
        final String timeZoneId;
        final String primaryExpiryTime;
        final String alternateExpiryTime;
        final String endOfDayTime;
        Underlier(String ticker, String bloombergTicker, String bloombergFutureTicker, String timeZoneId,
                  String primaryExpiryTime, String alternateExpiryTime, String endOfDayTime) {
            this.ticker = ticker;
            this.bloombergTicker = bloombergTicker;
            this.bloombergFutureTicker = bloombergFutureTicker;
            this.timeZoneId = timeZoneId;
            this.primaryExpiryTime = primaryExpiryTime;
            this.alternateExpiryTime = alternateExpiryTime;
            this.endOfDayTime = endOfDayTime;
        }
    }

    static Map<String, Underlier> underlierMap() {
        Map<String, Underlier> underlierMap = new HashMap<>();
        for (Underlier underlier : Underlier.values()) {
            underlierMap.put(underlier.ticker, underlier);
        }
        return underlierMap;
    }



    static final String LIVE_VOL_ADDRESS = "ftp.datashop.livevol.com";
    static final String LIVE_VOL_USERNAME = "jsvmeer@gmail.com";
    static final String LIVE_VOL_PASSWORD = "courageandhonor";
    static final String DATABASE_PATH = "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\historical_data.db";
    static final String LOCAL_FILES_PATH = "C:\\Users\\Jacob\\Dropbox\\Code\\milton\\live_vol_files\\";

    static final int VIX_DAYS = 30;

    static String dateToString(ZonedDateTime date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'HH':'mm':'ss'['VV']'");
        return date.format(dateTimeFormatter);
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
