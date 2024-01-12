package io.shulie.takin.web.biz.constant;

import io.shulie.takin.web.common.common.Separator;
import org.springframework.beans.factory.annotation.Value;

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

    //压测任务列表(正式环境)
    public final static String SCENE_REPORTID_KEY = "scene.report.list";
    //压测任务列表(预发环境)
    public final static String SCENE_REPORTID_KEY_FOR_INNER_PRE = "scene.report.list.inner.pre";

    /**
     * 是否是预发环境
     */
    @Value("${takin.inner.pre:0}")
    private static int isInnerPre;

    /**
     * 压测 应用名列表
     */
    public final static String PTING_APPLICATION_KEY = "pting.application:hmset:%s";

    public static String getReportKey(Long reportId) {
        return getTaskList() + Separator.Separator2.getValue() + reportId;
    }

    public static String getTaskList(){
        final String reportKeyName = isInnerPre == 1 ? WebRedisKeyConstant.SCENE_REPORTID_KEY_FOR_INNER_PRE
            : WebRedisKeyConstant.SCENE_REPORTID_KEY;
        return reportKeyName;
    }

    /**
     * 应用缓存key 应用ID 租户 环境
     */
    public final static String APPLICATION_CACHE_KEY = "takin:application@%s:%s:%s";
    /**
     * 远程mock数据缓存key 应用ID 租户 环境
     */
    public final static String REMOTE_MOCK_CACHE_KEY = "takin:remote:mock@%s:%s:%s";
    /**
     * 本地mock数据缓存key 应用ID 租户 环境
     */
    public final static String LOCAL_MOCK_CACHE_KEY = "takin:local:mock@%s:%s:%s";
}
