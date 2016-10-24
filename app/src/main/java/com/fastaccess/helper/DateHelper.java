package com.fastaccess.helper;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kosh20111 on 10/7/2015. CopyRights @ Innov8tif
 * <p>
 * Helper Class to deal with time and dates
 */
public class DateHelper {

    public enum DateFormats {
        D_YYMMDD("yy-MM-dd"), D_DDMMyy("dd-MM-yy"),
        D_YYMMDD_N("yy-MMM-dd"), D_DDMMyy_N("dd-MMM-yy"),
        D_YYMMDDHHMMA_N("yy-MMM-dd, hh:mma"), D_DDMMyyHHMMA_N("dd-MMM-yy, hh:mma"),
        S_YYMMDD("yy/MM/dd"), S_DDMMyy("dd/MM/yy"),
        S_YYMMDDHHMMA("yy/MM/dd, hh:mma"), S_DDMMyyHHMMA("dd/MM/yy, hh:mma"),
        S_YYMMDDHHMMA_N("yy/MMM/dd, hh:mma"), S_DDMMyyHHMMA_N("dd/MMM/yy, hh:mma"),
        D_YYYYMMDD("yyyy-MM-dd"), D_DDMMYYYY("dd-MM-yyyy"),
        D_YYYYMMDDHHMMA("yyyy-MM-dd, hh:mma"), D_DDMMYYYYHHMMA("dd-MM-yyyy, hh:mma"),
        D_YYYYMMDD_N("yyyy-MMM-dd"), D_DDMMYYYY_N("dd-MMM-yyyy"),
        D_YYYYMMDDHHMMA_N("yyyy-MMM-dd, hh:mma"), D_DDMMYYYYHHMMA_N("dd-MMM-yyyy, hh:mma"),
        S_YYYYMMDD("yyyy/MM/dd"), S_DDMMYYYY("dd/MM/yyyy"),
        S_YYYYMMDDHHMMA("yyyy/MM/dd, hh:mma"), S_DDMMYYYYHHMMA("dd/MM/yyyy, hh:mma"),
        S_YYYYMMDDHHMMA_N("yyyy/MMM/dd, hh:mma"), S_DDMMYYYYHHMMA_N("dd/MMM/yyyy, hh:mma"),
        D_YYMMDDHHMMSSA_N("yy-MMM-dd, hh:mm:ssa"), D_DDMMyyHHMMSSA_N("dd-MMM-yy, hh:mm:ssa"),
        S_YYMMDDHHMMSSA("yy/MM/dd, hh:mm:ssa"), S_DDMMyyHHMMSSA("dd/MM/yy, hh:mm:ssa"),
        S_YYMMDDHHMMSSA_N("yy/MMM/dd, hh:mm:ssa"), S_DDMMyyHHMMSSA_N("dd/MMM/yy, hh:mm:ssa"),
        D_YYYYMMDDHHMMSSA("yyyy-MM-dd, hh:mm:ssa"), D_DDMMYYYYHHMMSSA("dd-MM-yyyy, hh:mm:ssa"),
        D_YYYYMMDDHHMMSSA_N("yyyy-MMM-dd, hh:mm:ssa"), D_DDMMYYYYHHMMSSA_N("dd-MMM-yyyy, hh:mm:ssa"),
        S_YYYYMMDDHHMMSSA("yyyy/MM/dd, hh:mm:ssa"), S_DDMMYYYYHHMMSSA("dd/MM/yyyy, hh:mm:ssa"),
        S_YYYYMMDDHHMMSSA_N("yyyy/MMM/dd, hh:mm:ssa"), S_DDMMYYYYHHMMSSA_N("dd/MMM/yyyy, hh:mm:ssa"),
        YYMMDDHHMMSS("yyMMddhhmmss"), YYMMDDHHMMSS_24("yyMMddkkmmss"),
        HHMMA("hh:mma"), HHMM("hh:mm"), HHMMSSA("hh:mm:ssa"), HHMMSS("hh:mm:ss"),
        DD("dd"), MM("MM"), MM_N("MMM"), DDMM("dd MM"), DDMM_N("dd MMM"), DDMMYYYY("ddMMyyyy");
        private String dateFormat;

        DateFormats(String dateFormat) {this.dateFormat = dateFormat;}

        public String getDateFormat() {
            return dateFormat;
        }
    }

