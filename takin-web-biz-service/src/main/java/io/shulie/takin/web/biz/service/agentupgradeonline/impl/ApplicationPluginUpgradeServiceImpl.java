package io.shulie.takin.web.biz.service.agentupgradeonline.impl;

import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeService;
import io.shulie.takin.web.data.dao.agentupgradeonline.ApplicationPluginUpgradeDAO;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeDetailResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 应用升级单(ApplicationPluginUpgrade)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:29:42
 */
@Service
public class ApplicationPluginUpgradeServiceImpl implements ApplicationPluginUpgradeService {

    @Autowired
    private ApplicationPluginUpgradeDAO upgradeDAO;

    @Override
    public List<ApplicationPluginUpgradeDetailResult> getList(Set<String> upgradeBatchs) {
        if(CollectionUtils.isEmpty(upgradeBatchs)){
            return Collections.emptyList();
        }
        return upgradeDAO.getList(upgradeBatchs);
    }

    @Override
    public List<ApplicationPluginUpgradeDetailResult> getListByStatus(Integer status) {
        if(Objects.isNull(status)){
            return Collections.emptyList();
        }
        return null;
    }
}
