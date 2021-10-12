package io.shulie.takin.web.config.sync.api;

import java.util.List;

import io.shulie.takin.web.config.entity.ShadowDB;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;

/**
 * 影子数据库/表
 *
 * @author shiyajian
 * create: 2020-09-01
 */
public interface ShadowDbSyncService {

    void syncShadowDataBase(TenantCommonExt commonExt, String applicationName, List<ShadowDB> shadowDataBases);

}
