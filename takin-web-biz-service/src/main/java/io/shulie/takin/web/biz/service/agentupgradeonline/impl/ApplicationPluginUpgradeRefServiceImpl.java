package io.shulie.takin.web.biz.service.agentupgradeonline.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeRefService;
import io.shulie.takin.web.data.dao.agentupgradeonline.ApplicationPluginUpgradeRefDAO;
import io.shulie.takin.web.data.param.agentupgradeonline.CreateApplicationPluginUpgradeRefParam;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeRefDetailResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 应用升级批次明细(ApplicationPluginUpgradeRef)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:31:15
 */
@Service
public class ApplicationPluginUpgradeRefServiceImpl implements ApplicationPluginUpgradeRefService {

    @Autowired
    private ApplicationPluginUpgradeRefDAO refDAO;

    @Override
    public List<ApplicationPluginUpgradeRefDetailResult> getList(Long pluginId) {
        if (Objects.isNull(pluginId)) {
            return Collections.emptyList();
        }
        return refDAO.getList(pluginId);
    }

    @Override
    public List<ApplicationPluginUpgradeRefDetailResult> getList(String upgradeBatch) {
        if (StringUtils.isBlank(upgradeBatch)) {
            return Collections.emptyList();
        }
        return null;
    }

    @Override
    public List<ApplicationPluginUpgradeRefDetailResult> getList(List<String> upgradeBatchs) {
        return null;
    }

    @Override
    public void batchCreate(List<CreateApplicationPluginUpgradeRefParam> upgradeRefs) {
        refDAO.batchCreate(upgradeRefs);
    }
}
