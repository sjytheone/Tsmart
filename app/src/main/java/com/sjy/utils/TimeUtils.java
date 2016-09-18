package com.sjy.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/10 0010.
 */

public class TimeUtils {
    public static final long HOUR_Millis = 60 * 60 * 1000;
    public static final long HALF_HOUR_Millis = HOUR_Millis / 2;
    public static final long DAY_Millis = 24 * HOUR_Millis;
    public static final long MONTH_Millis = 30 * DAY_Millis;
    public static final long YEAR_Millis = 365 * DAY_Millis;
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    public static final SimpleDateFormat DATE_FORMAT_DATE_HM = new SimpleDateFormat("HH:mm ");

    private TimeUtils() {
        throw new AssertionError();
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }
//
//    @SuppressWarnings("Deprecated")
//    public static String getConciseTime(long timeInMillis, long nowInMillis, Context context) {
//        if (context == null)
//            return "";
//        long diff = nowInMillis - timeInMillis;
//        if (diff >= YEAR_Millis){
//            int year = (int)(diff / YEAR_Millis);
//            return context.getString(R.string.before_year, year);
//        }
//        if (diff >= MONTH_Millis){
//            int month = (int)(diff / MONTH_Millis);
//            return context.getString(R.string.before_month, month);
//        }
//
//        if (diff >= DAY_Millis){
//            int day = (int)(diff / DAY_Millis);
//            return context.getString(R.string.before_day, day);
//        }
//
//        if (diff >= HOUR_Millis){
//            int hour = (int)(diff / HOUR_Millis);
//            return context.getString(R.string.before_hour, hour);
//        }
//
//        if (diff >= HALF_HOUR_Millis){
//            return context.getString(R.string.before_half_hour);
//        }
//        return context.getString(R.string.just_now);
//    }


//    public static String getConciseTime(long timeInMillis, Context context) {
//        return getConciseTime(timeInMillis, getCurrentTimeInLong(), context);
//    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if(day > 0) {
            sb.append(day+"天");
        }
        if(hour > 0) {
            sb.append(hour+"小时");
        }
        if(minute > 0) {
            sb.append(minute+"分");
        }
        if(second > 0) {
            sb.append(second+"秒");
        }
        if(milliSecond > 0) {
            sb.append(milliSecond+"毫秒");
        }
        return sb.toString();
    }
}