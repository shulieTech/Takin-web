package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.pamirs.attach.plugin.dynamic.one.Type;
import io.shulie.takin.web.data.model.mysql.pressureresource.RelateDsEntity;
import io.shulie.takin.web.data.result.application.ApplicationDsDbManageDetailResult;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/10/26 3:48 PM
 */
public class RelateConvert {
    /**
     * 应用详情关联数据源转换为压测资源准备数据源信息
     *
     * @param result
     * @return
     */
    public static RelateDsEntity dsManageConvertRelateDs(ApplicationDsDbManageDetailResult result) {
        final RelateDsEntity dsEntity = new RelateDsEntity();
        dsEntity.setAppName(result.getApplicationName());
        dsEntity.setBusinessUserName(result.getUserName());
        dsEntity.setBusinessDatabase(result.getUrl());
        dsEntity.setMiddlewareName(result.getConnPoolName());
        dsEntity.setMiddlewareType(Type.MiddleWareType.LINK_POOL.value());
        return dsEntity;
    }
}
