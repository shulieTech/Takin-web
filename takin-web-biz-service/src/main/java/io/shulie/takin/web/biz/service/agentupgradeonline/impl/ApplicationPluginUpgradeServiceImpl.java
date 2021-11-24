package io.shulie.takin.web.biz.service.agentupgradeonline.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.convert.Convert;
import com.google.common.base.Splitter;
import com.pamirs.takin.common.util.MD5Util;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.ApplicationPluginUpgradeCreateAgentInfoRequest;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.ApplicationPluginUpgradeCreateRequest;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.ApplicationPluginUpgradeHistoryDetailResponse;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.ApplicationPluginUpgradeHistoryResponse;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.PluginInfo;
import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeRefService;
import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeService;
import io.shulie.takin.web.biz.utils.agentupgradeonline.AgentPkgUtil;
import io.shulie.takin.web.biz.utils.fastagentaccess.AgentVersionUtil;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentUpgradeEnum;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentUpgradeTypeEnum;
import io.shulie.takin.web.common.enums.agentupgradeonline.PluginTypeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.agentupgradeonline.ApplicationPluginUpgradeDAO;
import io.shulie.takin.web.data.param.agentupgradeonline.CreateApplicationPluginUpgradeParam;
import io.shulie.takin.web.data.param.agentupgradeonline.CreateApplicationPluginUpgradeRefParam;
import io.shulie.takin.web.data.result.agentUpgradeOnline.PluginLibraryDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeRefDetailResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用升级单(ApplicationPluginUpgrade)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:29:42
 */
@Service
public class ApplicationPluginUpgradeServiceImpl implements ApplicationPluginUpgradeService {

    @Value("${data.path}")
    protected String uploadPath;

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
    public void changeUpgradeStatus(Long appId, String upgradeBatch, Integer status, String errorInfo) {
        upgradeDAO.changeUpgradeStatus(appId, upgradeBatch, status, errorInfo);
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
     * <p>
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
            refParamList.add(refParam);
        }
        return refParamList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response pluginUpgrade(ApplicationPluginUpgradeCreateRequest createRequest) {
        Set<PluginInfo> pluginInfoSet = new HashSet<>(createRequest.getUpgradeInfo());
        StringBuilder sb = new StringBuilder();
        pluginInfoSet.forEach(pluginInfo -> {
            sb.append("module-id=");
            sb.append(pluginInfo.getPluginName());
            sb.append(",");
            sb.append("module-version=");
            sb.append(pluginInfo.getVersion());
            sb.append(";");
        });
        String upgradeContext = sb.toString();
        String upgradeBatch = MD5Util.getMD5(upgradeContext);

        //获取升级包下载地址
        String downLoadPath = this.downLoadPath(upgradeBatch, pluginInfoSet);

        Set<ApplicationPluginUpgradeCreateAgentInfoRequest> appsInfoSet = new HashSet<>(createRequest.getAppsInfo());
        List<CreateApplicationPluginUpgradeParam> upgradeSaveList = appsInfoSet.stream().map(apps -> {
            CreateApplicationPluginUpgradeParam upgradeParam = new CreateApplicationPluginUpgradeParam();
            upgradeParam.setApplicationId(apps.getApplicationId());
            upgradeParam.setApplicationName(apps.getApplicationName());
            upgradeParam.setUpgradeBatch(upgradeBatch);
            upgradeParam.setUpgradeContext(upgradeContext);
            upgradeParam.setUpgradeAgentId(apps.getAgentId());
            upgradeParam.setDownloadPath(downLoadPath);
            upgradeParam.setPluginUpgradeStatus(AgentUpgradeEnum.NOT_UPGRADE.getVal());
            upgradeParam.setType(AgentUpgradeTypeEnum.PROACTIVE_UPGRADE.getVal());
            //todo nf 租户&环境
//            upgradeParam.setEnvCode("");
//            upgradeParam.setTenantId(0L);
            return upgradeParam;
        }).collect(Collectors.toList());

        List<CreateApplicationPluginUpgradeRefParam> upgradeRefs = dealDependencyInfo(upgradeBatch, upgradeContext);
        if (!CollectionUtils.isEmpty(upgradeRefs)) {
            applicationPluginUpgradeRefService.batchCreate(upgradeRefs);
        }
        upgradeDAO.batchSave(upgradeSaveList);
        return Response.success();
    }


    private String downLoadPath(String upgradeBatch, Set<PluginInfo> pluginInfoSet) {
        //检查是否存在相同的升级包,如果存在就复用下载地址
        ApplicationPluginUpgradeDetailResult detailResult = upgradeDAO.queryOneByUpgradeBatch(upgradeBatch);
        String downLoadPath;
        if (Objects.nonNull(detailResult)) {
            downLoadPath = detailResult.getDownloadPath();
        } else {
            //获取合并包后的下载地址
            Map<Integer, List<PluginInfo>> type2Info = CollStreamUtil.groupByKey(pluginInfoSet, PluginInfo::getPluginType);
            if (!type2Info.containsKey(PluginTypeEnum.SIMULATOR.getCode())) {
                throw new TakinWebException(TakinWebExceptionEnum.PLUGIN_UPGRADE_VALID_ERROR, "升级单中未找到simulator插件");
            }

            PluginLibraryDetailResult simulator = Convert.convert(PluginLibraryDetailResult.class,
                    type2Info.get(PluginTypeEnum.SIMULATOR.getCode()).get(0));

            List<PluginLibraryDetailResult> middlewareList = CommonUtil.list2list(type2Info.get(PluginTypeEnum.MIDDLEWARE.getCode()),
                    PluginLibraryDetailResult.class);

            downLoadPath = AgentPkgUtil.aggregation(null, simulator, middlewareList, uploadPath);
        }

        return downLoadPath;
    }


