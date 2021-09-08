package io.shulie.takin.web.data.dao.application;

import io.shulie.takin.web.data.result.application.CacheConfigTemplateDetailResult;

/**
 * 缓存配置模版表(CacheConfigTemplate)表数据库 dao 层
 *
 * @author 南风
 * @date 2021-08-30 10:28:56
 */
public interface CacheConfigTemplateDAO {

    CacheConfigTemplateDetailResult queryOne(String middlewareType, String engName);
}