    /**
     * @return hh:mm a || dd MMM hh:mm a
     */
    public static String prettifyDate(long timestamp) {
        SimpleDateFormat dateFormat;
        if (DateUtils.isToday(timestamp)) {
            dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        } else {
            dateFormat = new SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault());
        }
        return dateFormat.format(timestamp);
    }

    /**
     * @return hh:mm a || dd MMM hh:mm a
     */
    public static String prettifyDate(String timestamp, DateFormats dateFormats) {
        SimpleDateFormat sample = new SimpleDateFormat(dateFormats.getDateFormat(), Locale.getDefault());

        try {
            long time = sample.parse(timestamp).getTime();
            if (DateUtils.isToday(time)) {
                sample = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            } else {
                sample = new SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault());
            }
            return sample.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    /**
     * @return dd/MM/yyyy
     */
    public static long getDateOnly(String date) {
        SimpleDateFormat sample = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            return sample.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @return dd/MM/yyyy
     */
    public static String getDateOnly(long time) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(time);
    }

    /**
     * @return dd/MM/yyyy, hh:mm a
     */
    public static String getDateAndTime(long time) {
        SimpleDateFormat sample = new SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.getDefault());
        return sample.format(new Date(time));
    }

    /**
     * @return dd/MM/yyyy, hh:mm a
     */
    public static String getDateAndTime(String time) {
        SimpleDateFormat sample = new SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.getDefault());
        return sample.format(time);
    }

    /**
     * @return hh:mm a
     */
    public static String getTimeOnly(long time) {
        SimpleDateFormat sample = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sample.format(time);
    }

    /**
     * @return today's date in format (dd/MM/yyyy HH:mm:ss)
     */
    public static String getTodayWithTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    /**
     * @return today's date in format (dd/MM/yyyy)
     */
    public static String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    public static String getYesterday() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(getToday()));
            calendar.add(Calendar.DATE, -1);
            Date tomorrow = calendar.getTime();
            return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(tomorrow);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return tomorrows's date in format (dd/MM/yyyy)
     */
    public static String getTomorrow() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(getToday()));
            calendar.add(Calendar.DATE, 1);
            Date tomorrow = calendar.getTime();
            return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(tomorrow);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * new and old date must equal to {@link DateFormats}
     *
     * @return number of hours
     */
    public static long getDaysBetweenTwoDate(String old, String newDate, DateFormats dateFormats) {
        SimpleDateFormat myFormat = new SimpleDateFormat(dateFormats.getDateFormat(), Locale.getDefault());
        try {
            Date date1 = myFormat.parse(old);
            Date date2 = myFormat.parse(newDate);
            long diff = date1.getTime() - date2.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            return hours / 24;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * new and old date must equal to {@link DateFormats}
     *
     * @return number of hours
     */
    public static long getHoursBetweenTwoDate(String old, String newDate, DateFormats dateFormats) {
        SimpleDateFormat myFormat = new SimpleDateFormat(dateFormats.getDateFormat(), Locale.getDefault());
        try {
            Date date1 = myFormat.parse(old);
            Date date2 = myFormat.parse(newDate);
            long diff = date1.getTime() - date2.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            return hours;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getMinutesBetweenTwoDates(String old, String newDate, DateFormats dateFormats) {
        SimpleDateFormat myFormat = new SimpleDateFormat(dateFormats.getDateFormat(), Locale.getDefault());
        try {
            Date date1 = myFormat.parse(old);
            Date date2 = myFormat.parse(newDate);
            long diff = date1.getTime() - date2.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            return minutes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getMinutesBetweenTwoDates(long old, long newDate) {
        long diff = old - newDate;
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return minutes;
    }

    public static boolean isInFuture(long timestamp) {
        Date date = new Date(timestamp);
        return date.getTime() - new Date().getTime() >= 0;
    }

    /**
     */
    public static long parseAnyDate(String date) {
        long time = 0;
        for (DateFormats formats : DateFormats.values()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(formats.getDateFormat(), Locale.getDefault());
                time = format.parse(date).getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return time;
    }

    public static long parseDate(String date, DateFormats dateFormats) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormats.getDateFormat(), Locale.getDefault());
        try {
            return format.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getDate(String date, DateFormats orginalFormat, DateFormats newFormat) {
        SimpleDateFormat sample = new SimpleDateFormat(orginalFormat.getDateFormat(), Locale.getDefault());
        try {
            long time = sample.parse(date).getTime();
            sample = new SimpleDateFormat(newFormat.getDateFormat(), Locale.getDefault());
            return sample.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getDesiredFormat(DateFormats formats) {
        SimpleDateFormat format = new SimpleDateFormat(formats.getDateFormat(), Locale.getDefault());
        return format.format(new Date());
    }

    public static String getDesiredFormat(DateFormats formats, long date) {
        if (date == 0) return "";
        SimpleDateFormat format = new SimpleDateFormat(formats.getDateFormat(), Locale.getDefault());
        return format.format(date);
    }
}
