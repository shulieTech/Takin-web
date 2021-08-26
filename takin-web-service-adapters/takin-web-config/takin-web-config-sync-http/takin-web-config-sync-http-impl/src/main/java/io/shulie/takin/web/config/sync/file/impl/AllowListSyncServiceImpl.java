package io.shulie.takin.web.config.sync.file.impl;

import java.util.List;

import io.shulie.takin.web.config.entity.AllowList;
import io.shulie.takin.web.config.sync.api.AllowListSyncService;
import org.springframework.stereotype.Service;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
@Service
public class AllowListSyncServiceImpl implements AllowListSyncService {

    @Override
    public void syncAllowList(String namespace, String applicationName, List<AllowList> allows) {
        // 不写redis，写入到文件
    }
}
