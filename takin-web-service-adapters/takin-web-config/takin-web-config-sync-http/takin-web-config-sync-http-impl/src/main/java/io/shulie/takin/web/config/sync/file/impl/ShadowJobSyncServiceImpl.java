package io.shulie.takin.web.config.sync.file.impl;

import java.util.List;

import io.shulie.takin.web.config.entity.ShadowJob;
import io.shulie.takin.web.config.sync.api.ShadowJobSyncService;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
@Component
public class ShadowJobSyncServiceImpl implements ShadowJobSyncService {

    @Override
    public void syncShadowJob(TenantCommonExt commonExt, String applicationName, List<ShadowJob> shadowJobs) {
        // TODO 写到redis，不写文件
    }
}
