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

import com.google.common.collect.Lists;
import com.pamirs.takin.common.util.http.DateUtil;
import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationNodeQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeProbeInfoDTO;
import io.shulie.takin.web.biz.design.probe.AbstractApplicationNodeProbeState;
import io.shulie.takin.web.biz.design.probe.ApplicationNodeProbeStateFactory;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeDashBoardQueryRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeOperateProbeRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeQueryRequest;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationNodeDashBoardResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationNodeResponse;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.application.ApplicationNodeService;
import io.shulie.takin.web.biz.utils.AgentZkClientUtil;
import io.shulie.takin.web.biz.utils.business.probe.ApplicationNodeProbeUtil;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.constant.LockKeyConstants;
import io.shulie.takin.web.common.constant.ProbeConstants;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.enums.probe.AmdbProbeStatusEnum;
import io.shulie.takin.web.common.enums.probe.ApplicationNodeProbeOperateEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.pojo.dto.probe.ApplicationNodeProbeOperateDTO;
import io.shulie.takin.web.common.pojo.dto.probe.MatchApplicationNodeProbeStateDTO;
import io.shulie.takin.web.data.util.ConfigServerHelper;
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
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (CollectionUtils.isEmpty(applicationNodes.getList())) {
            return PagingList.of(Lists.newArrayList(),applicationNodes.getTotal());
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
        // ??????????????????
        String applicationName = applicationService.getApplicationNameByApplicationId(request.getApplicationId());

        // ????????????????????????
        PagingList<ApplicationNodeListResult> applicationNodeResultPage =
            this.getApplicationNodeListResultPage(request, applicationName);

        // ????????????????????????????????????
        return this.getApplicationNodeResponseList(applicationName, applicationNodeResultPage);
    }

    /**
     * ????????????????????????????????????
     *
     * @param applicationName           ????????????
     * @param applicationNodeResultPage ??????????????????
     * @return ??????????????????????????????
     */
    private PagingList<ApplicationNodeResponse> getApplicationNodeResponseList(String applicationName,
        PagingList<ApplicationNodeListResult> applicationNodeResultPage) {
        if (applicationNodeResultPage.getList().isEmpty()) {
            return PagingList.of(Collections.emptyList(), applicationNodeResultPage.getTotal());
        }

        // ????????????
        List<ApplicationNodeListResult> nodeList = applicationNodeResultPage.getList();

        // agentId ?????? ?????????????????????map
        Map<String, ApplicationNodeProbeResult> agentIdAboutApplicationNodeProbeMap =
            this.getAgentIdAboutApplicationNodeProbeMap(applicationName, nodeList);

        // ??????????????????, ??????????????????
        List<ApplicationNodeResponse> applicationNodeResponseList = nodeList.stream().map(node -> {
            ApplicationNodeResponse applicationNodeResponse = new ApplicationNodeResponse();
            BeanUtils.copyProperties(node, applicationNodeResponse);

            // ????????????
            applicationNodeResponse.setUpdateTime(
                DateUtil.getYYYYMMDDHHMMSS(applicationNodeResponse.getAgentUpdateTime()));
            applicationNodeResponse.setProcessNo(applicationNodeResponse.getProgressId());
            applicationNodeResponse.setIp(applicationNodeResponse.getIpAddress());

            MatchApplicationNodeProbeStateDTO matchApplicationNodeProbeStateDTO
                = new MatchApplicationNodeProbeStateDTO();
            matchApplicationNodeProbeStateDTO.setAmdbProbeState(applicationNodeResponse.getProbeStatus());
            matchApplicationNodeProbeStateDTO.setProbeOperate(ApplicationNodeProbeOperateEnum.NONE.getCode());

            // ????????????
            ApplicationNodeProbeResult applicationNodeProbeResult =
                agentIdAboutApplicationNodeProbeMap.get(applicationNodeResponse.getAgentId());
            if (applicationNodeProbeResult != null) {
                matchApplicationNodeProbeStateDTO.setProbeOperate(applicationNodeProbeResult.getOperate());
                matchApplicationNodeProbeStateDTO.setProbeOperateResult(applicationNodeProbeResult.getOperateResult());
            }

            // ??????????????????
            AbstractApplicationNodeProbeState abstractApplicationNodeProbeState =
                applicationNodeProbeStateFactory.getState(matchApplicationNodeProbeStateDTO);
            if (abstractApplicationNodeProbeState == null) {
                return applicationNodeResponse;
            }

            // ?????????
            applicationNodeResponse
                .setProbeVersion(abstractApplicationNodeProbeState.probeVersion(applicationNodeResponse.getProbeVersion()));

            // ????????????
            applicationNodeResponse.setProbeStatus(abstractApplicationNodeProbeState.probeState());
            applicationNodeResponse.setProbeStatusDesc(abstractApplicationNodeProbeState.probeStateDesc());

            // ????????????
            applicationNodeResponse.setOperateStatusDesc(
                abstractApplicationNodeProbeState.operateStateDesc(matchApplicationNodeProbeStateDTO));
            return applicationNodeResponse;
        }).collect(Collectors.toList());

        return PagingList.of(applicationNodeResponseList, applicationNodeResultPage.getTotal());
    }

    /**
     * ??????????????????????????????
     *
     * @param request         ????????????
     * @param applicationName ????????????
     * @return ????????????????????????
     */
    private PagingList<ApplicationNodeListResult> getApplicationNodeListResultPage(ApplicationNodeQueryRequest request,
        String applicationName) {
        // ????????????, ???????????????
        QueryApplicationNodeParam queryApplicationNodeParam = new QueryApplicationNodeParam();
        queryApplicationNodeParam.setPageSize(request.getPageSize());
        queryApplicationNodeParam.setCurrent(request.getRealCurrent());

        queryApplicationNodeParam.setAppName(applicationName);
        queryApplicationNodeParam.setAppNames("");
        queryApplicationNodeParam.setIp(request.getIp());

        // ?????????????????????
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
     * ?????? amdb ????????????, ??????????????????????????? map
     *
     * @param applicationName ????????????
     * @param nodeList        ????????????
     * @return agentId ?????? ?????????????????? map
     */
    private Map<String, ApplicationNodeProbeResult> getAgentIdAboutApplicationNodeProbeMap(
        String applicationName, List<ApplicationNodeListResult> nodeList) {
        // ?????? agentId, ?????? ??????????????????
        List<String> agentIds = nodeList.stream().map(ApplicationNodeListResult::getAgentId)
            .collect(Collectors.toList());
        if (agentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // ??????????????????
        List<ApplicationNodeProbeResult> applicationNodeProbeResultList = applicationNodeProbeDAO
            .listByApplicationNameAndAgentIds(applicationName, agentIds);
        if (applicationNodeProbeResultList.isEmpty()) {
            return Collections.emptyMap();
        }

        // map ??????
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
        String agentRegisteredPath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.AGENT_REGISTERED_PATH);

        String path;
        if (agentRegisteredPath.endsWith("/")) {
            path = agentRegisteredPath + appName;
        } else {
            path = agentRegisteredPath + "/" + appName;
        }
        if (StringUtils.isNotBlank(agentId)) {
            // ???????????????agentId
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
            response.setErrorMsg("???????????????????????????????????????");
            return response;
        }
        response.setNodeOnlineCount(
            Long.valueOf(applicationResultList.get(0).getInstanceInfo().getInstanceOnlineAmount()));
        if (!response.getNodeOnlineCount().equals(Long.valueOf(response.getNodeTotalCount()))) {
            response.setErrorMsg("???????????????????????????????????????");
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
                response.setErrorMsg("agent???????????????");
            }
        }
        return response;
    }

    @Override
    public ApplicationNodeDashBoardResponse getApplicationNodeInfo(Long applicationId) {
        ApplicationDetailResult application = applicationDAO.getApplicationById(applicationId);
        return application == null ? new ApplicationNodeDashBoardResponse()
            : this.getApplicationNodeDashBoardResponse(application.getApplicationName(), application.getNodeNum());
    }

    @Override
    public ApplicationNodeDashBoardResponse getApplicationNodeDashBoardResponse(String applicationName,
        Integer nodeNum) {
        ApplicationNodeQueryDTO applicationNodeQueryDTO = new ApplicationNodeQueryDTO();
        applicationNodeQueryDTO.setAppName(applicationName);
        applicationNodeQueryDTO.setProbeStatus(AmdbProbeStatusEnum.INSTALLED.getCode());
        ApplicationNodeProbeInfoDTO applicationNodeProbeInfo =
            applicationClient.getApplicationNodeProbeInfo(applicationNodeQueryDTO);

        // ????????????
        List<String> messages = new ArrayList<>(3);

        ApplicationNodeDashBoardResponse response = new ApplicationNodeDashBoardResponse();
        // ??????
        response.setNodeTotalCount(nodeNum);

        if (applicationNodeProbeInfo != null) {
            // ??????
            response.setNodeOnlineCount(applicationNodeProbeInfo.getOnlineNodesCount());
            // ???????????????
            response.setProbeInstalledNodeNum(applicationNodeProbeInfo.getSpecificStatusNodesCount());

            // ?????????????????????
            List<String> versionList = applicationNodeProbeInfo.getVersionList();
            if (versionList != null && versionList.size() > 1) {
                messages.add("???????????????????????????, ???????????????");
            }
        }

        if (!Long.valueOf(response.getNodeTotalCount()).equals(response.getNodeOnlineCount())) {
            messages.add("??????????????????????????????????????????????????????");
        }

        if (!Long.valueOf(response.getNodeTotalCount()).equals(response.getProbeInstalledNodeNum())) {
            messages.add("???????????????????????????????????????????????????????????????");
        }

        response.setErrorMsg(String.join(SEMICOLON, messages));
        return response;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void operateProbe(ApplicationNodeOperateProbeRequest request) {
        // ?????????, ???????????????, ????????????????????????id
        if (!ApplicationNodeProbeUtil.isUninstallOperate(request.getOperateType())
            && request.getProbeId() == null) {
            throw this.getOperateProbeError("??????????????????!");
        }

        // ????????????, ???????????????????????????????????????????????????
        String lockKey = String.format(LockKeyConstants.LOCK_OPERATE_PROBE,
            request.getApplicationId(), request.getAgentId());
        this.isOperateProbeError(!distributedLock.tryLockSecondsTimeUnit(lockKey, 0L, 30L), TOO_FREQUENTLY);

        if (log.isInfoEnabled()) {
            log.info("???????????? --> ????????????: {}", request.getOperateType());
            log.info("???????????? --> ??????????????????");
        }

        // ??????????????????????????????
        ApplicationDetailResult application = applicationService.getByApplicationIdWithCheck(request.getApplicationId());
        if (!Objects.equals(application.getTenantId(), WebPluginUtils.traceTenantId())) {
            throw this.getOperateProbeError("??????????????????????????????????????????!");
        }

        try {
            // ??????????????????
            String applicationName = request.getAppName();
            if (StringUtils.isBlank(applicationName)) {
                applicationName = applicationService.getApplicationNameByApplicationId(request.getApplicationId());
            }

            // ????????????????????????????????????, ?????????, ??????????????????
            ApplicationNodeProbeResult applicationNodeProbeResult =
                applicationNodeProbeDAO.getByApplicationNameAndAgentId(applicationName, ALL_AGENT_ID);
            if (applicationNodeProbeResult != null) {
                throw new UnsupportedOperationException("????????????????????????, ?????????????????????????????????!");
            }

            // ????????????????????????
            Integer probeStatus = AmdbProbeStatusEnum.INSTALLED.getCode();
            if (!ALL_AGENT_ID.equals(request.getAgentId())) {
                // ??????????????????
                ApplicationNodeListResult node = this.getApplicationNode(applicationName, request.getAgentId());
                probeStatus = node.getProbeStatus();
            }

            this.operate(request, applicationName, probeStatus);
        } finally {
            distributedLock.unLockSafely(lockKey);
        }
    }

    /**
     * ??????????????????
     *
     * @param request         ????????????
     * @param applicationName ????????????
     * @param amdbProbeStatus amdb ???, ????????????, @see AmdbProbeStatusEnum
     */
    private void operate(ApplicationNodeOperateProbeRequest request, String applicationName, Integer amdbProbeStatus) {
        // ??????????????????????????????, ?????????????????????
        log.info("???????????? --> ????????????????????????????????????");
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

        // ??????????????????????????????, ??????
        log.info("???????????? --> ??????");
        AbstractApplicationNodeProbeState abstractApplicationNodeProbeState =
            applicationNodeProbeStateFactory.getState(matchApplicationNodeProbeStateDTO);
        this.isOperateProbeError(abstractApplicationNodeProbeState == null, "??????????????????!");

        log.info("???????????? --> ??????");
        ApplicationNodeProbeOperateDTO applicationNodeProbeOperateDTO = new ApplicationNodeProbeOperateDTO();
        BeanUtils.copyProperties(request, applicationNodeProbeOperateDTO);
        applicationNodeProbeOperateDTO.setApplicationName(applicationName);
        abstractApplicationNodeProbeState.handle(applicationNodeProbeOperateDTO);
    }

    /**
     * ??????????????????
     *
     * @param applicationName ????????????
     * @param agentId         agentId
     * @return ????????????
     */
    private ApplicationNodeListResult getApplicationNode(String applicationName, String agentId) {
        // amdb ??????????????????
        log.info("???????????? --> amdb ????????????????????????");
        QueryApplicationNodeParam queryApplicationNodeParam = new QueryApplicationNodeParam();
        queryApplicationNodeParam.setAppName(applicationName);
        queryApplicationNodeParam.setAgentId(agentId);
        queryApplicationNodeParam.setPageSize(1);
        PagingList<ApplicationNodeListResult> applicationNodeResultPage =
            applicationNodeDAO.pageNode(queryApplicationNodeParam);
        List<ApplicationNodeListResult> nodeList = applicationNodeResultPage.getList();
        ApplicationNodeListResult node = nodeList.isEmpty() ? null : nodeList.get(0);
        this.isOperateProbeError(node == null, "???????????????!");
        return node;
    }

    /**
     * ??????????????????
     *
     * @param message ????????????
     */
    private TakinWebException getOperateProbeError(String message) {
        return new TakinWebException(ExceptionCode.AGENT_APPLICATION_NODE_PROBE_UPDATE_OPERATE_RESULT_ERROR, message);
    }

    /**
     * ??????????????????
     *
     * @param condition ??????
     * @param message   ????????????
     */
    private void isOperateProbeError(boolean condition, String message) {
        if (condition) {
            throw this.getOperateProbeError(message);
        }
    }

}
