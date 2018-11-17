package com.dwaynedevelopment.passtimes.utils;

import com.dwaynedevelopment.passtimes.models.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarUtils {

    private static Calendar mCalendar;

    public static String getMonthFromDate(long date) {
        return new SimpleDateFormat("MMM", Locale.US).format(new Date(date));
    }

    public static String getDayFromDate(long date) {
        return new SimpleDateFormat("dd", Locale.US).format(new Date(date));
    }

    public static String getDateTimeFromDate(long date) {
        return new SimpleDateFormat("EEEE hh:mm aa", Locale.US).format(new Date(date));
    }

    public static String getTimeFromDate(long date) {
        return new SimpleDateFormat("hh:mm aa", Locale.US).format(new Date(date));
    }

    public static String getCurrentTimeAsString(Calendar calendar) {
        Date time = calendar.getTime();

        return new SimpleDateFormat("hh:mm a", Locale.US).format(time);
    }

    public static Calendar convertTimeWithTimeZome(long time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(time);
        return cal;
    }

    public static Calendar setDate(Calendar calendar, int year, int month, int day) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        return calendar;
    }

    public static Date setTime(Calendar calendar, int hours, int minutes) {
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);

        return calendar.getTime();
    }

    public static String timeRangeString(Event event) {
        String startTime = CalendarUtils.getTimeFromDate(event.getStartDate());
        String endTime = CalendarUtils.getTimeFromDate(event.getEndDate());
        return startTime + " - " + endTime;
    }
}
