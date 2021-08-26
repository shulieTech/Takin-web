package com.pamirs.takin.common.zk;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @author vernon
 * @date 2019/12/3 16:59
 */
public class FormatUtils {
    private static final long[] TIME_SPANS_MILLIS = {
        TimeUnit.DAYS.toMillis(1), TimeUnit.HOURS.toMillis(1),
        TimeUnit.MINUTES.toMillis(1), TimeUnit.SECONDS.toMillis(1),
        1
    };
    private static final String[] TIME_SPANS_TEXT = {
        "d", "h", "m", "s", "ms"
    };

    /**
     * 将时间表示成适合阅读的表示法。为简明起见，只显示最大的两个时间单位。
     *
     * @param millis -
     * @return -
     */
    public static String humanReadableTimeSpan(long millis) {
        return humanReadableTimeSpan(millis, 2, TIME_SPANS_TEXT);
    }

    /**
     * 将时间表示成适合阅读的表示法。为简明起见，只显示最大的 maxTimeUnit 个时间单位。
     *
     * @param millis      -
     * @param maxTimeUnit -
     * @param textLabels  -
     * @return -
     */
    private static String humanReadableTimeSpan(long millis, int maxTimeUnit, String[] textLabels) {
        boolean negative = millis < 0;
        long fix = (millis == Long.MIN_VALUE) ? Long.MAX_VALUE : Math.abs(millis);
        int appendUnit = 0;
        StringBuilder appender = new StringBuilder();
        if (negative) {
            appender.append('-');
        }
        final int l = TIME_SPANS_MILLIS.length;
        for (int i = 0; i < l && fix > 0; ++i) {
            final long span = TIME_SPANS_MILLIS[i];
            if (fix >= span) {
                long unit = fix / span;
                fix %= span;
                appender.append(unit).append(textLabels[i]);
                if (++appendUnit >= maxTimeUnit) {
                    return appender.toString();
                }
            }
        }
        if (appender.length() == 0) {
            appender.append(fix).append(textLabels[l - 1]);
        }
        return appender.toString();
    }

    public static String join(int[] array, String delim) {
        if (array == null || array.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(array[0]);
        for (int i = 1; i < array.length; ++i) {
            builder.append(delim).append(array[i]);
        }
        return builder.toString();
    }

    public static String join(long[] array, long append, String delim) {
        if (array == null || array.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(array[0]);
        for (int i = 1; i < array.length; ++i) {
            builder.append(delim).append(array[i]);
        }
        builder.append(delim).append(append);
        return builder.toString();
    }

    public static String join(long[] array, String delim) {
        if (array == null || array.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(array[0]);
        for (int i = 1; i < array.length; ++i) {
            builder.append(delim).append(array[i]);
        }
        return builder.toString();
    }

    public static String join(Iterator<?> it, String delim) {
        if (it == null || !it.hasNext()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(it.next().toString());
        while (it.hasNext()) {
            builder.append(delim).append(it.next().toString());
        }
        return builder.toString();
    }
}
