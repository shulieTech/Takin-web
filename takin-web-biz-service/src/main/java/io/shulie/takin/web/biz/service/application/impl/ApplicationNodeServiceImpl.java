package io.shulie.takin.web.biz.service.application.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.pamirs.takin.common.util.http.DateUtil;
import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.design.probe.AbstractApplicationNodeProbeState;
import io.shulie.takin.web.biz.design.probe.ApplicationNodeProbeStateFactory;
import io.shulie.takin.web.biz.utils.AgentZkClientUtil;
import io.shulie.takin.web.biz.utils.business.probe.ApplicationNodeProbeUtil;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeDashBoardQueryRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeOperateProbeRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeQueryRequest;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationNodeDashBoardResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationNodeResponse;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationNodeQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeProbeInfoDTO;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.application.ApplicationNodeService;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.constant.LockKeyConstants;
import io.shulie.takin.web.common.constant.ProbeConstants;
import io.shulie.takin.web.common.enums.probe.AmdbProbeStatusEnum;
import io.shulie.takin.web.common.enums.probe.ApplicationNodeProbeOperateEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.pojo.dto.probe.ApplicationNodeProbeOperateDTO;
import io.shulie.takin.web.common.pojo.dto.probe.MatchApplicationNodeProbeStateDTO;
import io.shulie.takin.web.data.dao.ApplicationNodeProbeDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationNodeDAO;
import io.shulie.takin.web.data.param.application.ApplicationNodeQueryParam;
import io.shulie.takin.web.data.param.application.QueryApplicationNodeParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationNodeListResult;
import io.shulie.takin.web.data.result.application.ApplicationNodeProbeResult;
import io.shulie.takin.web.data.result.application.ApplicationNodeResult;
import io.shulie.takin.web.data.result.application.ApplicationResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mubai
 * @date 2020-09-23 19:31
 */
@Service
@Slf4j
public class ApplicationNodeServiceImpl implements ApplicationNodeService, ProbeConstants, AppConstants {

    @Autowired
    private ApplicationClient applicationClient;

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private ApplicationNodeProbeStateFactory applicationNodeProbeStateFactory;

    @Autowired
    private ApplicationNodeProbeDAO applicationNodeProbeDAO;

    @Autowired
    private ApplicationNodeDAO applicationNodeDAO;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private AgentZkClientUtil agentZkClientUtil;

    @Value("${agent.registered.path:/config/log/pradar/client/}")
    private String agentRegisteredPath;

    @Override
    public PagingList<ApplicationNodeResponse> pageNodes(ApplicationNodeQueryRequest request) {
        ApplicationNodeQueryParam queryParam = new ApplicationNodeQueryParam();
        Response<ApplicationVo> applicationVoResponse = applicationService.getApplicationInfo(
            String.valueOf(request.getApplicationId()));
        ApplicationVo applicationVo = applicationVoResponse.getData();
        if (Objects.isNull(applicationVo)) {
            return PagingList.empty();
        }
        queryParam.setCurrent(request.getCurrent());
        queryParam.setPageSize(request.getPageSize());
        queryParam.setApplicationNames(Collections.singletonList(applicationVo.getApplicationName()));
        queryParam.setIp(request.getIp());
        PagingList<ApplicationNodeResult> applicationNodes = applicationNodeDAO.pageNodes(queryParam);
        if (applicationNodes.isEmpty()) {
            return PagingList.empty();
        }
        List<ApplicationNodeResponse> responseNodes = applicationNodes.getList().stream()
            .map(instance -> {
                ApplicationNodeResponse response = new ApplicationNodeResponse();
                BeanUtils.copyProperties(instance, response);
                response.setIp(instance.getNodeIp());

                return response;
            }).sorted(Comparator.comparing(ApplicationNodeResponse::getAgentId))
            .collect(Collectors.toList());
        return PagingList.of(responseNodes, applicationNodes.getTotal());
    }

