package lx.af.utils;

import java.util.Arrays;

/**
 *
 * author: liuxu
 * date: 2015-03-05
 *
 * 1.   get a formatted time length from millisecond, like xxx millisecond will
 *      be formatted as "2 day, 12 hour, 35 minute, 17 second". use get() method
 *      to get the four fields.
 * 2.   get millisecond from a specified time: "2 day, 12 hour, 35 minute, 17 second"
 *      can be calculated to millisecond
 * 3.   add and subtract operation
 *
 */
public class Time {

    /**
     * field for second
     */
    public static final int SECOND = 0;

    /**
     * field for minute
     */
    public static final int MINUTE = 1;

    /**
     * field for hour
     */
    public static final int HOUR = 2;

    /**
     * field for day
     */
    public static final int DAY = 3;

    // millisecond for one second
    private static final long S = 1000;
    // millisecond for one minute
    private static final long M = 60 * S;
    // millisecond for one hour
    private static final long H = 60 * M;
    // millisecond for one day
    private static final long D = 24 * H;

    // max value for fields
    private final int[] maxFields = { 59, 59, 23, Integer.MAX_VALUE - 1 };

    // min value for fields
    private final int[] minFields = { 0, 0, 0, Integer.MIN_VALUE };

    // fields
    private int[] fields = new int[4];

    /**
     * use millisecond to construct a Time
     * @param millis millisecond
     */
    public Time(long millis) {
        int day, hour, minute, second;
        long t = millis;

        second = (int) ((t % M) / S);
        t -= second * S;
        if (t <= 0) {
            initialize(0, 0, 0, second);
            return;
        }

        minute = (int) ((t % H) / M);
        t -= minute * M;
        if (t <= 0) {
            initialize(0, 0, minute, second);
            return;
        }

        hour = (int) ((t % D) / H);
        t -= hour * H;
        if (t <= 0) {
            initialize(0, hour, minute, second);
            return;
        }

        day = (int) (t / D);
        initialize(day, hour, minute, second);
    }

    /**
     * construct Time as 0 day, 0 hour, 0 minute, 0 second
     */
    public Time() {
        this(0, 0, 0, 0);
    }

    /**
     * construct Time
     */
    public Time(int day, int hour, int minute, int second) {
        initialize(day, hour, minute, second);
    }

    /**
     * get millisecond represented by this Time
     * @return
     */
    public long getMillis() {
        return fields[DAY] * D + fields[HOUR] * H + fields[MINUTE] * M + fields[SECOND] * S;
    }

    /**
     * set time field.
     * value exceed max will be rolled: 62 second will add 1 to minute and set second to 2.
     * negative value will throw IllegalArgumentException.
     * @param field can be DAY, HOUR, MINUTE, SECOND
     * @param value value
     */
    public void set(int field, int value) {
        if(value < minFields[field]) {
            throw new IllegalArgumentException(value + ", time value must be positive.");
        }
        fields[field] = value % (maxFields[field] + 1);
        // roll time
        int carry = value / (maxFields[field] + 1);
        if(carry > 0) {
            int upFieldValue = get(field + 1);
            set(field + 1, upFieldValue + carry);
        }
    }


    /**
     * get time field value
     * @param field can be DAY, HOUR, MINUTE, SECOND
     * @return field value
     */
    public int get(int field) {
        if(field < 0 || field > fields.length - 1) {
            throw new IllegalArgumentException(field + ", field value is error.");
        }
        return fields[field];
    }

    /**
     * calculate add
     * @param time Time to be added
     */
    public Time addTime(Time time) {
        Time result = new Time();
        int up = 0;
        for (int i = 0; i < fields.length; i++) {
            int sum = fields[i] + time.fields[i] + up;
            up = sum / (maxFields[i] + 1);
            result.fields[i] = sum % (maxFields[i] + 1);
        }
        return result;
    }

    /**
     * calculate subtract
     * @param time Time to be subtracted
     */
    public Time subtractTime(Time time) {
        Time result = new Time();
        int down = 0;       // 退位标志
        for (int i = 0, k = fields.length - 1; i < k; i++) {
            int difference = fields[i] + down;
            if (difference >= time.fields[i]) {
                difference -= time.fields[i];
                down = 0;
            } else {
                difference += maxFields[i] + 1 - time.fields[i];
                down = -1;
            }
            result.fields[i] = difference;
        }
        result.fields[DAY] = fields[DAY] - time.fields[DAY] + down;
        return result;
    }

    private void initialize(int day, int hour, int minute, int second) {
        set(DAY, day);
        set(HOUR, hour);
        set(MINUTE, minute);
        set(SECOND, second);
    }

    private StringBuilder formatString(StringBuilder sb, int field) {
        if(fields[field] < 10) {
            sb.append('0');
        }
        return sb.append(fields[field]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(16);
        sb.append(fields[DAY]).append(',').append(' ');
        formatString(sb, HOUR).append(":");
        formatString(sb, MINUTE).append(":");
        formatString(sb, SECOND);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + Arrays.hashCode(fields);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Time other = (Time) obj;
        if (!Arrays.equals(fields, other.fields)) {
            return false;
        }
        return true;
    }
}
