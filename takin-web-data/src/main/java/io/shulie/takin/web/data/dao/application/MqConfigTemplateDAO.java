package io.shulie.takin.web.data.dao.application;


import io.shulie.takin.web.data.result.application.MqConfigTemplateDetailResult;

import java.util.List;

/**
 * MQ配置模版表(MqConfigTemplate)表数据库 dao 层
 *
 * @author 南风
 * @date 2021-08-31 15:34:04
 */
public interface MqConfigTemplateDAO {

    /**
     * 获取支持的mq类型
     * @return
     */
    List<MqConfigTemplateDetailResult> queryList();

    MqConfigTemplateDetailResult queryOne(String engName);
}

