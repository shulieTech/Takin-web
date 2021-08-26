package io.shulie.takin.web.config.sync.api;

import java.util.List;

import io.shulie.takin.web.config.entity.ShadowDB;

/**
 * 影子数据库/表
 *
 * @author shiyajian
 * create: 2020-09-01
 */
public interface ShadowDbSyncService {

    void syncShadowDataBase(String namespace, String applicationName, List<ShadowDB> shadowDataBases);

}
