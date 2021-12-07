package io.shulie.takin.web.config.sync.file.impl;

import java.util.List;

import io.shulie.takin.web.config.entity.ShadowDB;
import io.shulie.takin.web.config.sync.api.ShadowDbSyncService;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
@Component
public class ShadowDbSyncServiceImpl implements ShadowDbSyncService {

    @Override
    public void syncShadowDataBase(TenantCommonExt commonExt, String applicationName, List<ShadowDB> shadowDataBases) {
        //TODO 写到redis，不写到文件
    }
}
