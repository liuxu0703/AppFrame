package lx.af.utils.UnitFormatters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class TimeFormatter {

    private static final long ONE_MINUTE = 60;
    private static final long ONE_HOUR = 3600;
    private static final long ONE_DAY = 86400;
    private static final long ONE_MONTH = 2592000;
    private static final long ONE_YEAR = 31104000;

    private TimeFormatter() {}

    public static Calendar calendar = Calendar.getInstance();

    /**
     * 距离今天多久,只显示最大单位,如 4小时前, 昨天, 1个月前.
     * @param millis 距离 1970-01-01 毫秒数
     */
    public static String toNowSummary(long millis) {
        long now = new Date().getTime();
        long ago = now - millis;
        if (ago >= 0) {
            return agoSummary(ago);
        } else {
            return afterSummary(ago);
        }
    }

    /**
     * 距离今天多久,只显示最大单位,如 4小时前, 昨天, 1个月前.
     * @param millis 距离 1970-01-01 毫秒数
     */
    public static String toNow(long millis) {
        long now = new Date().getTime();
        long ago = now - millis;
        if (ago >= 0) {
            return ago(ago);
        } else {
            return after(ago);
        }
    }

    /**
     * 将毫秒数转译为已过去的时间,只显示最大单位,如 4小时前, 昨天, 1个月前.
     * @param agoMillis 毫秒数,若为负数则取其绝对值
     */
    public static String agoSummary(long agoMillis) {
        long ago = Math.abs(agoMillis) / 1000;
        if (ago <= ONE_HOUR) {
            long minute = ago / ONE_MINUTE;
            if (minute < 1) {
                return "刚刚";
            } else {
                return minute + "分钟前";
            }
        } else if (ago <= ONE_DAY) {
            return ago / ONE_HOUR + "小时前";
        } else if (ago <= ONE_DAY * 2) {
            return "昨天";
        } else if (ago <= ONE_DAY * 3) {
            return "前天";
        } else if (ago <= ONE_MONTH) {
            long day = ago / ONE_DAY;
            return day + "天前";
        } else if (ago <= ONE_YEAR) {
            long month = ago / ONE_MONTH;
            return month + "个月前";
        } else {
            long year = ago / ONE_YEAR;
            return year + "年前";
        }
    }

    /**
     * 将毫秒数转译为已过去的时间
     * @param agoMillis 毫秒数,若为负数则取其绝对值
     */
    public static String ago(long agoMillis) {
        long ago = Math.abs(agoMillis) / 1000;
        if (ago <= ONE_HOUR) {
            long minute = ago / ONE_MINUTE;
            if (minute < 1) {
                return "刚刚";
            } else {
                return minute + "分钟前";
            }
        } else if (ago <= ONE_DAY) {
            return ago / ONE_HOUR + "小时" + (ago % ONE_HOUR / ONE_MINUTE)
                    + "分钟前";
        } else if (ago <= ONE_DAY * 2) {
            return "昨天" + calendar.get(Calendar.HOUR_OF_DAY) + "点"
                    + calendar.get(Calendar.MINUTE) + "分";
        } else if (ago <= ONE_DAY * 3) {
            return "前天" + calendar.get(Calendar.HOUR_OF_DAY) + "点"
                    + calendar.get(Calendar.MINUTE) + "分";
        } else if (ago <= ONE_MONTH) {
            long day = ago / ONE_DAY;
            return day + "天前" + calendar.get(Calendar.HOUR_OF_DAY) + "点"
                    + calendar.get(Calendar.MINUTE) + "分";
        } else if (ago <= ONE_YEAR) {
            long month = ago / ONE_MONTH;
            long day = ago % ONE_MONTH / ONE_DAY;
            return month + "个月" + day + "天前"
                    + calendar.get(Calendar.HOUR_OF_DAY) + "点"
                    + calendar.get(Calendar.MINUTE) + "分";
        } else {
            long year = ago / ONE_YEAR;
            int month = calendar.get(Calendar.MONTH) + 1;// JANUARY which is 0 so month+1
            return year + "年前" + month + "月" + calendar.get(Calendar.DATE)
                    + "日";
        }
    }

    /**
     * 将毫秒数转译为未来时间,只显示最大单位,如 4小时后, 明天, 1个月后.
     * @param remainMillis 毫秒数,若为负数则取其绝对值
     */
    public static String afterSummary(long remainMillis) {
        long remain = Math.abs(remainMillis) / 1000;
        if (remain <= ONE_HOUR) {
            long minute = remainMillis / ONE_MINUTE;
            if (minute < 1) {
                return "1分钟内";
            } else {
                return minute + "分钟后";
            }
        } else if (remain <= ONE_DAY) {
            return remain / ONE_HOUR + "小时后";
        } else if (remain <= ONE_DAY * 2) {
            return "明天";
        } else if (remain <= ONE_DAY * 3) {
            return "后天";
        } else {
            long day = remain / ONE_DAY;
            long hour = remain % ONE_DAY / ONE_HOUR;
            long minute = remain % ONE_DAY % ONE_HOUR / ONE_MINUTE;
            return day + "天" + hour + "小时" + minute + "分钟后";
        }
    }

    /**
     * 将毫秒数转译为未来时间
     * @param remainMillis 毫秒数,若为负数则取其绝对值
     */
    public static String after(long remainMillis) {
        long remain = Math.abs(remainMillis) / 1000;
        if (remain <= ONE_HOUR) {
            long minute = remainMillis / ONE_MINUTE;
            if (minute < 1) {
                return "1分钟内";
            } else {
                return minute + "分钟后";
            }
        } else if (remain <= ONE_DAY) {
            return remain / ONE_HOUR + "小时"
                    + (remain % ONE_HOUR / ONE_MINUTE) + "分钟后";
        } else {
            long day = remain / ONE_DAY;
            long hour = remain % ONE_DAY / ONE_HOUR;
            long minute = remain % ONE_DAY % ONE_HOUR / ONE_MINUTE;
            return day + "天" + hour + "小时" + minute + "分钟后";
        }
    }

    // ====================================================

    /**
     * @param format 时间格式化字符串,例: "yyyy-MM-dd HH:mm"
     */
    public static String getCurrentTimeString(String format) {
        SimpleDateFormat simple = new SimpleDateFormat(format, Locale.getDefault());
        return simple.format(new Date());
    }

    public static String getCurrentYear() {
        return calendar.get(Calendar.YEAR) + "";
    }

    public static String getCurrentMonth() {
        int month = calendar.get(Calendar.MONTH) + 1;
        return month + "";
    }

    public static String getCurrentDay() {
        return calendar.get(Calendar.DATE) + "";
    }

    public static String getCurrentHour24() {
        return calendar.get(Calendar.HOUR_OF_DAY) + "";
    }

    public static String getCurrentMinute() {
        return calendar.get(Calendar.MINUTE) + "";
    }

    public static String getCurrentSecond() {
        return calendar.get(Calendar.SECOND) + "";
    }

}
