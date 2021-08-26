package io.shulie.takin.web.biz.service.dashboard;

import io.shulie.takin.web.biz.pojo.response.dashboard.AppPressureSwitchSetResponse;
import io.shulie.takin.web.biz.pojo.response.dashboard.ApplicationSwitchStatusResponse;

public interface ApplicationService {
    /**
     * 获取用户应用开关信息
     *
     * @return 开关信息
     */
    ApplicationSwitchStatusResponse getUserAppSwitchInfo();

    /**
     * 设置用户全局压测开关
     *
     * @param enable 开关状态
     * @return 设置结果
     */
    AppPressureSwitchSetResponse setUserAppPressureSwitch(Boolean enable);
}
