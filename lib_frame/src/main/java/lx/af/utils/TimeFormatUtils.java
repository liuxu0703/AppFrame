package lx.af.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import lx.af.R;

/**
 * author: lx
 * date: 16-3-22
 */
public final class TimeFormatUtils {

    private static final long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);

    private TimeFormatUtils() {}

    public static boolean moreThanOneMinute(long time1, long time2) {
        return Math.abs(time1 - time2) > ONE_MINUTE;
    }

    public static String getDisplayTime(long time) {
        if(time<=0){
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        if (cal.before(getYesterday())) {
            return formatTime(time, "MM-dd HH:mm");
        } else if (cal.before(getToday())) {
            return ResourceUtils.getString(
                    R.string.time_utils_yesterday, formatTime(time, "HH:mm"));
        } else {
            return formatTime(time, "HH:mm");
        }
    }
    public static String getDisplayTime(String timeStr) {
        long time0;
        try {
            time0 = Long.parseLong(timeStr);
        }catch(NumberFormatException nfe){
            return timeStr;
        }
        long time = 1000 * time0;
        if(time<=0){
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        if (cal.before(getYesterday())) {
            return formatTime(time, "MM-dd HH:mm");
        } else if (cal.before(getToday())) {
            return ResourceUtils.getString(
                    R.string.time_utils_yesterday, formatTime(time, "HH:mm"));
        } else {
            return formatTime(time, "HH:mm");
        }
    }

    public static String formatTime(long time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date(time));
    }


    // ==================================


    private static Calendar getToday() {
        Calendar toady = Calendar.getInstance();
        toady.set(Calendar.HOUR_OF_DAY, 0);
        toady.set(Calendar.MINUTE, 0);
        return toady;
    }

    private static Calendar getYesterday() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        return yesterday;
    }

}
