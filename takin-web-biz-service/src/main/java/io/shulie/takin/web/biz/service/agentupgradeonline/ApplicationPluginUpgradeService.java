package io.shulie.takin.web.biz.service.agentupgradeonline;

import java.util.List;
import java.util.Set;

import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeDetailResult;

/**
 * 应用升级单(ApplicationPluginUpgrade)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:29:41
 */
public interface ApplicationPluginUpgradeService {

    List<ApplicationPluginUpgradeDetailResult> getList(Set<String> upgradeBatchs);

    List<ApplicationPluginUpgradeDetailResult> getListByStatus(Integer status);

    /**
     * 根据ApplicationId和状态查询最新升级单
     *
     * @param applicationId 应用Id
     * @param status        状态
     * @return ApplicationPluginUpgradeDetailResult对象
     */
    ApplicationPluginUpgradeDetailResult queryLatestUpgradeByAppIdAndStatus(Long applicationId, Integer status);

    /**
     * 将升级单状态置为完成
     *
     * @param appId        应用Id
     * @param upgradeBatch 升级批次号
     */
    void finishUpgrade(Long appId, String upgradeBatch);

}
