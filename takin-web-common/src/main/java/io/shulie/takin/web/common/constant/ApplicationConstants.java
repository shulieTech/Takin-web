package io.shulie.takin.web.common.constant;

/**
 * 应用 常量池
 *
 * @author liuchuan
 * @date 2021/4/27 10:44 上午
 */
public interface ApplicationConstants {

    /**
     * 应用异常列表key
     * 用户id, 环境, 应用名称
     */
    String APPLICATION_ERROR_LIST_KEY = "application:error:%d:%s:%s";

    /**
     * agent 上报错误信息时, map
     */
    String APPLICATION_ERROR_MAP_KEY_ERROR_CODE = "errorCode";

}
