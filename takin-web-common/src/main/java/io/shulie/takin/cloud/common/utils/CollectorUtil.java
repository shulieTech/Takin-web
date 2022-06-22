package io.shulie.takin.cloud.common.utils;

import java.util.Calendar;

import io.shulie.takin.cloud.common.constants.CollectorConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-20 20:38
 */
public class CollectorUtil {

    /**
     * 组装时间戳 +  podNum
     *
     * @param timestamp 时间戳
     * @param podNum    pod数量
     * @return -
     */
    public static String getTimestampPodNum(Long timestamp, String podNum) {
        return timestamp + (StringUtils.isNotBlank(podNum) ? podNum : "null");
    }

    /**
     * 时间窗口格式化
     * 取值 5秒以内
     * >0 - <=5 取值 秒：5
     * >5 - <=10 取值 秒：10
     * >55 - <=60 取值 秒：0   分钟：+1
     *
     * @param timestamp 时间戳
     * @return -
     */
    public static Calendar getTimeWindow(long timestamp) {
        int nowSecond = 0;
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(timestamp);

        int second = instance.get(Calendar.SECOND);
        int millSecond = instance.get(Calendar.MILLISECOND);
        if (millSecond > 0){
            second = second + 1;
        }
        for (int time : CollectorConstants.timeWindow) {
            if (second <= time) {
                nowSecond = time;
                break;
            }
        }
        instance.set(Calendar.MILLISECOND, 0);
        if (CollectorConstants.SECOND_60 == nowSecond) {
            instance.set(Calendar.SECOND, 0);
            instance.add(Calendar.MINUTE, 1);
        } else {
            instance.set(Calendar.SECOND, nowSecond);
        }
        return instance;
    }

    public static long getTimeWindowTime(long timestamp) {
        return getTimeWindow(timestamp).getTimeInMillis();
    }

    public static long getNextTimeWindow(long timestamp) {
        Calendar instance = getTimeWindow(timestamp);
        instance.add(Calendar.SECOND, CollectorConstants.SEND_TIME);
        return instance.getTimeInMillis();
    }

    /**
     * 获取当前时间的时间窗口，时间窗口在当前时间搓往前推一个窗口
     */
    public static long getNowTimeWindow() {
        Calendar instance = getTimeWindow(System.currentTimeMillis());
        instance.add(Calendar.SECOND, -CollectorConstants.SEND_TIME);
        return instance.getTimeInMillis();
    }

    /**
     * 获取延迟10S的写入窗口格式化时间。
     *
     * @param timestamp 时间戳
     * @return -
     */
    public static long getPushWindowTime(long timestamp) {
        Calendar instance = getTimeWindow(timestamp);
        instance.add(Calendar.SECOND, -10);
        return instance.getTimeInMillis();
    }

    /**
     * 获取5S后的写入窗口格式化时间。
     *
     * @param timestamp 时间戳
     * @return -
     */
    public static long addWindowTime(long timestamp) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(timestamp);
        instance.add(Calendar.SECOND, CollectorConstants.SEND_TIME);
        return instance.getTimeInMillis();
    }

    /**
     * 获取结束时间的写入窗口格式化时间。
     *
     * @param timestamp 时间戳
     * @return -
     */
    public static long getEndWindowTime(long timestamp) {
        Calendar instance = getTimeWindow(timestamp);
        return instance.getTimeInMillis();
    }
}
