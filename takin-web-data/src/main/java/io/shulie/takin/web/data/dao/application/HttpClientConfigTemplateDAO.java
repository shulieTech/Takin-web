package io.shulie.takin.web.data.dao.application;

import io.shulie.takin.web.data.result.application.HttpClientConfigTemplateDetailResult;

import java.util.List;

/**
 * http-client配置模版表(HttpClientConfigTemplate)表数据库 dao 层
 *
 * @author 南风
 * @date 2021-08-25 14:56:54
 */
public interface HttpClientConfigTemplateDAO {

    HttpClientConfigTemplateDetailResult selectTemplate(String typeName);

    List<HttpClientConfigTemplateDetailResult> selectList();

}

