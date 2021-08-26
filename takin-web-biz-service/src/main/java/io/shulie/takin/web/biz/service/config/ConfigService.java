package io.shulie.takin.web.biz.service.config;

/**
 * @author shiyajian
 * create: 2020-09-18
 */
public interface ConfigService {

    void updateClusterTestSwitch(String userAppKey, Boolean value);

    boolean getClusterTestSwitch(String userAppKey);

    void updateAllowListSwitch(String userAppKey, Boolean value);

    boolean getAllowListSwitch(String userAppKey);
}
