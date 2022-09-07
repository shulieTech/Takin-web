package io.shulie.takin.web.biz.service.pressureresource.common;

import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateDsEntity;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/7 3:56 PM
 */
public class DataSourceUtil {
    /**
     * 数据源唯一
     *
     * @param dsEntity
     * @return
     */
    public static String generateKey(PressureResourceRelateDsEntity dsEntity) {
        return String.format("%d-%s-%s-%d-%s",
                dsEntity.getResourceId(),
                dsEntity.getAppName(),
                dsEntity.getBusinessDatabase(),
                dsEntity.getTenantId(),
                dsEntity.getEnvCode());
    }
}