    /**
     * 查看升级历史信息
     *
     * @return
     */
    @Override
    public Response<List<ApplicationPluginUpgradeHistoryResponse>> history() {
        List<ApplicationPluginUpgradeHistoryResponse> history = new ArrayList<>();

        List<ApplicationPluginUpgradeDetailResult> list = upgradeDAO.getList();
        if (CollectionUtils.isEmpty(list)) {
            return Response.success();
        }

        Map<String, List<ApplicationPluginUpgradeDetailResult>> upgradeBatch2Detail = CollStreamUtil.groupByKey(list,
                ApplicationPluginUpgradeDetailResult::getUpgradeBatch);

        upgradeBatch2Detail.forEach((upgradeBatch, details) -> {
            Set<Long> ids = details.stream().map(ApplicationPluginUpgradeDetailResult::getId).collect(Collectors.toSet());
            Map<Integer, List<ApplicationPluginUpgradeDetailResult>> map = CollStreamUtil.groupByKey(details,
                    ApplicationPluginUpgradeDetailResult::getPluginUpgradeStatus);
            /*
             * 同一批次升级单中
             * 升级失败:只要有一个升级失败，就判定为失败状态
             * 升级中:在没有失败的情况下，只要有一个未升级，就判定为升级中
             * 已回滚: 只要全部回滚，才判定为已回滚
             */
            int status;
            if (map.containsKey(AgentUpgradeEnum.UPGRADE_FILE.getVal())) {
                status = AgentUpgradeEnum.UPGRADE_FILE.getVal();
            } else if (map.containsKey(AgentUpgradeEnum.NOT_UPGRADE.getVal())) {
                status = AgentUpgradeEnum.UPGRADING.getVal();
            } else if (!map.containsKey(AgentUpgradeEnum.ROLLBACK.getVal())) {
                status = AgentUpgradeEnum.UPGRADE_SUCCESS.getVal();
            } else {
                status = AgentUpgradeEnum.ROLLBACK.getVal();
            }
            ApplicationPluginUpgradeHistoryResponse response = new ApplicationPluginUpgradeHistoryResponse(upgradeBatch,
                    status, ids.size());

            if (AgentUpgradeEnum.UPGRADE_FILE.getVal() == status) {
                //补充失败详情
                List<ApplicationPluginUpgradeDetailResult> errorLists = map.get(status);
                List<ApplicationPluginUpgradeHistoryResponse.UpgradeErrorInfo> errorDetails = errorLists
                        .stream()
                        .map(detail ->
                                new ApplicationPluginUpgradeHistoryResponse.UpgradeErrorInfo(
                                        detail.getApplicationName(),
                                        detail.getErrorInfo()))
                        .collect(Collectors.toList());
                response.setErrorDetails(errorDetails);
            }
            history.add(response);
        });
        return Response.success(history);
    }

    /**
     * 查看升级历史信息
     *
     * @param upgradeBatch
     * @return
     */
    @Override
    public Response<List<ApplicationPluginUpgradeHistoryDetailResponse>> historyDetail(String upgradeBatch) {
        List<ApplicationPluginUpgradeRefDetailResult> list = applicationPluginUpgradeRefService.getList(upgradeBatch);
        if (CollectionUtils.isEmpty(list)) {
            return Response.success();
        }

        List<ApplicationPluginUpgradeHistoryDetailResponse> collect = list
                .stream()
                .map(detail ->
                        new ApplicationPluginUpgradeHistoryDetailResponse(detail.getPluginName(), detail.getPluginVersion()))
                .collect(Collectors.toList());
        return Response.success(collect);
    }

    /**
     * 回滚详情
     *
     * @param upgradeBatch
     * @return 应用名集合
     */
    @Override
    public Response<List<String>> rollbackDetail(String upgradeBatch) {
        List<ApplicationPluginUpgradeDetailResult> list = upgradeDAO.getList(upgradeBatch);
        return Response.success(CollStreamUtil.toList(list, ApplicationPluginUpgradeDetailResult::getApplicationName));
    }

    /**
     * 回滚
     *
     * @param upgradeBatch
     */
    @Override
    public Response rollback(String upgradeBatch) {
        List<ApplicationPluginUpgradeDetailResult> list = upgradeDAO.getList(upgradeBatch);
        Map<Long, ApplicationPluginUpgradeDetailResult> appId2Detail = list.stream()
                .collect(Collectors.toMap(ApplicationPluginUpgradeDetailResult::getApplicationId, Function.identity()));


        List<String> errorAppList = new ArrayList<>();
        appId2Detail.forEach((appId, detail) -> {
            List<ApplicationPluginUpgradeDetailResult> upgradeDetails = upgradeDAO.getListByUpgradeBatchAndAppIdGtId(upgradeBatch,
                    appId, detail.getId());
            if (!CollectionUtils.isEmpty(upgradeDetails)) {
                errorAppList.add(detail.getApplicationName());
            }
        });
        if (!CollectionUtils.isEmpty(errorAppList)) {
            //有不能回滚的应用，回滚失败
            return Response.fail("回滚失败", errorAppList);
        }
        //执行回滚
        upgradeDAO.batchRollBack(CollStreamUtil.toList(list, ApplicationPluginUpgradeDetailResult::getId));
        return Response.success();
    }
}
