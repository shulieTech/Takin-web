package io.shulie.takin.web.config.sync.file.impl;

import io.shulie.takin.web.config.sync.api.SwitchSyncService;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
@Component
public class SwitchSyncServiceImpl implements SwitchSyncService {

    @Override
    public void turnClusterTestSwitch(String namespace, boolean on) {
        // TODO 写到redis，不写文件
    }

    @Override
    public void turnAllowListSwitch(String namespace, boolean on) {
        // TODO 写到redis，不写文件
    }
}
