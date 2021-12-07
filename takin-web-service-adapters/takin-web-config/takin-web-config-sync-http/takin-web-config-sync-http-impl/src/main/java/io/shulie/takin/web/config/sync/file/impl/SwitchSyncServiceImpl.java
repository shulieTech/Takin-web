package io.shulie.takin.web.config.sync.file.impl;

import io.shulie.takin.web.config.sync.api.SwitchSyncService;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
@Component
public class SwitchSyncServiceImpl implements SwitchSyncService {

    @Override
    public void turnClusterTestSwitch(TenantCommonExt commonExt, boolean on) {
        // TODO 写到redis，不写文件
    }

    @Override
    public void turnAllowListSwitch(TenantCommonExt commonExt, boolean on) {
        // TODO 写到redis，不写文件
    }
}
