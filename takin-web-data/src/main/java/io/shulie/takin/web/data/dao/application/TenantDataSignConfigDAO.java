package io.shulie.takin.web.data.dao.application;

import java.util.List;
import java.util.Map;

/**
 * 租户数据存储签名配置(TenantDataSignConfig)表数据库 dao 层
 *
 * @author 南风
 * @date 2022-05-23 15:46:02
 */
public interface TenantDataSignConfigDAO {

    Map<String, Integer> queryTenantStatus();

    void updateStatus(int status,Long tenantId);

    Integer getStatus(Long tenantId);


    void clearSign(List<Long> tenantId,String envCode);

}

