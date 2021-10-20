package io.shulie.takin.web.biz.service.dsManage;

import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInputV2;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInputV2;

/**
 * @Author: 南风
 * @Date: 2021/9/2 5:45 下午
 */
public abstract class AbstractShaDowManageService {

    /**
     * 更新影子配置方案
     * @param inputV2
     */
    public abstract void updateShadowProgramme(ApplicationDsUpdateInputV2 inputV2);

    /**
     * 创建影子配置方案
     * @param inputV2
     */
    public abstract void createShadowProgramme(ApplicationDsCreateInputV2 inputV2);

}
