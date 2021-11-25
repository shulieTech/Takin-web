package io.shulie.takin.web.biz.service.agentupgradeonline.impl;

import io.shulie.takin.web.biz.service.agentupgradeonline.WebApplicationPluginUpgradeService;
import io.shulie.takin.web.data.dao.agentupgradeonline.WebApplicationPluginUpgradeDAO;
import io.shulie.takin.web.data.result.agentUpgradeOnline.ApplicationPluginUpgradeDetailResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 应用升级单(ApplicationPluginUpgrade)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:29:42
 */
@Service
public class WebApplicationPluginUpgradeServiceImpl implements WebApplicationPluginUpgradeService {

    @Autowired
    private WebApplicationPluginUpgradeDAO upgradeDAO;

    @Override
    public ApplicationPluginUpgradeDetailResult queryLatestUpgradeByAppIdAndStatus(Long applicationId, Integer status) {
        return upgradeDAO.queryLatestUpgradeByAppIdAndStatus(applicationId, status);
    }
}
