package com.dwaynedevelopment.passtimes.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarUtils {

    private static Calendar mCalendar;

    public static String getMonthFromDate(long date) {
        return new SimpleDateFormat("MMM", Locale.US).format(new Date(date));
    }

    public static String getDayFromDate(long date) {
        return new SimpleDateFormat("dd", Locale.US).format(new Date(date));
    }

    public static String getTimeFromDate(long date) {
        return new SimpleDateFormat("EEEE hh:mm aa", Locale.US).format(new Date(date));
    }

    public static String getCurrentTimeAsString(Calendar calendar) {
        Date time = calendar.getTime();

        return new SimpleDateFormat("hh:mm a", Locale.US).format(time);
    }

    public static Date setTime(Calendar calendar, int hours, int minutes) {
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);

        return calendar.getTime();
    }
}
