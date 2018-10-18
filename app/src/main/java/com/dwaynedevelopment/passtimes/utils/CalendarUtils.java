package com.dwaynedevelopment.passtimes.utils;

import java.lang.invoke.MutableCallSite;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

public class CalendarUtils {

    private static Calendar mCalendar;

    // Get current calendar passed from where is invoked
    // It needs to get Calendar as parameter to get the
    public static String getCurrentTimeAsString(Calendar calendar) {
        Date time = calendar.getTime();

        return new SimpleDateFormat("hh:mm a", Locale.US).format(time);
    }

    public static Calendar setTime(int hours, int minutes) {
        mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, hours);
        mCalendar.set(Calendar.MINUTE, minutes);

        return mCalendar;
    }
}
