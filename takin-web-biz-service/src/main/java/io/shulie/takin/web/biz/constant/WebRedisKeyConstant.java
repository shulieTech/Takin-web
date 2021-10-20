package io.shulie.takin.web.biz.constant;

/**
 * redis key
 *
 * @author qianshui
 * @date 2020/11/4 下午3:54
 */
public class WebRedisKeyConstant {
    //性能分析 线程、内存数据
    public static final String CACHE_PERFOMANCE_BASE_DATA_KEY = "WEB#PERFOMANCE_BASE_DATA";

    //报告告警set数据
    public final static String REPORT_WARN_PREFIX = "report:warn:";

    //压测中报告id
    public final static String PTING_REPORTID_KEY = "pting.reportid:set";

    /**
     * 压测 应用名列表
     */
    public final static String PTING_APPLICATION_KEY = "pting.application:hmset:%s";
}
