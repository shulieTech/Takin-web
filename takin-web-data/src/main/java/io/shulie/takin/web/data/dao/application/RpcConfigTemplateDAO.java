package io.shulie.takin.web.data.dao.application;

import io.shulie.takin.web.data.result.application.RpcConfigTemplateDetailResult;

import java.util.List;

/**
 * rpc框架配置模版(RpcConfigTemplate)表数据库 dao 层
 *
 * @author 南风
 * @date 2021-08-25 15:00:26
 */
public interface RpcConfigTemplateDAO {

    RpcConfigTemplateDetailResult selectTemplate(String typeName);

    List<RpcConfigTemplateDetailResult> selectList();
}