    @Override
    public PagingList<ApplicationNodeResponse> pageNodesV2(ApplicationNodeQueryRequest request) {
        // 获得应用名称
        String applicationName = applicationService.getApplicationNameByApplicationId(request.getApplicationId());

        // 获得节点分页列表
        PagingList<ApplicationNodeListResult> applicationNodeResultPage =
            this.getApplicationNodeListResultPage(request, applicationName);

        // 获得处理后的节点分页列表
        return this.getApplicationNodeResponseList(applicationName, applicationNodeResultPage);
    }

    /**
     * 获得处理后的节点分页列表
     *
     * @param applicationName           应用名称
     * @param applicationNodeResultPage 节点分页列表
     * @return 处理后的节点分页列表
     */
    private PagingList<ApplicationNodeResponse> getApplicationNodeResponseList(String applicationName,
        PagingList<ApplicationNodeListResult> applicationNodeResultPage) {
        if (applicationNodeResultPage.getList().isEmpty()) {
            return PagingList.of(Collections.emptyList(), applicationNodeResultPage.getTotal());
        }

        // 节点列表
        List<ApplicationNodeListResult> nodeList = applicationNodeResultPage.getList();

        // agentId 映射 节点探针操作的map
        Map<String, ApplicationNodeProbeResult> agentIdAboutApplicationNodeProbeMap =
            this.getAgentIdAboutApplicationNodeProbeMap(applicationName, nodeList);

        // 遍历匹配数据, 要返回的数据
        List<ApplicationNodeResponse> applicationNodeResponseList = nodeList.stream().map(node -> {
            ApplicationNodeResponse applicationNodeResponse = new ApplicationNodeResponse();
            BeanUtils.copyProperties(node, applicationNodeResponse);

            // 基础数据
            applicationNodeResponse.setUpdateTime(
                DateUtil.getYYYYMMDDHHMMSS(applicationNodeResponse.getAgentUpdateTime()));
            applicationNodeResponse.setProcessNo(applicationNodeResponse.getProgressId());
            applicationNodeResponse.setIp(applicationNodeResponse.getIpAddress());

            MatchApplicationNodeProbeStateDTO matchApplicationNodeProbeStateDTO
                = new MatchApplicationNodeProbeStateDTO();
            matchApplicationNodeProbeStateDTO.setAmdbProbeState(applicationNodeResponse.getProbeStatus());
            matchApplicationNodeProbeStateDTO.setProbeOperate(ApplicationNodeProbeOperateEnum.NONE.getCode());

            // 状态相关
            ApplicationNodeProbeResult applicationNodeProbeResult =
                agentIdAboutApplicationNodeProbeMap.get(applicationNodeResponse.getAgentId());
            if (applicationNodeProbeResult != null) {
                matchApplicationNodeProbeStateDTO.setProbeOperate(applicationNodeProbeResult.getOperate());
                matchApplicationNodeProbeStateDTO.setProbeOperateResult(applicationNodeProbeResult.getOperateResult());
            }

            // 探针状态实例
            AbstractApplicationNodeProbeState abstractApplicationNodeProbeState =
                applicationNodeProbeStateFactory.getState(matchApplicationNodeProbeStateDTO);
            if (abstractApplicationNodeProbeState == null) {
                return applicationNodeResponse;
            }

            // 版本号
            applicationNodeResponse
                .setProbeVersion(abstractApplicationNodeProbeState.probeVersion(applicationNodeResponse.getProbeVersion()));

            // 探针状态
            applicationNodeResponse.setProbeStatus(abstractApplicationNodeProbeState.probeState());
            applicationNodeResponse.setProbeStatusDesc(abstractApplicationNodeProbeState.probeStateDesc());

            // 操作结果
            applicationNodeResponse.setOperateStatusDesc(
                abstractApplicationNodeProbeState.operateStateDesc(matchApplicationNodeProbeStateDTO));
            return applicationNodeResponse;
        }).collect(Collectors.toList());

        return PagingList.of(applicationNodeResponseList, applicationNodeResultPage.getTotal());
    }

