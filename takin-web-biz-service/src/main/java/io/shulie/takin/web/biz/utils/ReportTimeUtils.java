package io.shulie.takin.web.biz.utils;

public class ReportTimeUtils {

    /**
     * 处理压测报告时间，开始时间提前2分钟，结束时间延后5分钟
     * @param startTime
     * @return
     */
    public static Long beforeStartTime(Long startTime) {
        return startTime - 2 * 60 * 1000;
    }

    public static Long afterEndTime(Long endTime) {
        return endTime + 5 * 60 * 1000;
    }
}
