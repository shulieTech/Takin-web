package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pamirs.attach.plugin.dynamic.one.Type;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import io.shulie.takin.web.data.model.mysql.pressureresource.RelateDsEntity;
import io.shulie.takin.web.data.result.application.ApplicationDsDbManageDetailResult;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/10/26 3:48 PM
 */
public class RelateConvert {
    /**
     * 应用详情关联数据源转换为压测资源准备数据源信息
     *
     * @param convert
     * @return
     */
    public static RelateDsEntity dsManageConvertRelateDs(ApplicationDsDbManageDetailResult convert) {
        final RelateDsEntity dsEntity = new RelateDsEntity();
        String shaDowFileExtedn = convert.getShaDowFileExtedn();
        if (StringUtils.isNotBlank(shaDowFileExtedn) &&
                (DsTypeEnum.SHADOW_REDIS_SERVER.getCode().equals(convert.getDsType())
                        || DsTypeEnum.SHADOW_DB.getCode().equals(convert.getDsType()))) {
            // 获取影子数据源和地址
            JSONObject dataObj = JSON.parseObject(shaDowFileExtedn);
            String shadowUrl = dataObj.getString("shadowUrl");
            String shadowPwd = dataObj.getString("shadowPwd");
            String shadowUserName = dataObj.getString("shadowUserName");
            dsEntity.setShadowDatabase(shadowUrl);
            dsEntity.setShadowUserName(shadowUserName);
            dsEntity.setShadowPassword(shadowPwd);
        }
        dsEntity.setAppName(convert.getApplicationName());
        dsEntity.setBusinessUserName(convert.getUserName());
        dsEntity.setBusinessDatabase(convert.getUrl());
        dsEntity.setMiddlewareName(convert.getConnPoolName());
        dsEntity.setMiddlewareType(Type.MiddleWareType.LINK_POOL.value());
        return dsEntity;
    }
}
