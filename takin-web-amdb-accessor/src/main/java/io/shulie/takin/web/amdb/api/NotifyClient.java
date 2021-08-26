package io.shulie.takin.web.amdb.api;

/**
 * @author shiyajian
 * create: 2020-12-29
 */
public interface NotifyClient {

    /**
     * 通知AMDB开始计算入口
     * @return
     */
    boolean startApplicationEntrancesCalculate(String applicationName, String serviceName,String method,
        String type,String extend);

    /**
     * 通知AMDB结束计算入口
     * @return
     */
    boolean stopApplicationEntrancesCalculate(String applicationName, String serviceName,String method,
        String type,String extend);
}
