package io.shulie.takin.web.config.sync.file.impl;

import java.util.List;

import io.shulie.takin.web.config.entity.Guard;
import io.shulie.takin.web.config.sync.api.GuardSyncService;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
@Component
public class GuardSyncServiceImpl implements GuardSyncService {

    @Override
    public void syncGuard(TenantCommonExt commonExt, String applicationName, List<Guard> newGuards) {
        //TODO 写到redis，不写文件
    }
}
