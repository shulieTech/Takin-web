package io.shulie.takin.web.data.dao.application;

import io.shulie.takin.web.data.result.application.AppRemoteCallTemplateMappingDetailResult;

/**
 * 远程调用接口类型与模板映射(AppRemoteCallTemplateMapping)表数据库 dao 层
 *
 * @author 南风
 * @date 2021-09-09 14:44:41
 */
public interface AppRemoteCallTemplateMappingDAO {


    AppRemoteCallTemplateMappingDetailResult getOneByInterfacetype(String interfacetype);

}

