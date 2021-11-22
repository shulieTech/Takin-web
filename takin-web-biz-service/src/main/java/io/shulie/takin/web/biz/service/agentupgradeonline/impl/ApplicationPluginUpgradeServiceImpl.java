package io.shulie.takin.web.biz.service.agentupgradeonline.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeService;
import io.shulie.takin.web.data.dao.agentupgradeonline.ApplicationPluginUpgradeDAO;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeDetailResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * 应用升级单(ApplicationPluginUpgrade)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:29:42
 */
@Service
public class ApplicationPluginUpgradeServiceImpl implements ApplicationPluginUpgradeService {

    @Resource
    private ApplicationPluginUpgradeDAO upgradeDAO;

    @Override
    public List<ApplicationPluginUpgradeDetailResult> getList(Set<String> upgradeBatchs) {
        if (CollectionUtils.isEmpty(upgradeBatchs)) {
            return Collections.emptyList();
        }
        return upgradeDAO.getList(upgradeBatchs);
    }

    @Override
    public List<ApplicationPluginUpgradeDetailResult> getListByStatus(Integer status) {
        if (Objects.isNull(status)) {
            return Collections.emptyList();
        }
        return null;
    }

    @Override
    public ApplicationPluginUpgradeDetailResult queryLatestUpgradeByAppIdAndStatus(Long applicationId, Integer status) {
        return upgradeDAO.queryLatestUpgradeByAppIdAndStatus(applicationId, status);
    }

    @Override
    public void finishUpgrade(Long appId, String upgradeBatch) {
        upgradeDAO.finishUpgrade(appId, upgradeBatch);
    }

    @Override
    public ApplicationPluginUpgradeDetailResult queryByAppIdAndUpgradeBatch(Long applicationId, String upgradeBatch) {
        return upgradeDAO.queryByAppIdAndUpgradeBatch(applicationId, upgradeBatch);
    }
}
