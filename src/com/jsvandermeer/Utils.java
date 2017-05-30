package com.jsvandermeer;

import javax.xml.crypto.Data;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collection;
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
    static final String LIVE_VOL_LOCAL_DIRECTORY = "C:\\Users\\Jacob\\Dropbox\\Code\\milton\\live_vol_files\\";

    static final String HOLIDAY_CAL_LOCAL_FILE_PATH = "C:\\Users\\Jacob\\Dropbox\\Code\\milton\\holidays.csv";

    static final String DATABASE_PATH = "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\green.db";

    static final int REPLICATION_DAY_TOLERANCE = 6;
    static final int VIX_DAYS = 30;

    static final int STRIKE_SCALE = 4;

    static final double FORWARD_RATE = 0.01;
    static final double BOX_RATE = 0.01;

    static final double CALENDAR_MINUTES_IN_YEAR = 525600.0;
    static final double BUSINESS_MINUTES_IN_YEAR = 362880.0;

    static String zonedDateTimeToString(ZonedDateTime date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'HH':'mm':'ss'['VV']'");
        return date.format(dateTimeFormatter);
    }

    static ZonedDateTime stringToZonedDateTime(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'HH':'mm':'ss'['VV']'");
        return ZonedDateTime.parse(date, dateTimeFormatter);
    }

    static String localDateToString(LocalDate date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd");
        return date.format(dateTimeFormatter);
    }

    static LocalDate stringToLocalDate(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd");
        return LocalDate.parse(date, dateTimeFormatter);
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
        return stringToZonedDateTime(monthDayYear[2] + monthDayYear[0] + monthDayYear[1]);
    }

    static LocalDate nextBusinessDay(LocalDate date) {
        LocalDate result = date;
        while (true) {
            result = result.plusDays(1);
            if (isBusinessDay(result)) break;
        }
        return result;
    }

    static boolean isBusinessDay(LocalDate date) {
        DataInterface dataInterface = DataInterface.getInstance();
        Collection<LocalDate> holidays = dataInterface.retrieveHolidays();
        return !(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY ||
                holidays.contains(date));
    }

    static double calendarPeriod(ZonedDateTime startDate, ZonedDateTime endDate) {
        return ChronoUnit.MINUTES.between(startDate, endDate) / CALENDAR_MINUTES_IN_YEAR;
    }


}