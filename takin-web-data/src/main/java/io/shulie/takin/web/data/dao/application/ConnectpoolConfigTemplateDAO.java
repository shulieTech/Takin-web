package io.shulie.takin.web.data.dao.application;

import io.shulie.takin.web.data.result.application.ConnectpoolConfigTemplateDetailResult;

/**
 * 连接池配置模版表(ConnectpoolConfigTemplate)表数据库 dao 层
 *
 * @author 南风
 * @date 2021-08-30 10:31:04
 */
public interface ConnectpoolConfigTemplateDAO {

    ConnectpoolConfigTemplateDetailResult queryOne(String middlewareType, String engName);

}

