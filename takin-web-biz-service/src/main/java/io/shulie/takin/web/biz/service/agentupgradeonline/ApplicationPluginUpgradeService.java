package io.shulie.takin.web.biz.service.agentupgradeonline;

import java.util.List;
import java.util.Set;

import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.ApplicationPluginUpgradeCreateRequest;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.ApplicationPluginUpgradeHistoryDetailResponse;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.ApplicationPluginUpgradeHistoryResponse;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.data.param.agentupgradeonline.CreateApplicationPluginUpgradeParam;
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
     * 变更升级单状态
     *
     * @param appId        应用Id
     * @param upgradeBatch 升级批次号
     * @param status       升级单状态
     * @param errorInfo    错误信息
     */
    void changeUpgradeStatus(Long appId, String upgradeBatch, Integer status, String errorInfo);

    /**
     * 根据ApplicationId和批次号查询升级单,没有已回滚数据
     *
     * @param applicationId 应用Id
     * @param upgradeBatch  升级批次号
     * @return ApplicationPluginUpgradeDetailResult对象
     */
    ApplicationPluginUpgradeDetailResult queryByAppIdAndUpgradeBatch(Long applicationId, String upgradeBatch);

    /**
     * 创建升级单
     *
     * @param param CreateApplicationPluginUpgradeParam对象
     */
    void createUpgradeOrder(CreateApplicationPluginUpgradeParam param);

    Response pluginUpgrade(ApplicationPluginUpgradeCreateRequest createRequest);

    /**
     * 查看升级历史信息
     * @return
     */
    Response<List<ApplicationPluginUpgradeHistoryResponse>> history();

    /**
     * 查看升级历史信息
     * @return
     */
    Response<List<ApplicationPluginUpgradeHistoryDetailResponse>> historyDetail(String upgradeBatch);

    /**
     * 回滚详情
     * @param upgradeBatch
     * @return 应用名集合
     */
    Response<List<String>> rollbackDetail(String upgradeBatch);

    /**
     * 回滚
     * @param upgradeBatch
     * @return 不能回滚的应用名集合
     */
    Response rollback(String upgradeBatch);

}