    /**
     * 获得应用节点分页列表
     *
     * @param request         请求参数
     * @param applicationName 应用名称
     * @return 应用节点分页列表
     */
    private PagingList<ApplicationNodeListResult> getApplicationNodeListResultPage(ApplicationNodeQueryRequest request,
        String applicationName) {
        // 拼接参数, 请求大数据
        QueryApplicationNodeParam queryApplicationNodeParam = new QueryApplicationNodeParam();
        queryApplicationNodeParam.setPageSize(request.getPageSize());
        queryApplicationNodeParam.setCurrent(request.getRealCurrent());

        queryApplicationNodeParam.setAppName(applicationName);
        queryApplicationNodeParam.setIp(request.getIp());

        // 探针状态的转换
        Integer type = request.getType();
        if (type != null) {
            if (INSTALLED_TYPE.equals(type)) {
                queryApplicationNodeParam.setProbeStatus(AmdbProbeStatusEnum.INSTALLED.getCode());
            }

            if (UNINSTALLED_TYPE.equals(type)) {
                queryApplicationNodeParam.setProbeStatus(AmdbProbeStatusEnum.NOT_INSTALLED.getCode());
            }
        }

        return applicationNodeDAO.pageNode(queryApplicationNodeParam);
    }

    /**
     * 根据 amdb 节点列表, 获取节点探针操作的 map
     *
     * @param applicationName 应用名称
     * @param nodeList        节点列表
     * @return agentId 映射 节点探针操作 map
     */
    private Map<String, ApplicationNodeProbeResult> getAgentIdAboutApplicationNodeProbeMap(
        String applicationName, List<ApplicationNodeListResult> nodeList) {
        // 收集 agentId, 查询 探针操作记录
        List<String> agentIds = nodeList.stream().map(ApplicationNodeListResult::getAgentId)
            .collect(Collectors.toList());
        if (agentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 操作记录列表
        List<ApplicationNodeProbeResult> applicationNodeProbeResultList = applicationNodeProbeDAO
            .listByApplicationNameAndAgentIds(applicationName, agentIds);
        if (applicationNodeProbeResultList.isEmpty()) {
            return Collections.emptyMap();
        }

        // map 形式
        return applicationNodeProbeResultList.stream()
            .collect(Collectors.toMap(ApplicationNodeProbeResult::getAgentId, Function.identity(), (v1, v2) -> v2));
    }

    @Override
    public ApplicationNodeResponse getNodeByAgentId(String agentId) {
        ApplicationNodeResult applicationNode = applicationNodeDAO.getNodeByAgentId(agentId);
        ApplicationNodeResponse response = new ApplicationNodeResponse();
        BeanUtils.copyProperties(applicationNode, response);
        return response;
    }

    @Override
    public void deleteZkNode(String appName, String agentId) {
        //
        String path;
        if (agentRegisteredPath.endsWith("/")) {
            path = agentRegisteredPath + appName;
        } else {
            path = agentRegisteredPath + "/" + appName;
        }
        if (StringUtils.isNotBlank(agentId)) {
            // 删除指定的agentId
            agentZkClientUtil.deleteNode(path + "/" + agentId);
        } else {
            String zkString = agentZkClientUtil.getNode(path + "/172.17.0.1-14977");
            log.info(zkString);
        }

    }

    @Override
    public ApplicationNodeDashBoardResponse getApplicationNodeAmount(ApplicationNodeDashBoardQueryRequest request) {
        ApplicationNodeDashBoardResponse response = new ApplicationNodeDashBoardResponse();
        response.setNodeTotalCount(0);
        response.setNodeOnlineCount(0L);
        Response<ApplicationVo> applicationVoResponse = applicationService.getApplicationInfo(
            String.valueOf(request.getApplicationId()));
        ApplicationVo applicationVo = applicationVoResponse.getData();
        if (Objects.isNull(applicationVo.getId())) {
            return response;
        }
        String applicationName = applicationVo.getApplicationName();
        List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(
            Arrays.asList(applicationName));
        response.setNodeTotalCount(applicationVo.getNodeNum());
        if (CollectionUtils.isEmpty(applicationResultList)) {
            response.setErrorMsg("在线节点数与节点总数不一致");
            return response;
        }
        response.setNodeOnlineCount(
            Long.valueOf(applicationResultList.get(0).getInstanceInfo().getInstanceOnlineAmount()));
        if (!response.getNodeOnlineCount().equals(Long.valueOf(response.getNodeTotalCount()))) {
            response.setErrorMsg("在线节点数与节点总数不一致");
            return response;
        }
        ApplicationNodeQueryParam queryParam = new ApplicationNodeQueryParam();
        queryParam.setCurrent(0);
        queryParam.setPageSize(99999);
        queryParam.setApplicationNames(Arrays.asList(applicationVo.getApplicationName()));
        PagingList<ApplicationNodeResult> applicationNodes = applicationNodeDAO.pageNodes(queryParam);
        List<ApplicationNodeResult> applicationNodeResultList = applicationNodes.getList();
        if (CollectionUtils.isNotEmpty(applicationNodeResultList)) {
            Long count = applicationNodeResultList.stream().map(ApplicationNodeResult::getAgentVersion).distinct()
                .count();
            if (count > 1) {
                response.setErrorMsg("agent版本不一致");
            }
        }
        return response;
    }

    @Override
    public ApplicationNodeDashBoardResponse getApplicationNodeInfo(Long applicationId) {
        ApplicationDetailResult application = applicationDAO.getApplicationById(applicationId);
        if (application == null) {
            return new ApplicationNodeDashBoardResponse();
        }

        ApplicationNodeDashBoardResponse response = new ApplicationNodeDashBoardResponse();
        // 总数
        response.setNodeTotalCount(application.getNodeNum());

        ApplicationNodeQueryDTO applicationNodeQueryDTO = new ApplicationNodeQueryDTO();
        applicationNodeQueryDTO.setAppName(application.getApplicationName());
        applicationNodeQueryDTO.setProbeStatus(AmdbProbeStatusEnum.INSTALLED.getCode());
        ApplicationNodeProbeInfoDTO applicationNodeProbeInfo =
            applicationClient.getApplicationNodeProbeInfo(applicationNodeQueryDTO);

        // 提示信息
        List<String> messages = new ArrayList<>(3);

        if (applicationNodeProbeInfo != null) {
            // 在线
            response.setNodeOnlineCount(applicationNodeProbeInfo.getOnlineNodesCount());
            // 已安装探针
            response.setProbeInstalledNodeNum(applicationNodeProbeInfo.getSpecificStatusNodesCount());

            // 探针版本不一致
            List<String> versionList = applicationNodeProbeInfo.getVersionList();
            if (versionList != null && versionList.size() > 1) {
                messages.add("当前探针版本不一致, 请及时处理");
            }
        }

        if (!Long.valueOf(response.getNodeTotalCount()).equals(response.getNodeOnlineCount())) {
            messages.add("在线节点数与配置的应用总节点数不一致");
        }

        if (!Long.valueOf(response.getNodeTotalCount()).equals(response.getProbeInstalledNodeNum())) {
            messages.add("已安装探针节点数与配置的应用总节点数不一致");
        }

        response.setErrorMsg(String.join(SEMICOLON, messages));
        return response;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void operateProbe(ApplicationNodeOperateProbeRequest request) {
        // 当安装, 升级操作时, 需要传探针包记录id
        if (!ApplicationNodeProbeUtil.isUninstallOperate(request.getOperateType())
            && request.getProbeId() == null) {
            throw this.getOperateProbeError("请选择探针包!");
        }

        // 分布式锁, 防止同一个对象重复同一时刻重复操作
        String lockKey = String.format(LockKeyConstants.LOCK_OPERATE_PROBE,
            request.getApplicationId(), request.getAgentId());
        this.isOperateProbeError(!distributedLock.tryLockSecondsTimeUnit(lockKey, 0L, 30L), TOO_FREQUENTLY);

        log.info("探针操作 --> 操作类型: {}", request.getOperateType());

        try {
            log.info("探针操作 --> 查询应用信息");
            // 获得应用名称
            String applicationName = applicationService.getApplicationNameByApplicationId(request.getApplicationId());

            // 获得应用节点
            ApplicationNodeListResult node = this.getApplicationNode(applicationName, request.getAgentId());

            // 操作
            this.operate(request, applicationName, node.getProbeStatus());
        } finally {
            distributedLock.unLockSafely(lockKey);
        }
    }

    /**
     * 节点探针操作
     *
     * @param request         操作入参
     * @param applicationName 应用名称
     * @param amdbProbeStatus amdb 上, 探针状态, @see AmdbProbeStatusEnum
     */
    private void operate(ApplicationNodeOperateProbeRequest request, String applicationName, Integer amdbProbeStatus) {
        // 查询本地是否有该应用, 该节点操作记录
        log.info("探针操作 --> 查询本地节点探针操作记录");
        ApplicationNodeProbeResult applicationNodeProbeResult =
            applicationNodeProbeDAO.getByApplicationNameAndAgentId(applicationName, request.getAgentId());

        MatchApplicationNodeProbeStateDTO matchApplicationNodeProbeStateDTO
            = new MatchApplicationNodeProbeStateDTO();
        matchApplicationNodeProbeStateDTO.setAmdbProbeState(amdbProbeStatus);
        matchApplicationNodeProbeStateDTO.setProbeOperate(ApplicationNodeProbeOperateEnum.NONE.getCode());

        if (applicationNodeProbeResult != null) {
            matchApplicationNodeProbeStateDTO.setProbeOperate(applicationNodeProbeResult.getOperate());
            matchApplicationNodeProbeStateDTO.setProbeOperateResult(applicationNodeProbeResult.getOperateResult());
        }

        // 根据状态获得状态实例, 操作
        log.info("探针操作 --> 匹配");
        AbstractApplicationNodeProbeState abstractApplicationNodeProbeState =
            applicationNodeProbeStateFactory.getState(matchApplicationNodeProbeStateDTO);
        this.isOperateProbeError(abstractApplicationNodeProbeState == null, "探针状态错误!");

        log.info("探针操作 --> 操作");
        ApplicationNodeProbeOperateDTO applicationNodeProbeOperateDTO = new ApplicationNodeProbeOperateDTO();
        BeanUtils.copyProperties(request, applicationNodeProbeOperateDTO);
        applicationNodeProbeOperateDTO.setApplicationName(applicationName);
        abstractApplicationNodeProbeState.handle(applicationNodeProbeOperateDTO);
    }

    /**
     * 获得应用节点
     *
     * @param applicationName 应用名称
     * @param agentId         agentId
     * @return 应用节点
     */
    private ApplicationNodeListResult getApplicationNode(String applicationName, String agentId) {
        // amdb 查询应用节点
        log.info("探针操作 --> amdb 查询应用节点信息");
        QueryApplicationNodeParam queryApplicationNodeParam = new QueryApplicationNodeParam();
        queryApplicationNodeParam.setAppName(applicationName);
        queryApplicationNodeParam.setAgentId(agentId);
        queryApplicationNodeParam.setPageSize(1);
        PagingList<ApplicationNodeListResult> applicationNodeResultPage =
            applicationNodeDAO.pageNode(queryApplicationNodeParam);
        List<ApplicationNodeListResult> nodeList = applicationNodeResultPage.getList();
        ApplicationNodeListResult node = nodeList.isEmpty() ? null : nodeList.get(0);
        this.isOperateProbeError(node == null, "节点不存在!");
        return node;
    }

    /**
     * 探针操作错误
     *
     * @param message 错误信息
     */
    private TakinWebException getOperateProbeError(String message) {
        return new TakinWebException(ExceptionCode.AGENT_APPLICATION_NODE_PROBE_UPDATE_OPERATE_RESULT_ERROR, message);
    }

    /**
     * 探针操作错误
     *
     * @param condition 条件
     * @param message   错误信息
     */
    private void isOperateProbeError(boolean condition, String message) {
        if (condition) {
            throw this.getOperateProbeError(message);
        }
    }

}
