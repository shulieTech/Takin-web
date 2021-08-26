package io.shulie.takin.web.biz.constant;

/**
 * 脚本调试记录常量池
 *
 * @author liuchuan
 * @date 2021/5/13 3:31 下午
 */
public interface ScriptDebugConstants {

    /**
     * 脚本调试记录下的请求流量明细
     * 类型入参
     * 2 响应失败
     */
    int REQUEST_TYPE_FAILED = 2;

    /**
     * 3 断言失败
     */
    int REQUEST_TYPE_FAILED_ASSERT = 3;

    /**
     * 脚本发布对应的业务活动没有配置漏数校验
     */
    int NO_LEAK_CONFIG = 99;

}
