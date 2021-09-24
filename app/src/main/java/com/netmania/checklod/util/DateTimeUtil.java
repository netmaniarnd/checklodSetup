package com.netmania.checklod.util;

import android.text.format.DateFormat;

import com.netmania.checklod.BaseApplication;
import com.netmania.checklod.R;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {

    public static final String RTC_DATE_FORMAT = "yy-MM-dd HH:mm:ss";
    public static final String RTC_DATE_FORMAT_NEW = "yyyy-MM-dd HH:mm:ss";
    public static final String RTC_TIMESTAMP_FORMAT = "yy-MM-dd HH:mm:ss";

    public static String getTimestampToDatetime(long timestamp, String format) {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat(format, Locale.KOREA);
        String date = timeStampFormat.format(new Timestamp(timestamp));
        return date;
    }

    public static String getTimeString(String format) {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat(format, Locale.KOREA);
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return timeStampFormat.format(cal.getTime());
    }

    public static long getStringToTimestamp(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(RTC_TIMESTAMP_FORMAT, Locale.KOREA);
        Date date = sdf.parse(dateStr);
        long timestamp = date.getTime();
        return timestamp / 1000;
    }

    public static long getCurrentTimestamp() throws ParseException {
        return DateTimeUtil.getStringToTimestamp(DateTimeUtil.getTimeString(RTC_DATE_FORMAT_NEW))*1000;
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("yy-MM-dd", cal).toString();
        return date;
    }

    public static String getTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm:ss", cal).toString();
        return date;
    }

    public static String getMilliSecondsToTimeString(long timeInMillies) {
        return String.format(BaseApplication.getInstance().getResources().getString(R.string.time_utils_something),
                TimeUnit.MILLISECONDS.toHours(timeInMillies),
                TimeUnit.MILLISECONDS.toMinutes(timeInMillies),
                TimeUnit.MILLISECONDS.toSeconds(timeInMillies) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillies))
        );
    }

    public static int getMinuteCount(long timeInMillies) {
        return Integer.parseInt(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(timeInMillies)));
    }

    public static String getTimeCode() {
        Date tDate = new Date();
        return Long.toString(tDate.getTime());
    }
}
