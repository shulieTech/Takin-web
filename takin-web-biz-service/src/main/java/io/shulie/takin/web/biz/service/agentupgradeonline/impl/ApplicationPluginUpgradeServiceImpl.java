package io.shulie.takin.web.biz.service.agentupgradeonline.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

import com.google.common.base.Splitter;
import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeRefService;
import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeService;
import io.shulie.takin.web.biz.utils.fastagentaccess.AgentVersionUtil;
import io.shulie.takin.web.data.dao.agentupgradeonline.ApplicationPluginUpgradeDAO;
import io.shulie.takin.web.data.param.agentupgradeonline.CreateApplicationPluginUpgradeParam;
import io.shulie.takin.web.data.param.agentupgradeonline.CreateApplicationPluginUpgradeRefParam;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeDetailResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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

    @Resource
    private ApplicationPluginUpgradeRefService applicationPluginUpgradeRefService;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUpgradeOrder(CreateApplicationPluginUpgradeParam param) {
        // 处理升级明细
        List<CreateApplicationPluginUpgradeRefParam> upgradeRefs = dealDependencyInfo(param.getUpgradeBatch(),
            param.getUpgradeContext());
        if (!CollectionUtils.isEmpty(upgradeRefs)) {
            applicationPluginUpgradeRefService.batchCreate(upgradeRefs);
        }
        upgradeDAO.save(param);
    }

    /**
     * 处理依赖数据
     *
     * 示例：module-id=instrument-simulator,module-version=null;module-id=module-aerospike,module-version=null;
     *
     * @param upgradeBatch   升级批次
     * @param dependencyInfo 依赖数据
     * @return CreateApplicationPluginUpgradeRefParam集合
     */
    private List<CreateApplicationPluginUpgradeRefParam> dealDependencyInfo(String upgradeBatch,
        String dependencyInfo) {

        List<CreateApplicationPluginUpgradeRefParam> refParamList = new ArrayList<>();
        // 处理升级明细
        List<String> dependencyList = Splitter.on(";").omitEmptyStrings().trimResults().splitToList(dependencyInfo);
        for (String dependency : dependencyList) {
            String[] items = dependency.split(",");
            if (items.length < 2
                || !items[0].startsWith("module-id=")
                || !items[1].startsWith("module-version=")) {
                continue;
            }
            String pluginVersion = items[1].substring(15);
            CreateApplicationPluginUpgradeRefParam refParam = new CreateApplicationPluginUpgradeRefParam();
            refParam.setUpgradeBatch(upgradeBatch);
            refParam.setPluginName(items[0].substring(10));
            refParam.setPluginVersion(pluginVersion);
            refParam.setPluginVersionNum(AgentVersionUtil.string2Int(pluginVersion));
            refParam.setRemark("agent上报的依赖数据");
            refParamList.add(refParam);
        }
        return refParamList;
    }
}
