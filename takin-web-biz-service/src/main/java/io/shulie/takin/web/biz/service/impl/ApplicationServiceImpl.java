package io.shulie.takin.web.biz.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.constant.AppConfigSheetEnum;
import com.pamirs.takin.common.constant.AppSwitchEnum;
import com.pamirs.takin.common.enums.ds.DbTypeEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.entity.dao.simplify.TAppMiddlewareInfoMapper;
import com.pamirs.takin.entity.domain.dto.ApplicationSwitchStatusDTO;
import com.pamirs.takin.entity.domain.dto.NodeUploadDataDTO;
import com.pamirs.takin.entity.domain.dto.config.ImportConfigDTO;
import com.pamirs.takin.entity.domain.dto.linkmanage.InterfaceVo;
import com.pamirs.takin.entity.domain.entity.ExceptionInfo;
import com.pamirs.takin.entity.domain.entity.simplify.TAppMiddlewareInfo;
import com.pamirs.takin.entity.domain.entity.simplify.TShadowJobConfig;
import com.pamirs.takin.entity.domain.query.ApplicationQueryRequest;
import com.pamirs.takin.entity.domain.query.LinkGuardQueryParam;
import com.pamirs.takin.entity.domain.query.agent.AppMiddlewareQuery;
import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import com.pamirs.takin.entity.domain.vo.JarVersionVo;
import com.pamirs.takin.entity.domain.vo.application.NodeNumParam;
import com.pamirs.takin.entity.domain.vo.dsmanage.Configurations;
import com.pamirs.takin.entity.domain.vo.dsmanage.DataSource;
import com.pamirs.takin.entity.domain.vo.guardmanage.LinkGuardVo;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.constant.AgentConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.input.application.*;
import io.shulie.takin.web.biz.pojo.input.whitelist.WhitelistImportFromExcelInput;
import io.shulie.takin.web.biz.pojo.openapi.response.application.ApplicationListResponse;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationListByUpgradeRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeOperateProbeRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationQueryRequestV2;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationVisualInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityBottleneckResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationListByUpgradeResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationListResponseV2;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationVisualInfoResponse;
import io.shulie.takin.web.biz.pojo.response.application.ShadowServerConfigurationResponse;
import io.shulie.takin.web.biz.pojo.vo.application.ApplicationDsManageExportVO;
import io.shulie.takin.web.biz.service.*;
import io.shulie.takin.web.biz.service.application.ApplicationNodeService;
import io.shulie.takin.web.biz.service.dsManage.DsService;
import io.shulie.takin.web.biz.service.linkmanage.LinkGuardService;
import io.shulie.takin.web.biz.service.linkmanage.WhiteListService;
import io.shulie.takin.web.biz.service.simplify.ShadowJobConfigService;
import io.shulie.takin.web.biz.utils.DsManageUtil;
import io.shulie.takin.web.biz.utils.PageUtils;
import io.shulie.takin.web.biz.utils.WhiteListUtil;
import io.shulie.takin.web.biz.utils.xlsx.ExcelUtils;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.constant.GuardEnableConstants;
import io.shulie.takin.web.common.constant.ProbeConstants;
import io.shulie.takin.web.common.constant.WhiteListConstants;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.enums.application.AppAccessStatusEnum;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.enums.excel.BooleanEnum;
import io.shulie.takin.web.common.enums.probe.ApplicationNodeProbeOperateEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.common.util.JsonUtil;
import io.shulie.takin.web.common.util.MD5Tool;
import io.shulie.takin.web.common.util.whitelist.WhitelistUtil;
import io.shulie.takin.web.common.vo.excel.*;
import io.shulie.takin.web.data.dao.ApplicationNodeProbeDAO;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.dao.application.*;
import io.shulie.takin.web.data.dao.blacklist.BlackListDAO;
import io.shulie.takin.web.data.model.mysql.*;
import io.shulie.takin.web.data.param.application.*;
import io.shulie.takin.web.data.param.blacklist.BlacklistCreateNewParam;
import io.shulie.takin.web.data.param.blacklist.BlacklistSearchParam;
import io.shulie.takin.web.data.param.blacklist.BlacklistUpdateParam;
import io.shulie.takin.web.data.result.application.*;
import io.shulie.takin.web.data.result.blacklist.BlacklistResult;
import io.shulie.takin.web.data.result.whitelist.WhitelistEffectiveAppResult;
import io.shulie.takin.web.data.result.whitelist.WhitelistResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.entity.e2e.E2eExceptionConfigInfoExt;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import static io.shulie.takin.web.common.common.Response.PAGE_TOTAL_HEADER;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mubai<chengjiacai.shulie.io>
 * @date 2020-03-16 15:25
 */

@Service
@Slf4j
@EnableScheduling
public class ApplicationServiceImpl implements ApplicationService, WhiteListConstants {
    public static final String PRADAR_SEPERATE_FLAG = "_NEW_PRADAR_";
    public static final String PRADARNODE_SEPERATE_FLAG = "_PRADARNODE_";
    public static final String PRADARNODE_KEYSET = "_PRADARNODE_KEYSET";
    private static final String FALSE_CORE = "0";
    private static final String NEED_VERIFY_USER_MAP = "NEED_VERIFY_USER_MAP";
    private static final String PRADAR_SWITCH_STATUS = "PRADAR_SWITCH_STATUS_";
    private static final String PRADAR_SWITCH_STATUS_VO = "PRADAR_SWITCH_STATUS_VO_";
    private static final String PRADAR_SWITCH_ERROR_INFO_UID = "PRADAR_SWITCH_ERROR_INFO_";
    private static final String PRADAR_SILENCE_SWITCH_STATUS_VO = "PRADAR_SILENCE_SWITCH_STATUS_";
    private static final String PRADAR_SILENCE_SWITCH_STATUS = "PRADAR_SILENCE_SWITCH_STATUS_VO_";
    private static final List<String> CONFIGITEMLIST = Collections.singletonList("redis??????key?????????");
    private static final String QUERY_METRIC_DATA = "/amdb/db/api/metrics/metricsDetailes";

    @Autowired
    private ConfCenterService confCenterService;

    @Autowired
    private TAppMiddlewareInfoMapper tAppMiddlewareInfoMapper;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private AgentConfigCacheManager agentConfigCacheManager;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ApplicationNodeDAO applicationNodeDAO;
    @Autowired
    private WhiteListService whiteListService;
    @Autowired
    private ShadowJobConfigService shadowJobConfigService;
    @Autowired
    private LinkGuardService linkGuardService;

    @Autowired
    private ApplicationDsManageDAO applicationDsManageDao;

    @Autowired
    private WhitelistEffectiveAppDao whitelistEffectiveAppDao;

    @Autowired
    private ShadowConsumerService shadowConsumerService;

    @Autowired
    private AppConfigEntityConvertService appConfigEntityConvertService;

    @Autowired
    private DsService dsService;

    @Autowired
    private LinkGuardDAO linkGuardDAO;

    @Value("${application.ds.config.is.new.version: false}")
    private Boolean isNewVersion;

    @Value("${application.error.num: 20}")
    private Integer appErrorNum;

    @Autowired
    private ApplicationService applicationService;

    /**
     * ?????????????????????????????????
     */
    private boolean isCheckDuplicateName;

    @Autowired
    private ShadowJobConfigDAO shadowJobConfigDAO;

    @Autowired
    private WhiteListDAO whiteListDAO;

    @Autowired
    private ShadowMqConsumerDAO shadowMqConsumerDAO;

    @Autowired
    private AppRemoteCallDAO appRemoteCallDAO;

    @Autowired
    private BlackListDAO blackListDAO;

    @Autowired
    private ApplicationPluginsConfigDAO pluginsConfigDAO;

    @Autowired
    private ApplicationPluginsConfigService pluginsConfigService;

    @Autowired
    private ApplicationNodeService applicationNodeService;

    @Autowired
    private ApplicationNodeProbeDAO applicationNodeProbeDAO;

    @Autowired
    private AppAgentConfigReportDAO reportDAO;

    @Autowired
    private AmdbClientProperties properties;

    @Autowired
    private ActivityDAO activityDAO;

    @Autowired
    private LinkTopologyService linkTopologyService;

    @Autowired
    private ApplicationDsCacheManageDAO dsCacheManageDAO;

    @Autowired
    private ApplicationDsDbManageDAO dsDbManageDAO;

    @Autowired
    @Qualifier("agentDataThreadPool")
    private ThreadPoolExecutor agentDataThreadPool;


    @PostConstruct
    public void init() {
        isCheckDuplicateName = ConfigServerHelper.getBooleanValueByKey(
                ConfigServerKeyEnum.TAKIN_WHITE_LIST_DUPLICATE_NAME_CHECK);
    }

    @Override
    public List<ApplicationDetailResult> getApplicationsByUserIdList(List<Long> userIdList) {
        return applicationDAO.getApplicationListByUserIds(userIdList);
    }

    //3.??????????????????
    //???????????????????????????????????????5???
    @Override
    public void configureTasks() {
        //??????????????????????????????
        Map<String, Long> map = redisTemplate.opsForHash().entries(NEED_VERIFY_USER_MAP);
        if (map.size() > 0) {
            log.info("??????????????????????????? => " + map.size());
            long pradarSwitchProcessingTime = Long.parseLong(
                    ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_PRADAR_SWITCH_PROCESSING_WAIT_TIME));
            map.forEach((key, value) -> {
                if (System.currentTimeMillis() - value >= pradarSwitchProcessingTime * 1000L) {
                    // ???????????????????????????????????????
                    // ?????????key????????? tenantUseAppkey_env
                    String switchStatus = getUserSwitchStatusForAgent(key);
                    redisTemplate.opsForValue().set(PRADAR_SWITCH_STATUS_VO + key, switchStatus);
                    // agent??????????????????????????????????????????
                    redisTemplate.opsForHash().delete(NEED_VERIFY_USER_MAP, key);
                }
            });
        }
    }

    @Override
    public List<ApplicationListResponse> getApplicationList(String appNames) {
        List<String> appNamesList = Lists.newArrayList();
        if (StringUtils.isNotBlank(appNames)) {
            appNamesList = Lists.newArrayList(appNames.split(","));
        }
        List<ApplicationDetailResult> results = applicationDAO.getApplications(appNamesList);
        if (results == null || results.size() == 0) {
            return Lists.newArrayList();
        }
        ApplicationNodeQueryParam param = new ApplicationNodeQueryParam();
        List<String> nodeAppNames = results.stream().map(ApplicationDetailResult::getApplicationName)
                .collect(Collectors.toList());
        param.setApplicationNames(nodeAppNames);
        param.setCurrent(0);
        param.setPageSize(99999999);
        PagingList<ApplicationNodeResult> nodeResults = applicationNodeDAO.pageNodes(param);
        Map<String, List<ApplicationNodeResult>> nodeIpMap = nodeResults.getList().stream()
                .collect(Collectors.groupingBy(ApplicationNodeResult::getAppName));
        // ????????????
        return results.stream().map(result -> {
            ApplicationListResponse response = new ApplicationListResponse();
            BeanUtils.copyProperties(result, response);
            if (nodeIpMap.get(result.getApplicationName()) != null && nodeIpMap.get(result.getApplicationName()).size()
                    > 0) {
                response.setIpsList(nodeIpMap.get(result.getApplicationName())
                        .stream().map(ApplicationNodeResult::getNodeIp).collect(Collectors.toList()));
                response.setNodeNum(nodeIpMap.get(result.getApplicationName()).size());
                // ??????????????????
                if (!response.getNodeNum().equals(result.getNodeNum())
                        || nodeIpMap.get(result.getApplicationName()).stream().
                        map(ApplicationNodeResult::getAgentVersion).distinct().count() > 1) {
                    response.setAccessStatus(3);
                }
            } else {
                response.setIpsList(Lists.newArrayList());
                response.setNodeNum(0);
                // ?????????
                response.setAccessStatus(3);
            }
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public Response<List<ApplicationVo>> getApplicationList(ApplicationQueryRequest queryParam) {

        PagingList<ApplicationDetailResult> pageInfo = confCenterService.queryApplicationList(queryParam);
        List<ApplicationDetailResult> list = pageInfo.getList();
        List<ApplicationVo> applicationVoList = appEntryListToVoList(list);
        //??????ids
        List<Long> userIds = applicationVoList.stream()
                .map(ApplicationVo::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        //????????????Map key:userId  value:user??????
        Map<Long, UserExt> userMap = WebPluginUtils.getUserMapByIds(userIds);
        if (!userMap.isEmpty()) {
            applicationVoList.forEach(data -> data
                    .setUserName(Optional.ofNullable(userMap.get(data.getUserId()))
                            .map(UserExt::getName)
                            .orElse(""))
            );
        }
        return Response.success(applicationVoList,
                CollectionUtils.isEmpty(applicationVoList) ? 0 : pageInfo.getTotal());
    }

    @Override
    public Long getAccessErrorNum() {
        ApplicationQueryRequestV2 requestV2 = new ApplicationQueryRequestV2();
        requestV2.setAccessStatus(3);
     return this.pageApplication(requestV2).getTotal();
    }

//    @Override
//    public Long getAccessErrorNum() {
//        List<ApplicationDetailResult> results = applicationDAO.getDashboardAppData();
//        // ??????amdb????????????
//        if (results == null || results.size() == 0) {
//            return 0L;
//        }
//        //????????????????????????
//        List<String> appNameList = results.stream().map(ApplicationDetailResult::getApplicationName).collect(
//                Collectors.toList());
//        List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(appNameList);
//        if (CollectionUtil.isEmpty(applicationResultList)) {
//            return (long) results.size();
//        }
//        Map<String, List<ApplicationResult>> appResultMap = applicationResultList.stream()
//                .collect(Collectors.groupingBy(ApplicationResult::getAppName));
//        //???????????????????????????
//        ApplicationNodeQueryParam queryParam = new ApplicationNodeQueryParam();
//        queryParam.setCurrent(0);
//        queryParam.setPageSize(99999);
//        queryParam.setApplicationNames(appNameList);
//        PagingList<ApplicationNodeResult> applicationNodes = applicationNodeDAO.pageNodes(queryParam);
//        if (CollectionUtil.isEmpty(applicationResultList)) {
//            return (long) results.size();
//        }
//
//        List<ApplicationNodeResult> applicationNodeResultList = applicationNodes.getList();
//        Map<String, List<ApplicationNodeResult>> applicationNodeResultMap = applicationNodeResultList
//                .stream().collect(Collectors.groupingBy(ApplicationNodeResult::getAppName));
//        return results.stream().filter(result -> {
//            List<ApplicationNodeResult> nodeResults = applicationNodeResultMap.get(result.getApplicationName());
//            List<ApplicationResult> appResults = appResultMap.get(result.getApplicationName());
//            if (CollectionUtils.isEmpty(nodeResults) || CollectionUtils.isEmpty(appResults)) {
//                return true;
//            }
//            if (!appResults.get(0).getInstanceInfo().getInstanceOnlineAmount().equals(result.getNodeNum())
//                    || nodeResults.stream().map(ApplicationNodeResult::getAgentVersion).distinct().count() > 1) {
//                return true;
//            }
//            // ????????????
//            if (AppAccessStatusEnum.EXCEPTION.getCode().equals(result.getAccessStatus())) {
//                return true;
//            }
//            return false;
//        }).count();
//    }

    @Override
    public List<ApplicationVo> getApplicationListVo(ApplicationQueryRequest queryParam) {

        PagingList<ApplicationDetailResult> pageInfo = confCenterService.queryApplicationList(queryParam);
        List<ApplicationDetailResult> list = pageInfo.getList();
        List<ApplicationVo> applicationVoList = appEntryListToVoList(list);

        //??????ids
        List<Long> userIds = applicationVoList.stream().map(ApplicationVo::getUserId).filter(Objects::nonNull)
                .collect(
                        Collectors.toList());
        //????????????Map key:userId  value:user??????
        Map<Long, UserExt> userMap = WebPluginUtils.getUserMapByIds(userIds);
        if (!userMap.isEmpty()) {
            applicationVoList.stream().map(data -> {
                //???????????????
                String userName = Optional.ofNullable(userMap.get(data.getUserId()))
                        .map(UserExt::getName)
                        .orElse("");
                data.setUserName(userName);
                return data;
            }).collect(Collectors.toList());
        }
        return applicationVoList;
    }

    /**
     * ????????????????????????????????????
     *
     * @return -
     */
    @Override
    public Response<List<ApplicationVo>> getApplicationList(ApplicationQueryRequest param, Integer accessStatus) {
        if (accessStatus == null) {
            return getApplicationList(param);
        }
        //??????????????????
        int pageSize = param.getPageSize();
        int currentPage = param.getCurrentPage();
        param.setPageSize(-1);
        Response<List<ApplicationVo>> response = getApplicationList(param);
        List<ApplicationVo> voList = response.getData();
        if (CollectionUtils.isEmpty(voList)) {
            return response;
        }
        currentPage = currentPage - 1;
        List<ApplicationVo> filterData = voList.stream().filter(vo -> vo.getAccessStatus().equals(accessStatus))
                .collect(Collectors.toList());
        List<ApplicationVo> page = PageUtils.getPage(true, currentPage, pageSize, filterData);
        return Response.success(page, CollectionUtils.isEmpty(filterData) ? 0 : filterData.size());
    }

    @Override
    public Response<List<ApplicationVo>> getApplicationList(ApplicationQueryRequestV2 request) {
        PagingList<ApplicationListResponseV2> paging = pageApplication(request);
        List<ApplicationListResponseV2> applications = paging.getList();
        List<ApplicationVo> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(applications)) {
            resultList = applications.stream().map(application -> {
                ApplicationVo vo = new ApplicationVo();
                vo.setId(application.getId());
                vo.setApplicationName(application.getApplicationName());
                return vo;
            }).collect(Collectors.toList());
        }
        Response.setHeaders(
            new HashMap<String, String>(1) {{
                put(PAGE_TOTAL_HEADER, String.valueOf(paging.getTotal()));
            }});
        return Response.success(resultList);
    }

    @Override
    public Response<ApplicationVo> getApplicationInfo(String id) {
        if (id == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_VALIDATE_ERROR, "??????id?????????null");
        }
        ApplicationDetailResult tApplicationMnt = confCenterService.queryApplicationInfoById(Long.parseLong(id));
        if (tApplicationMnt == null) {
            return Response.success(new ApplicationVo());
        }

        // ????????????????????????
        List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(
                Collections.singletonList(tApplicationMnt.getApplicationName()));
        ApplicationResult applicationResult = CollectionUtils.isEmpty(applicationResultList)
                ? null : applicationResultList.get(0);

        // ???????????????????????????
        ApplicationNodeQueryParam queryParam = new ApplicationNodeQueryParam();
        queryParam.setCurrent(0);
        queryParam.setPageSize(99999);
        queryParam.setApplicationNames(Collections.singletonList(tApplicationMnt.getApplicationName()));
        PagingList<ApplicationNodeResult> applicationNodes = applicationNodeDAO.pageNodes(queryParam);
        List<ApplicationNodeResult> applicationNodeResultList = applicationNodes.getList();
        ApplicationVo vo = this.appEntryToVo(tApplicationMnt, applicationResult, applicationNodeResultList);
        return Response.success(vo);
    }

    @Override
    public Response<ApplicationDetailResult> getApplicationInfoForError(String id) {
        if (id == null) {
            return Response.fail(FALSE_CORE, "??????id?????????null");
        }
        ApplicationDetailResult tApplicationMnt = confCenterService.queryApplicationInfoByIdAndRole(Long.parseLong(id));
        return Response.success(tApplicationMnt);
    }

    @Override
    public Response<Integer> addApplication(ApplicationVo param) {
        if (param == null) {
            return Response.fail(FALSE_CORE, "??????????????????");
        }
        try {
            if (StringUtil.isEmpty(param.getSwitchStutus())) {
                param.setSwitchStutus(AppSwitchEnum.OPENED.getCode());
            }
            if (param.getNodeNum() == null) {
                param.setNodeNum(1);
            }
            confCenterService.saveApplication(voToAppEntity(param));
        } catch (TakinModuleException e) {
            OperationLogContextHolder.ignoreLog();
            return Response.fail(FALSE_CORE, e.getErrorMessage(), e.getErrorMessage());
        }

        return Response.success();
    }

    @Override
    public Response<Integer> addAgentRegisterApplication(ApplicationVo param) {
        if (param == null) {
            throw new TakinWebException(TakinWebExceptionEnum.AGENT_PUSH_APPLICATION_VALIDATE_ERROR, "??????????????????");
        }

        if (StringUtils.isBlank(param.getApplicationName())) {
            throw new TakinWebException(TakinWebExceptionEnum.AGENT_PUSH_APPLICATION_VALIDATE_ERROR, "?????????????????????");
        }

        if (StringUtil.isEmpty(param.getSwitchStutus())) {
            param.setSwitchStutus(AppSwitchEnum.OPENED.getCode());
        }
        if (param.getNodeNum() == null) {
            param.setNodeNum(1);
        }

        confCenterService.saveAgentRegisterApplication(voToAppEntity(param));
        OperationLogContextHolder.ignoreLog();

        return Response.success();
    }

    @Override
    public Response<Integer> modifyApplication(ApplicationVo param) {
        if (param == null || StringUtil.isEmpty(param.getId())) {
            return Response.fail(FALSE_CORE, "??????id????????????");
        }
        try {
            Response<ApplicationVo> applicationVoResponse = this.getApplicationInfo(param.getId());
            if (null == applicationVoResponse.getData().getId()) {
                return Response.fail("??????????????????");
            }
            ApplicationVo applicationVo = applicationVoResponse.getData();
            String applicationName = param.getApplicationName();
            if (!applicationVo.getNodeNum().equals(param.getNodeNum())) {
                applicationName = applicationName + "??????????????????" + param.getNodeNum();
            } else {
                if (applicationVo.getApplicationName().equals(param.getApplicationName())) {
                    OperationLogContextHolder.ignoreLog();
                }
            }
            OperationLogContextHolder.addVars(BizOpConstants.Vars.APPLICATION, applicationName);
            confCenterService.updateApplicationInfo(voToAppEntity(param));
            // ?????????????????????????????????appName??????????????????????????????????????????????????????,????????????
            if (StringUtil.isNotEmpty(param.getApplicationName())) {
                updateConfigAppName(param.getId(), param.getApplicationName());
            }
        } catch (TakinModuleException e) {
            return Response.fail(FALSE_CORE, e.getErrorMessage(), e.getErrorMessage());
        }
        return Response.success();
    }

    private void updateConfigAppName(String id, String appName) {
        // ?????????????????????????????????????????????
        shadowMqConsumerDAO.updateAppName(Long.valueOf(id), appName);
        linkGuardDAO.updateAppName(Long.valueOf(id), appName);
        applicationDsManageDao.updateAppName(Long.valueOf(id), appName);
        // ????????????????????????
        appRemoteCallDAO.updateAppName(Long.valueOf(id), appName);
    }

    @Override
    public Response<Integer> deleteApplication(String appId) {
        if (appId == null) {
            return Response.fail(FALSE_CORE, "??????id????????????");
        }
        confCenterService.deleteApplicationInfoByIds(appId);
        return Response.success();
    }

    /**
     * ????????????????????????
     * 1?????????????????????redis??????????????? ??? 2????????????????????????
     *
     * @return -
     */
    @Override
    public Response<String> uploadAccessStatus(NodeUploadDataDTO param) {
        param.setSource(ContextSourceEnum.AGENT.getCode());
        WebPluginUtils.transferTenantParam(WebPluginUtils.traceTenantCommonExt(), param);
        agentDataThreadPool.execute(() -> {
            this.uploadAppStatus(param);
        });

        return Response.success("??????????????????????????????");
    }

    // ????????????????????????
    public void uploadAppStatus(NodeUploadDataDTO param) {
        // ??????header
        param.setSource(ContextSourceEnum.AGENT.getCode());
        WebPluginUtils.setTraceTenantContext(param);
        if (param == null || StringUtil.isEmpty(param.getApplicationName()) || StringUtil.isEmpty(param.getNodeKey())) {
            throw new TakinWebException(TakinWebExceptionEnum.AGENT_PUSH_APPLICATION_STATUS_VALIDATE_ERROR,
                    "????????????key|???????????? ????????????");
        }

        String tenantAppKey = WebPluginUtils.traceTenantAppKey();

        Long applicationId = this.queryApplicationIdByAppName(param.getApplicationName());

        if (applicationId == null) {
            log.error("?????????????????????{}???,?????????????????????", param.getApplicationName());
            return;
        }

        if (param.getSwitchErrorMap() != null && !param.getSwitchErrorMap().isEmpty()) {
            //??????id+ agent id????????? ??????????????????
            String envCode = WebPluginUtils.traceEnvCode();
            String key = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3, tenantAppKey, envCode,
                    applicationId + PRADAR_SEPERATE_FLAG + param.getAgentId());
            List<String> nodeUploadDataDTOList = redisTemplate.opsForList().range(key, 0, -1);
            if (CollectionUtils.isEmpty(nodeUploadDataDTOList) || nodeUploadDataDTOList.size() <= appErrorNum) {
                //??????key??????
                String nodeSetKey = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3, tenantAppKey, envCode,
                        applicationId + PRADARNODE_KEYSET);
                redisTemplate.opsForSet().add(nodeSetKey, key);
                redisTemplate.expire(nodeSetKey, 1, TimeUnit.DAYS);
                //????????????????????????
                redisTemplate.opsForList().leftPush(key, JSONObject.toJSONString(param));
                redisTemplate.expire(key, 1, TimeUnit.DAYS);
            } else {
                // ?????? appErrorNum ?????? ????????????
                redisTemplate.opsForList().leftPush(key, JSONObject.toJSONString(param));
                redisTemplate.opsForList().trim(key, 0, 19);
            }
        }
    }

    @Override
    public synchronized void syncApplicationAccessStatus() {
        try {
            // ??????????????????
            int pageSize = 20;
            // ?????????????????????, ????????????pageSize, ??????????????????
            int applicationNumber;

            PageBaseDTO pageBaseDTO = new PageBaseDTO();
            pageBaseDTO.setPageSize(pageSize);

            do {
                // ?????????????????????????????????
                List<ApplicationListResult> applicationList = applicationDAO.pageFromSync(pageBaseDTO);
                if (applicationList.isEmpty()) {
                    return;
                }

                // ?????????
                pageBaseDTO.setCurrent(pageBaseDTO.getCurrent() + 1);
                // ??????????????????????????????
                applicationNumber = applicationList.size();

                // ??????????????????
                List<String> appNames = applicationList.stream()
                        .map(ApplicationListResult::getApplicationName)
                        .collect(Collectors.toList());

                // ??????????????????map, key ????????????, value amdb????????????
                Map<String, ApplicationResult> amdbApplicationMap = this.getAmdbApplicationMap(appNames);

                // ????????????????????????map, key ????????????, value amdb????????????
                Map<String, List<ApplicationNodeResult>> amdbApplicationNodeMap = this.getAmdbApplicationNodeMap(
                        appNames);

                // ???????????????
                Set<Long> errorApplicationIdSet = new HashSet<>(20);
                // ???????????????
                Set<Long> normalApplicationIdSet = new HashSet<>(20);

                // ????????????
                for (ApplicationListResult application : applicationList) {
                    String applicationName = application.getApplicationName();
                    Long applicationId = application.getApplicationId();
                    Integer nodeNum = application.getNodeNum();

                    // ???????????????????????????????????????
                    ApplicationResult amdbApplication;
                    // ???????????????????????????????????????
                    List<ApplicationNodeResult> amdbApplicationNodeList;

                    if (amdbApplicationMap.isEmpty()
                            || (amdbApplication = amdbApplicationMap.get(applicationName)) == null
                            || !Objects.equals(amdbApplication.getInstanceInfo().getInstanceOnlineAmount(), nodeNum)) {
                        // amdbApplicationMap ?????????, map.get ?????????, ????????????????????????
                        errorApplicationIdSet.add(applicationId);

                    } else if (!amdbApplicationMap.isEmpty()
                            && (amdbApplication = amdbApplicationMap.get(applicationName)) != null
                            && amdbApplication.getAppIsException()) {
                        // map ??????, map.get ??????, amdb???????????????
                        errorApplicationIdSet.add(applicationId);

                    } else if (!amdbApplicationNodeMap.isEmpty()
                            && CollectionUtil.isNotEmpty(
                            amdbApplicationNodeList = amdbApplicationNodeMap.get(applicationName))
                            && amdbApplicationNodeList.stream().map(ApplicationNodeResult::getAgentVersion).distinct()
                            .count()
                            > 1) {
                        // ??????agent?????????????????????
                        errorApplicationIdSet.add(applicationId);

                    } else {
                        normalApplicationIdSet.add(applicationId);
                    }
                }


                this.syncApplicationAccessStatus(applicationList,errorApplicationIdSet);
            } while (applicationNumber == pageSize);
            // ???????????????, ????????????????????????????????????pageSize, ?????????????????????

        } catch (Exception e) {
            log.error("??????????????????????????????, ????????????: {}", e.getMessage(), e);
        }
        log.debug("??????????????????????????????!");
    }

    private void syncApplicationAccessStatus(List<ApplicationListResult> applicationList,Set<Long> errorApplicationIdSet) {
        if (CollectionUtils.isNotEmpty(applicationList)) {
            for (ApplicationListResult app : applicationList) {
                Map result = applicationDAO.getStatus(app.getApplicationName());
                long n = (long) result.get("n");
                if (n != 0 || (errorApplicationIdSet.contains(app.getApplicationId()))) {
                    String e = (String) result.get("e");
                    //??????????????????Ip???????????????????????????
                    if (StringUtils.isBlank(e)) {
                        String a = (String) result.get("a");
                        if (StringUtils.isEmpty(a)) {
                            continue;
                        }
                        e = "??????????????????";
                        if (StringUtils.isNotEmpty(a)) {
                            e += "???agentId???" + a;
                        }
                    }
                    applicationDAO.updateStatus(app.getApplicationId(), e);
                    NodeUploadDataDTO param = new NodeUploadDataDTO();
                    param.setApplicationName(app.getApplicationName());
                    param.setAgentId((String) result.get("a"));
                    param.setNodeKey(UUID.randomUUID().toString().replace("_", ""));
                    param.setExceptionTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    HashMap map = new HashMap(1);
                    ExceptionInfo exceptionInfo = new ExceptionInfo();
                    exceptionInfo.setErrorCode("???");
                    exceptionInfo.setMessage(e);
                    exceptionInfo.setDetail(e);
                    map.put("Agent??????:" + this.toString().hashCode(), JSON.toJSONString(exceptionInfo));
                    param.setSwitchErrorMap(map);
                    uploadAccessStatus(param);
                } else {
                    applicationDAO.updateStatus(app.getApplicationId());}
            }
        }
    }

    /**
     * amdb??????????????????????????????????????????
     *
     * @param appNames ??????????????????
     * @return map, key ????????????, value amdb????????????
     */
    private Map<String, List<ApplicationNodeResult>> getAmdbApplicationNodeMap(List<String> appNames) {
        ApplicationNodeQueryParam queryParam = new ApplicationNodeQueryParam();
        queryParam.setCurrent(0);
        queryParam.setPageSize(99999);
        queryParam.setApplicationNames(appNames);
        PagingList<ApplicationNodeResult> applicationNodes = applicationNodeDAO.pageNodes(queryParam);
        List<ApplicationNodeResult> nodeList = applicationNodes.getList();
        return CollectionUtil.isEmpty(nodeList)
                ? Collections.emptyMap()
                : nodeList.stream().collect(Collectors.groupingBy(ApplicationNodeResult::getAppName));
    }

    /**
     * amdb????????????????????????????????????
     *
     * @param appNames ??????????????????
     * @return map, key ????????????, value amdb????????????
     */
    private Map<String, ApplicationResult> getAmdbApplicationMap(List<String> appNames) {
        // ??????amdb????????????
        List<ApplicationResult> amdbApplicationList = applicationDAO.listAmdbApplicationByAppNames(appNames);
        return CollectionUtil.isEmpty(amdbApplicationList)
                ? Collections.emptyMap()
                : amdbApplicationList.stream()
                .collect(Collectors.toMap(ApplicationResult::getAppName, Function.identity(), (v1, v2) -> v2));
    }

    @Override
    public void modifyAccessStatus(String applicationId, Integer accessStatus, String exceptionInfo) {
        //?????????????????????
        if (StringUtil.isNotEmpty(applicationId) && accessStatus != null) {
            ApplicationVo dbData = new ApplicationVo();
            dbData.setId(applicationId);
            dbData.setExceptionInfo(exceptionInfo);
            dbData.setAccessStatus(accessStatus);
            // ?????????????????????
            this.modifyApplication(dbData);
        }
    }

    @Override
    public List<ApplicationDetailResult> getAllApplications() {
        return applicationDAO.getAllApplications();
    }

    @Override
    public Response<Integer> uploadMiddleware(Map<String, String> requestMap) {
        return Response.success();
    }

    @Override
    public Response<Integer> calculateUserSwitch(TenantCommonExt ext) {
        if (ext == null) {
            UserExt user = WebPluginUtils.traceUser();
            if (WebPluginUtils.checkUserPlugin() && user == null) {
                return Response.fail(FALSE_CORE, "??????????????????");
            }
            //ext = user.getId();
        }
        // reCalculateUserSwitch(uid);
        return Response.success();
    }

    @Override
    public ApplicationSwitchStatusDTO agentGetUserSwitchInfo() {
        ApplicationSwitchStatusDTO result = new ApplicationSwitchStatusDTO();
        result.setSwitchStatus(getUserSwitchStatusForAgent(
                WebPluginUtils.traceTenantAppKey() + Separator.Separator3.getValue() + WebPluginUtils.traceEnvCode()));
        return result;
    }

    /**
     * @param key tenantUseAppkey_env
     * @return
     */
    private String getUserSwitchStatusForAgent(String key) {
        Object o = redisTemplate.opsForValue().get(PRADAR_SWITCH_STATUS + key);
        if (o == null) {
            redisTemplate.opsForValue().set(PRADAR_SWITCH_STATUS + key, AppSwitchEnum.OPENED.getCode());
            return AppSwitchEnum.OPENED.getCode();
        } else {
            return (String) o;
        }
    }

    @Override
    public String getUserSwitchStatusForVo() {
        String envCode = WebPluginUtils.traceEnvCode();
        String tenantAppKey = WebPluginUtils.traceTenantAppKey();
        final String statusVoRedisKey = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3,
                PRADAR_SWITCH_STATUS_VO + tenantAppKey, envCode);
        Object o = redisTemplate.opsForValue().get(statusVoRedisKey);
        if (o == null) {
            redisTemplate.opsForValue().set(statusVoRedisKey, AppSwitchEnum.OPENED.getCode());
            return AppSwitchEnum.OPENED.getCode();
        } else {
            return (String) o;
        }
    }

    @Override
    public List<String> getApplicationName() {
        List<Long> userIdList = WebPluginUtils.getQueryAllowUserIdList();
        List<ApplicationDetailResult> applicationDetailResultList = applicationDAO.getApplicationListByUserIds(
                userIdList);
        if (CollectionUtils.isEmpty(applicationDetailResultList)) {
            return Lists.newArrayList();
        }
        return applicationDetailResultList.stream().map(
                ApplicationDetailResult::getApplicationName).collect(
                Collectors.toList());
    }

    @Override
    public void exportApplicationConfig(HttpServletResponse response, Long applicationId) {
        ApplicationDetailResult application = applicationDAO.getApplicationByIdWithInterceptorIgnore(applicationId);
        Assert.notNull(application, "???????????????!");

        TenantInfoExt tenantInfo = WebPluginUtils.getTenantInfo(application.getTenantId());
        String tenantCode = "";
        String tenantAppKey = "";
        if (tenantInfo != null) {
            tenantCode = tenantInfo.getTenantCode();
            tenantAppKey = tenantInfo.getTenantAppKey();
        }
        // ???????????????
        WebPluginUtils.setTraceTenantContext(application.getTenantId(),
                tenantAppKey, application.getEnvCode(), tenantCode, ContextSourceEnum.HREF.getCode());

        List<ExcelSheetVO<?>> sheets = new ArrayList<>();

        // // ?????????/??? ?????????????????????????????????????????????
        // sheets.add(this.getShadowSheet(applicationId));

        // ??????????????????
        sheets.add(this.getLinkGuardSheet(applicationId));

        // job ??????
        sheets.add(this.getJobSheet(applicationId));

        // ???????????????
//        sheets.add(this.getWhiteListSheet(application));

        // ????????????????????????
        sheets.add(this.getRemoteCallConfigSheet(applicationId));

        // ?????????????????????
        sheets.add(this.getShadowConsumerSheet(applicationId));

        // ???????????????
        sheets.add(this.getBlacklistSheet(applicationId));

        // ??????????????????????????????
        sheets.add(this.getPluginsConfigSheet(applicationId));


        try {
            ExcelUtils.exportExcelManySheet(response, application.getApplicationName(), sheets);
        } catch (Exception e) {
            log.error("????????????????????????: {}", e.getMessage(), e);
        }
    }

    /**
     * ??????????????????????????????sheet
     *
     * @return -
     */
    private ExcelSheetVO<?> getPluginsConfigSheet(Long applicationId) {
        ApplicationPluginsConfigParam param = new ApplicationPluginsConfigParam();
        param.setApplicationId(applicationId);
        List<ApplicationPluginsConfigEntity> list = pluginsConfigDAO.findList(param);
        if (CollectionUtils.isEmpty(list)) {
            list = Collections.emptyList();
        }
        List<ApplicationPluginsConfigExcelVO> excelVoList = list.stream().map(t -> {
            ApplicationPluginsConfigExcelVO excelVO = new ApplicationPluginsConfigExcelVO();
            excelVO.setConfigItem(t.getConfigItem());
            excelVO.setConfigValue(t.getConfigValue());
            if ("redis_expire".equals(t.getConfigKey()) && "-1".equals(t.getConfigValue())) {
                excelVO.setConfigValue("?????????key??????");
            }
            return excelVO;
        }).collect(Collectors.toList());

        ExcelSheetVO<ApplicationPluginsConfigExcelVO> pluginsListSheet = new ExcelSheetVO<>();
        pluginsListSheet.setData(excelVoList);
        pluginsListSheet.setExcelModelClass(ApplicationPluginsConfigExcelVO.class);
        pluginsListSheet.setSheetName(AppConfigSheetEnum.PLUGINS_CONFIG.getDesc());
        pluginsListSheet.setSheetNum(AppConfigSheetEnum.values().length - 1);
        return pluginsListSheet;
    }

    /**
     * ?????????????????????sheet
     *
     * @return -
     */
    private ExcelSheetVO<?> getRemoteCallConfigSheet(Long applicationId) {
        AppRemoteCallQueryParam param = new AppRemoteCallQueryParam();
        param.setApplicationId(applicationId);
        List<AppRemoteCallResult> list = appRemoteCallDAO.getList(param);
        List<ApplicationRemoteCallConfigExcelVO> excelVoList = list.stream().map(t -> {
            ApplicationRemoteCallConfigExcelVO excelVO = new ApplicationRemoteCallConfigExcelVO();
            excelVO.setInterfaceName(t.getInterfaceName());
            excelVO.setInterfaceType(t.getInterfaceType());
            excelVO.setType(t.getType());
            excelVO.setServerAppName(
                    StringUtils.isBlank(t.getServerAppName()) ? CharSequenceUtil.NULL : t.getServerAppName());
            excelVO.setMockReturnValue(
                    StringUtils.isBlank(t.getMockReturnValue()) ? CharSequenceUtil.NULL : t.getMockReturnValue());
            return excelVO;
        }).collect(Collectors.toList());

        ExcelSheetVO<ApplicationRemoteCallConfigExcelVO> remoteCallSheet = new ExcelSheetVO<>();
        remoteCallSheet.setData(excelVoList);
        remoteCallSheet.setExcelModelClass(ApplicationRemoteCallConfigExcelVO.class);
        remoteCallSheet.setSheetName(AppConfigSheetEnum.REMOTE_CALL.getDesc());
        remoteCallSheet.setSheetNum(AppConfigSheetEnum.values().length - 1);
        return remoteCallSheet;
    }

    private ExcelSheetVO<?> getBlacklistSheet(Long applicationId) {
        BlacklistSearchParam param = new BlacklistSearchParam();
        param.setApplicationId(applicationId);
        List<BlacklistResult> blacklistResults = blackListDAO.selectList(param);
        List<BlacklistExcelVO> excelModelList = this.blacklist2ExcelModel(blacklistResults);
        ExcelSheetVO<BlacklistExcelVO> blacklistSheet = new ExcelSheetVO<>();
        blacklistSheet.setData(excelModelList);
        blacklistSheet.setExcelModelClass(BlacklistExcelVO.class);
        blacklistSheet.setSheetName(AppConfigSheetEnum.BLACK.getDesc());
        blacklistSheet.setSheetNum(4);
        return blacklistSheet;
    }

    private List<BlacklistExcelVO> blacklist2ExcelModel(List<BlacklistResult> blacklistResults) {
        if (CollectionUtils.isEmpty(blacklistResults)) {
            return Collections.emptyList();
        }
        return blacklistResults.stream().map(result -> {
            BlacklistExcelVO model = new BlacklistExcelVO();
            model.setRedisKey(result.getRedisKey());
            model.setUseYn(result.getUseYn());
            return model;
        }).collect(Collectors.toList());
    }

    /**
     * ?????? ?????????/??? sheet ??????
     *
     * @param applicationId ??????id
     * @return sheet sheet ??????
     */
    private ExcelSheetVO<?> getShadowSheet(Long applicationId) {
        // ????????????id, ?????? ds ??????
        List<ApplicationDsManageEntity> applicationDsManages = applicationDsManageDao.listByApplicationId(
                applicationId);
        // ?????????????????????????????????
        ApplicationDsQueryParam queryParam = new ApplicationDsQueryParam();
        queryParam.setApplicationId(applicationId);
        queryParam.setIsDeleted(0);
        List<ApplicationDsCacheManageDetailResult> caches = dsCacheManageDAO.selectList(queryParam);
        List<ApplicationDsDbManageDetailResult> dbs = dsDbManageDAO.selectList(queryParam);

        // ??????????????????
        ExcelSheetVO<ApplicationDsManageExportVO> sheet = new ExcelSheetVO<>();
        sheet.setExcelModelClass(ApplicationDsManageExportVO.class);
        sheet.setSheetName(AppConfigSheetEnum.DADABASE.getDesc());
        sheet.setSheetNum(AppConfigSheetEnum.DADABASE.getSheetNumber());

        List<ApplicationDsManageExportVO> exportList = new ArrayList<>();
        // ds ????????????
        if (!applicationDsManages.isEmpty()) {
            exportList.addAll(applicationDsManages.stream()
                    .map(ds -> {
                        // ?????????, ?????????, ???????????? config
                        ApplicationDsManageExportVO dsExportVO = new ApplicationDsManageExportVO();
                        // ??????, ??????, ??????, ???...
                        BeanUtils.copyProperties(ds, dsExportVO);

                        // ??????????????????, ???????????? ?????????/??? ??????
                        if (StringUtils.isBlank(ds.getParseConfig())) {
                            return dsExportVO;
                        }

                        // ???????????????, ???????????????, ??????????????????
                        if (!(DsManageUtil.isNewVersionSchemaDsType(ds.getDsType(), isNewVersion))) {
                            return dsExportVO;
                        }

                        Configurations configurations = JsonUtil.json2Bean(ds.getParseConfig(), Configurations.class);
                        if (configurations == null) {
                            return dsExportVO;
                        }

                        dsExportVO.setUserName(configurations.getDataSources().get(0).getUsername());

                        // ???????????? 0 0
                        dsExportVO.setConfig("???");

                        // ?????? ?????????/??? ??????
                        DataSource dsDataSource = configurations.getDataSources().get(1);
                        dsExportVO.setShadowDbUrl(dsDataSource.getUrl());
                        dsExportVO.setShadowDbUserName(dsDataSource.getUsername());
                        dsExportVO.setShadowDbPassword(dsDataSource.getPassword());
                        dsExportVO.setShadowDbMinIdle(dsDataSource.getMinIdle());
                        dsExportVO.setShadowDbMaxActive(dsDataSource.getMaxActive());
                        return dsExportVO;
                    })
                    .collect(Collectors.toList()));
        }


        // ?????????, ??????, ?????? sheet


        if (!caches.isEmpty()) {
            exportList.addAll(caches.stream().map(cache -> {
                ApplicationDsManageExportVO dsExportVO = new ApplicationDsManageExportVO();
                BeanUtils.copyProperties(cache, dsExportVO);
                dsExportVO.setConnPoolName(cache.getCacheName());
                dsExportVO.setDbType(DbTypeEnum.CACHE.getCode());
                dsExportVO.setUrl(cache.getColony());
                dsExportVO.setType(cache.getType());
                dsExportVO.setFileExtend(cache.getFileExtedn());
                dsExportVO.setShaDowFileExtend(cache.getShaDowFileExtedn());
                dsExportVO.setConfigJson(cache.getConfigJson());
                dsExportVO.setSource(cache.getSource());
                return dsExportVO;
            }).collect(Collectors.toList()));
        }

        if (!dbs.isEmpty()) {
            exportList.addAll(dbs.stream().map(db -> {
                ApplicationDsManageExportVO dsExportVO = new ApplicationDsManageExportVO();
                BeanUtils.copyProperties(db, dsExportVO);
                dsExportVO.setDbType(DbTypeEnum.DB.getCode());
                dsExportVO.setConfigJson(db.getConfigJson());
                dsExportVO.setSource(db.getSource());
                dsExportVO.setFileExtend(db.getFileExtedn());
                dsExportVO.setShaDowFileExtend(db.getShaDowFileExtedn());
                return dsExportVO;
            }).collect(Collectors.toList()));
        }
        sheet.setData(exportList);
        return sheet;
    }

    /**
     * ??????????????? ??????
     *
     * @param applicationId ??????id
     * @return sheet sheet ??????
     */
    private ExcelSheetVO<ShadowConsumerExcelVO> getShadowConsumerSheet(Long applicationId) {
        List<ShadowMqConsumerEntity> shadowMqConsumers = shadowMqConsumerDAO.listByApplicationId(applicationId);
        List<ShadowConsumerExcelVO> excelModelList = this.shadowConsumer2ExcelModel(shadowMqConsumers);
        ExcelSheetVO<ShadowConsumerExcelVO> shadowConsumerSheet = new ExcelSheetVO<>();
        shadowConsumerSheet.setData(excelModelList);
        shadowConsumerSheet.setExcelModelClass(ShadowConsumerExcelVO.class);
        shadowConsumerSheet.setSheetName(AppConfigSheetEnum.CONSUMER.getDesc());
        shadowConsumerSheet.setSheetNum(3);
        return shadowConsumerSheet;
    }

    @Override
    public Response<String> buildExportDownLoadConfigUrl(String appId, HttpServletRequest request) {
        if (StringUtil.isEmpty(appId)) {
            return Response.success("");
        }
        String builder = "/application/center/app/config/export?id=" + appId;
        return Response.success(builder);
    }

    @Override
    public Response<Boolean> appDsConfigIsNewVersion() {
        return Response.success(isNewVersion);
    }

    @Override
    public String getApplicationNameByApplicationId(Long applicationId) {
        // ????????????
        ApplicationDetailResult application = applicationDAO.getApplicationById(applicationId);
        // ??????
        if (application == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_VALIDATE_ERROR, "???????????????!");
        }
        return application.getApplicationName();
    }

    @Override
    public void uninstallAllAgent(List<String> appIds) {
        try {
            appIds = this.filterAppIds(appIds,AgentConstants.UNINSTALL);
            if (CollectionUtils.isEmpty(appIds)){
                log.info("?????????????????????????????????????????????");
                return;
            }
            // ??????????????????
            ApplicationQueryParam queryParam = new ApplicationQueryParam();
            queryParam.setPageSize(-1);
            queryParam.setCurrentPage(-1);
            queryParam.setApplicationIds(appIds.stream().map(Long::valueOf).collect(Collectors.toList()));
            PagingList<ApplicationDetailResult> applicationList = applicationDAO.queryApplicationList(queryParam);
            if (CollectionUtil.isEmpty(applicationList.getList())) {
                return;
            }
            for (ApplicationDetailResult application : applicationList.getList()) {
                try {
                    ApplicationNodeOperateProbeRequest nodeRequest = new ApplicationNodeOperateProbeRequest();
                    nodeRequest.setApplicationId(application.getApplicationId());
                    nodeRequest.setAgentId(ProbeConstants.ALL_AGENT_ID);
                    nodeRequest.setAppName(application.getApplicationName());
                    nodeRequest.setOperateType(ApplicationNodeProbeOperateEnum.UNINSTALL.getCode());
                    applicationNodeService.operateProbe(nodeRequest);
                } catch (Exception e) {
                    log.error("????????????????????????, {}", e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("????????????????????????", e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_UNSTALL_AGENT_ERROR, e);

        }
    }

    @Override
    public void modifyAppNodeNum(List<NodeNumParam> numParamList) {
        applicationDAO.batchUpdateAppNodeNum(numParamList, WebPluginUtils.traceEnvCode(),
                WebPluginUtils.traceTenantId());
    }

    /**
     * ????????????????????????
     *
     * @param request ?????????????????????????????????
     */
    @Override
    public Response<List<ApplicationVisualInfoResponse>> getApplicationVisualInfo(
            ApplicationVisualInfoQueryRequest request) {
        //do 1.??????
        ApplicationAttentionParam param = new ApplicationAttentionParam();
        param.setApplicationName(request.getAppName());
        param.setFocus(1);
        param.setTenantId(WebPluginUtils.traceTenantId());
        List<ApplicationAttentionListEntity> attentionList = doGetAttentionList(param);
        // 2.?????????????????????????????????????????????
        List<String> attentionInterfaces = attentionList.stream().map(ApplicationAttentionListEntity::getInterfaceName)
                .collect(Collectors.toList());
        request.setAttentionList(attentionInterfaces);
        request.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        request.setEnvCode(WebPluginUtils.traceEnvCode());
        Map<List<ApplicationVisualInfoResponse>, Integer> infoResponseMap = doGetAppDataByAppName(request);
        List<ApplicationVisualInfoResponse> infoDTOList = new ArrayList<>();
        Integer total = 0;
        if (null != infoResponseMap) {
            Map.Entry<List<ApplicationVisualInfoResponse>, Integer> infoEntry = null;
            for (Map.Entry<List<ApplicationVisualInfoResponse>, Integer> ApplicationVisualInfoEntry :
                    infoResponseMap.entrySet()) {
                infoEntry = ApplicationVisualInfoEntry;
                break;
            }
            infoDTOList = infoEntry.getKey();
            total = infoEntry.getValue();
            //do 2.1????????????????????????ID
            //do 3.???????????????(?????????????????????)
            doGetHealthIndicator(infoDTOList, request);
        }
        return Response.success(infoDTOList, total);
    }

    /**
     * ????????????
     *
     * @param request ????????????????????????????????????
     */
    @Override
    public void attendApplicationService(ApplicationVisualInfoQueryRequest request) throws Exception {
        Map param = new HashMap<>();
        String applicationName = request.getAppName();
        String interfaceName = request.getServiceName();
        param.put("appName", applicationName);
        param.put("interfaceName", interfaceName);
        param.put("isAttend", Boolean.parseBoolean(String.valueOf(request.getAttend())));
        String key = MD5Tool.getMD5(applicationName + interfaceName);
        param.put("id", key);
        param.put("tenantId", WebPluginUtils.traceTenantId());
        param.put("traceEnvCode", WebPluginUtils.traceEnvCode());
        applicationDAO.attendApplicationService(param);
    }

    @Override
    public void gotoActivityInfo(ActivityCreateRequest request) {
        Long linkId = activityService.createActivityWithoutAMDB(request);
        request.setLinkId(String.valueOf(linkId));
    }

    @Override
    public List<ApplicationDetailResult> getAllTenantApp(List<TenantCommonExt> commonExtList) {
        return applicationDAO.getAllTenantApp(commonExtList);
    }

    @Override
    public PagingList<ApplicationListResponseV2> pageApplication(ApplicationQueryRequestV2 request) {
        QueryApplicationParam queryApplicationParam = BeanUtil.copyProperties(request, QueryApplicationParam.class);
        queryApplicationParam.setTenantId(WebPluginUtils.traceTenantId());
        queryApplicationParam.setEnvCode(WebPluginUtils.traceEnvCode());
        queryApplicationParam.setUserIds(WebPluginUtils.getQueryAllowUserIdList());
        queryApplicationParam.setUpdateStartTime(request.getUpdateStartTime());
        queryApplicationParam.setUpdateEndTime(request.getUpdateEndTime());
        IPage<ApplicationListResult> applicationListResultPage = applicationDAO.pageByParam(queryApplicationParam);
        if (org.springframework.util.CollectionUtils.isEmpty(applicationListResultPage.getRecords())){
            return PagingList.empty();
        }
//        if (applicationListResultPage.getTotal() == 0) {
//            return PagingList.empty();
//        }

        List<ApplicationListResult> records = applicationListResultPage.getRecords();
        List<ApplicationListResponseV2> responseList = records.stream().map(result -> {
            ApplicationListResponseV2 response = BeanUtil.copyProperties(result, ApplicationListResponseV2.class);
            response.setId(result.getApplicationId().toString());
            return response;
        }).collect(Collectors.toList());
        return PagingList.of(responseList, applicationListResultPage.getTotal());
    }

    @Override
    public PagingList<ApplicationListByUpgradeResponse> listApplicationByUpgrade(ApplicationListByUpgradeRequest request) {
        QueryApplicationByUpgradeParam param = BeanUtil.copyProperties(request, QueryApplicationByUpgradeParam.class);
        param.setTenantId(WebPluginUtils.traceTenantId());
        param.setEnvCode(WebPluginUtils.traceEnvCode());
        param.setUserIds(WebPluginUtils.getQueryAllowUserIdList());
        IPage<ApplicationListResultByUpgrade> applicationList = applicationDAO.getApplicationList(param);
        if (applicationList.getTotal() == 0) {
            return PagingList.empty();
        }

        List<ApplicationListResultByUpgrade> records = applicationList.getRecords();
        List<ApplicationListByUpgradeResponse> responseList = records.stream().map(result -> {
            ApplicationListByUpgradeResponse response = BeanUtil.copyProperties(result, ApplicationListByUpgradeResponse.class);
            response.setId(result.getApplicationId().toString());
            response.setGmtUpdate(result.getUpdateTime());
            return response;
        }).collect(Collectors.toList());
        return PagingList.of(responseList, applicationList.getTotal());
    }

    @Override
    public Response<String> operateCheck(List<String> appIds, String operate) {
        if (CollectionUtils.isEmpty(appIds) || StringUtil.isEmpty(operate)){
            return Response.fail("????????????");
        }

        ApplicationQueryParam queryParam = new ApplicationQueryParam();
        queryParam.setPageSize(-1);
        queryParam.setCurrentPage(-1);
        queryParam.setApplicationIds(appIds.stream().map(Long::valueOf).collect(Collectors.toList()));
        PagingList<ApplicationDetailResult> pagingList = applicationDAO.queryApplicationList(queryParam);
        if (CollectionUtil.isEmpty(pagingList.getList())) {
            return Response.fail("?????????????????????????????????????????????????????????");
        }
        List<ApplicationDetailResult> applicationList = pagingList.getList();
        List<String> appNames = applicationList.stream().map(ApplicationDetailResult::getApplicationName).collect(
                Collectors.toList());
        List<ApplicationNodeProbeResult> applicationNodeProbeResults = applicationNodeProbeDAO.listByAppNameAndOperate(ApplicationNodeProbeOperateEnum.UNINSTALL.getCode(), appNames);
        long count = applicationNodeProbeResults == null ?  0 : applicationNodeProbeResults.stream().map(ApplicationNodeProbeResult::getApplicationName).distinct().count();
        if (AgentConstants.UNINSTALL.equals(operate)){
            if (count > 0){
                //??????????????????
                List<String> distinct = applicationNodeProbeResults.stream().map(ApplicationNodeProbeResult::getApplicationName).distinct().collect(Collectors.toList());
                StringBuilder sb = new StringBuilder();
                distinct.forEach(s -> {
                    sb.append(s).append("\n");
                });
                return Response.success(String.format("?????????%d?????????,%d??????????????????????????????\n???????????????:",appIds.size(),count) + sb);
            }else {
                return Response.success(String.format("?????????%d?????????,??????????????????",appIds.size()));
            }
        }
        if (AgentConstants.RESUME.equals(operate)){
            if (appIds.size() > count){
                //??????????????????
                List<String> result = appNames;
                if (count != 0){
                    List<String> distinct = applicationNodeProbeResults.stream().map(ApplicationNodeProbeResult::getApplicationName).distinct().collect(Collectors.toList());
                    result = result.stream().filter(o -> !distinct.contains(o)).collect(Collectors.toList());
                }
                StringBuilder sb = new StringBuilder();
                result.forEach(s -> {
                    sb.append(s).append("\n");
                });
                return Response.success(String.format("?????????%d?????????,%d??????????????????????????????\n???????????????:",appIds.size(), appIds.size() - count) + sb);
            }else {
                return Response.success(String.format("?????????%d?????????,????????????",appIds.size()));
            }
        }
        return Response.fail("????????????????????????????????????");
    }

    @Override
    public List<String> filterAppIds(List<String> appIds, String operate) {
        if (CollectionUtils.isEmpty(appIds) || StringUtil.isEmpty(operate)){
            return null;
        }

        ApplicationQueryParam queryParam = new ApplicationQueryParam();
        queryParam.setPageSize(-1);
        queryParam.setCurrentPage(-1);
        queryParam.setApplicationIds(appIds.stream().map(Long::valueOf).collect(Collectors.toList()));
        PagingList<ApplicationDetailResult> pagingList = applicationDAO.queryApplicationList(queryParam);
        if (CollectionUtil.isEmpty(pagingList.getList())) {
            return null;
        }
        List<ApplicationDetailResult> applicationList = pagingList.getList();
        List<String> appNames = applicationList.stream().map(ApplicationDetailResult::getApplicationName).collect(
                Collectors.toList());
        List<ApplicationNodeProbeResult> applicationNodeProbeResults = applicationNodeProbeDAO.
                listByAppNameAndOperate(ApplicationNodeProbeOperateEnum.UNINSTALL.getCode(), appNames);
        List<String> uninstallAppNames = applicationNodeProbeResults.stream().map(ApplicationNodeProbeResult::getApplicationName)
                .collect(Collectors.toList());
        //??????????????????????????????????????????????????????
        if (AgentConstants.UNINSTALL.equals(operate)){
            return applicationList.stream().filter(o -> !uninstallAppNames.contains(o.getApplicationName())).map(o ->
                    o.getApplicationId().toString()).collect(Collectors.toList());
        }
        //??????????????????????????????????????????????????????
        if (AgentConstants.RESUME.equals(operate)){
            return applicationList.stream().filter(o -> uninstallAppNames.contains(o.getApplicationName())).map(o ->
                    o.getApplicationId().toString()).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * ????????????
     *
     * @param param
     */
    private List<ApplicationAttentionListEntity> doGetAttentionList(ApplicationAttentionParam param) {
        return applicationDAO.getAttentionList(param);
    }

    /**
     * ???????????????????????????
     *
     * @param infoDTOList
     * @param request
     */
    private void doGetHealthIndicator(List<ApplicationVisualInfoResponse> infoDTOList,
                                      ApplicationVisualInfoQueryRequest request) {
        if (CollectionUtils.isEmpty(infoDTOList)) {
            return;
        }
        LocalDateTime startTime = request.getStartTimeDate();
        LocalDateTime endTime = request.getEndTimeDate();

        List<String> services = infoDTOList.stream().map(dto -> {
            String service = dto.getAppName() + "#" + dto.getServiceAndMethod() + "#"
                    + dto.getRpcType();
            return service;
        }).collect(Collectors.toList());
        Map<String, List<E2eExceptionConfigInfoExt>> bottleneckConfigMap = linkTopologyService
                .doGetServiceExceptionConfig(services);

        infoDTOList.stream().forEach(dto -> {
            ActivityBottleneckResponse response = activityService.getBottleneckByActivityList(dto, startTime, endTime,
                    bottleneckConfigMap);
            dto.setResponse(response);
        });
    }

    /**
     * ?????????????????????????????????
     */
    private Map<List<ApplicationVisualInfoResponse>, Integer> doGetAppDataByAppName(
            ApplicationVisualInfoQueryRequest request) {
        //do ????????????????????????
        String url = properties.getUrl().getAmdb() + QUERY_METRIC_DATA;
        try {
            AmdbResult<List<ApplicationVisualInfoResponse>> appDataResult = AmdbHelper.builder().httpMethod(
                            HttpMethod.POST)
                    .url(url)
                    .param(request)
                    .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                    .eventName("?????????????????????????????????????????????")
                    .list(ApplicationVisualInfoResponse.class);
            //do ??????????????????
            List<ApplicationVisualInfoResponse> data = appDataResult.getData();
            int total = Math.toIntExact(appDataResult.getTotal());
            List<String> attentionList = request.getAttentionList();
            String orderBy = request.getOrderBy();
            int current = request.getCurrent();
            int pageSize = request.getPageSize();
            return doSortAndPageAndConvertActivityId(data, attentionList, orderBy, pageSize, current, total,
                    request.getNameActivity());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }

    private Map<List<ApplicationVisualInfoResponse>, Integer> doSortAndPageAndConvertActivityId(
            List<ApplicationVisualInfoResponse> data, List<String> attentionList, String orderBy, int pageSize, int current,
            int total, String nameActivity) {
        if (CollectionUtils.isEmpty(data)) {
            return null;
        }

        List<ApplicationVisualInfoResponse> visualInfoDTOList = data.stream().map(dto -> {
            dto.setAttend(attentionList.contains(dto.getServiceAndMethod()));
            String[] activeList = dto.getActiveList();
            Map<String, String> activityResult = new HashMap<>();
            if (activeList != null) {
                for (String active : activeList) {
                    String[] split = active.split("#");
                    String appName = split[0];
                    // ????????????
                    String entrance = ActivityUtil.buildEntrance(split[2], split[1], "%");
                    List<Map<String, Object>> serviceList = activityDAO.findActivityIdByServiceName(appName, entrance);
                    if (!CollectionUtils.isEmpty(serviceList)) {
                        serviceList.forEach(serviceName -> activityResult.put(
                                serviceName.get("linkId").toString(), serviceName.get("linkName").toString()));
                    }
                }
            }
            dto.setActiveIdAndName(activityResult);
            String[] allActiveList = dto.getAllActiveList();
            Map<String, String> allActivityResult = new HashMap<>();
            if (allActiveList != null) {
                for (String active : allActiveList) {
                    String[] split = active.split("#");
                    String appName = split[0];
                    String entrance = ActivityUtil.buildEntrance(split[2], split[1], "%");
                    List<Map<String, Object>> serviceList = activityDAO.findActivityIdByServiceName(appName, entrance);
                    if (!CollectionUtils.isEmpty(serviceList)) {
                        serviceList.stream().forEach(serviceName -> allActivityResult.put(
                                split[1] + "#" + split[2], serviceName.get("linkName").toString()));
                    }
                }
            }
            dto.setAllActiveIdAndName(allActivityResult);
            return dto;
        }).collect(Collectors.toList());

        Map result = new HashMap<>();
        result.put(visualInfoDTOList, total);
        return result;
    }

    /**
     * ??????????????????
     *
     * @param stringArrayListHashMap excel ??????????????????
     */
    private List<String> preCheck(Map<String, ArrayList<ArrayList<String>>> stringArrayListHashMap) {
        List<String> result = new ArrayList<>();
        for (AppConfigSheetEnum sheetEnum : AppConfigSheetEnum.values()) {
            ArrayList<ArrayList<String>> arrayLists = stringArrayListHashMap.get(sheetEnum.name());
            // ???????????????????????????
            if (CollectionUtils.isEmpty(arrayLists)
                    || AppConfigSheetEnum.DADABASE.getDesc().equals(sheetEnum.name())) {
                continue;
            }

            arrayLists.stream()
                    .filter(Objects::nonNull)
                    .forEach(strings -> {
                        Integer columnNum = sheetEnum.getColumnNum();
                        strings = (ArrayList<String>) strings.stream().filter(StringUtil::isNotEmpty).collect(
                                Collectors.toList());
                        if (strings.size() < columnNum) {
                            String msg = sheetEnum.name() + strings;
                            result.add(msg);
                        }
                    });
            if (result.size() == 0) {
                if (AppConfigSheetEnum.PLUGINS_CONFIG.getDesc().equals(sheetEnum.name())) {
                    arrayLists.forEach(t -> {
                        String inputStr = t.get(1);
                        boolean number = NumberUtil.isNumber(inputStr);
                        if (number) {
                            BigDecimal num = new BigDecimal(inputStr);
                            if (num.compareTo(BigDecimal.ZERO) < 0) {
                                result.add(sheetEnum.name() + "???????????????" + num + "?????????0???");
                            }
                        }
                    });
                    //?????????????????????
                    List<ArrayList<String>> notExistList = arrayLists.stream().filter(
                            t -> !CONFIGITEMLIST.contains(t.get(0))).collect(
                            Collectors.toList());
                    if (!CollectionUtils.isEmpty(notExistList)) {
                        String notExist = notExistList.stream().map(t -> t.get(0)).collect(Collectors.joining(","));
                        result.add(sheetEnum.name() + "???????????????" + notExist + "???????????????");
                    }
                    //????????????????????????CONFIGITEMLIST????????? ??????????????????????????????
                    //                HashSet<String> hashSet = new HashSet<>();
                    //                arrayLists.forEach(list->{
                    //                    String configItemImport = list.get(0);
                    //                    if(configItemImport.trim().equals("redis??????key?????????") && !hashSet.add
                    //                    (configItemImport)){
                    //                        result.add(sheetEnum.name() + "???????????????"+configItemImport+"???????????????????????????");
                    //                    }
                    //                });
                }
            }
        }
        return result;
    }

    /**
     * ????????????????????????db
     */
    private void saveConfig2Db(Long applicationId, Map<String, ArrayList<ArrayList<String>>> configMap) {
        ApplicationDetailResult application = applicationDAO.getApplicationById(applicationId);
        if (application == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_VALIDATE_ERROR, "???????????????!");
        }

        // ?????? ?????????/??? ??????????????????????????????????????????
        // this.saveDsFromImport(applicationId, configMap.get(AppConfigSheetEnum.DADABASE.getDesc()));

        // ????????????
        if (configMap.containsKey(AppConfigSheetEnum.GUARD.getDesc())) {
            ArrayList<ArrayList<String>> guardList = configMap.get(AppConfigSheetEnum.GUARD.getDesc());
            List<LinkGuardVo> linkGuardVos = appConfigEntityConvertService.convertGuardSheet(guardList);
            if (CollectionUtils.isNotEmpty(linkGuardVos)) {
                linkGuardVos.forEach(guard -> {
                    guard.setApplicationName(application.getApplicationName());
                    guard.setApplicationId(String.valueOf(application.getApplicationId()));
                    LinkGuardQueryParam queryParam = new LinkGuardQueryParam();
                    queryParam.setAppId(applicationId);
                    queryParam.setMethodInfo(guard.getMethodInfo());
                    Response<List<LinkGuardVo>> response = linkGuardService.selectByExample(queryParam);
                    if (CollectionUtils.isNotEmpty(response.getData())) {
                        LinkGuardVo guardVo = response.getData().get(0);
                        guardVo.setGroovy(guard.getGroovy());
                        guardVo.setIsEnable(guard.getIsEnable());
                        guardVo.setUpdateTime(new Date());
                        guardVo.setRemark(guard.getRemark());
                        linkGuardService.updateGuard(guardVo);
                    } else {
                        linkGuardService.addGuard(guard);
                    }
                });
            }
        }

        // ?????? job
        if (configMap.containsKey(AppConfigSheetEnum.JOB.getDesc())) {
            ArrayList<ArrayList<String>> arrayLists = configMap.get(AppConfigSheetEnum.JOB.getDesc());
            List<TShadowJobConfig> tShadowJobConfigs = appConfigEntityConvertService.convertJobSheet(arrayLists);
            if (CollectionUtils.isNotEmpty(tShadowJobConfigs)) {
                tShadowJobConfigs.forEach(job -> {
                    try {
                        job.setApplicationId(application.getApplicationId());
                        shadowJobConfigService.insert(job);
                    } catch (DocumentException e) {
                        log.error(e.getMessage());
                    }
                });
            }
        }

        // ???????????????
        this.saveWhiteListFromImport(applicationId, configMap);

        // ???????????????
        this.saveBlacklistFromImport(applicationId, configMap);

        // ???????????????????????????
        if (configMap.containsKey(AppConfigSheetEnum.CONSUMER.getDesc())) {
            ArrayList<ArrayList<String>> arrayLists = configMap.get(AppConfigSheetEnum.CONSUMER.getDesc());
            List<ShadowConsumerCreateInput> shadowConsumerCreateInputs = appConfigEntityConvertService.converComsumerList(arrayLists);
            if (CollectionUtils.isNotEmpty(shadowConsumerCreateInputs)) {
                shadowConsumerCreateInputs.forEach(request -> {
                    request.setApplicationId(applicationId);

                    try {
                        ShadowConsumerQueryInput queryRequest = new ShadowConsumerQueryInput();
                        queryRequest.setType(request.getType());
                        queryRequest.setTopicGroup(request.getTopicGroup());
                        queryRequest.setApplicationId(applicationId);
                        // ????????????
                        if (shadowConsumerService.exist(queryRequest)) {
                            ShadowConsumerUpdateInput updateRequest = new ShadowConsumerUpdateInput();
                            BeanUtils.copyProperties(request, updateRequest);
                            shadowConsumerService.importUpdateMqConsumers(updateRequest);
                        } else {
                            shadowConsumerService.createMqConsumers(request);
                        }
                    } catch (Exception e) {
                        throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_CONFIG_FILE_IMPORT_ERROR,
                                "???????????????????????????!????????????:" + e.getMessage(), e);
                    }
                });
            }
        }

        //???????????????????????????
        this.savePluginsListFromImport(application, configMap);
        // ??????????????????
        this.saveRemoteCallFromImport(application, configMap);

        // ????????????
        agentConfigCacheManager.evict(application.getApplicationName());
    }

    private void saveRemoteCallFromImport(ApplicationDetailResult detailResult, Map<String, ArrayList<ArrayList<String>>> configMap) {
        // map ????????????
        ArrayList<ArrayList<String>> importRemoteCall;
        if ((importRemoteCall = configMap.get(AppConfigSheetEnum.REMOTE_CALL.getDesc())) == null) {
            return;
        }
        // ??????
        List<AppRemoteCallUpdateParam> inputs = appConfigEntityConvertService.converRemoteCall(importRemoteCall, detailResult);
        if (CollectionUtils.isEmpty(inputs)) {
            return;
        }
        appRemoteCallDAO.batchSaveOrUpdate(inputs);
    }

    private void savePluginsListFromImport(ApplicationDetailResult application,
                                           Map<String, ArrayList<ArrayList<String>>> configMap) {
        List<ArrayList<String>> lists = configMap.get(AppConfigSheetEnum.PLUGINS_CONFIG.getDesc());
        if (CollectionUtils.isEmpty(lists)) {
            return;
        }
        //??????????????????????????????
        ApplicationPluginsConfigParam param = new ApplicationPluginsConfigParam();
        param.setApplicationId(application.getApplicationId());
        List<ApplicationPluginsConfigEntity> list = pluginsConfigDAO.findList(param);
        if (CollectionUtils.isEmpty(list)) {
            list = Collections.emptyList();
        }
        //??????????????????????????????
        List<String> configItemResult = list.stream().map(ApplicationPluginsConfigEntity::getConfigItem).collect(
                Collectors.toList());
        //?????????????????????
        List<ArrayList<String>> existList = lists.stream().filter(t -> configItemResult.contains(t.get(0))).collect(
                Collectors.toList());
        //?????????????????????
        List<ArrayList<String>> notExistList = lists.stream().filter(t -> !configItemResult.contains(t.get(0))).collect(
                Collectors.toList());

        if (CollectionUtils.isNotEmpty(notExistList)) {
            List<ApplicationPluginsConfigParam> saveParamList = Lists.newArrayList();
            notExistList.forEach(t -> {
                if ("redis??????key?????????".equals(t.get(0))) {
                    ApplicationPluginsConfigParam configParam = new ApplicationPluginsConfigParam();
                    configParam.setConfigItem("redis??????key?????????");
                    configParam.setConfigKey("redis_expire");
                    configParam.setConfigDesc("??????????????????redis??????key???????????????????????????key??????????????????????????????????????????key???????????????????????????????????????key??????????????????");
                    configParam.setConfigValue("?????????key??????".equals(t.get(1).trim()) ? "-1" : t.get(1));
                    configParam.setApplicationName(application.getApplicationName());
                    configParam.setApplicationId(application.getApplicationId());
                    saveParamList.add(configParam);
                }
            });
            //????????????
            pluginsConfigService.addBatch(saveParamList);
        }
        if (CollectionUtils.isNotEmpty(existList)) {
            List<ApplicationPluginsConfigParam> updateParamList = Lists.newArrayList();
            List<ApplicationPluginsConfigEntity> finalList = list;
            existList.forEach(t -> {
                if ("redis??????key?????????".equals(t.get(0))) {
                    List<ApplicationPluginsConfigEntity> entityList = finalList.stream().filter(
                            entity -> "redis_expire".equals(entity.getConfigKey())).collect(Collectors.toList());
                    ApplicationPluginsConfigParam configParam = new ApplicationPluginsConfigParam();
                    configParam.setConfigItem("redis??????key?????????");
                    configParam.setConfigKey("redis_expire");
                    configParam.setConfigValue("?????????key??????".equals(t.get(1).trim()) ? "-1" : t.get(1));
                    configParam.setId(entityList.get(0).getId());
                    configParam.setApplicationName(application.getApplicationName());
                    configParam.setApplicationId(application.getApplicationId());
                    updateParamList.add(configParam);
                }
            });

            //????????????
            pluginsConfigService.updateBatch(updateParamList);
        }
    }

    private void saveBlacklistFromImport(Long applicationId, Map<String, ArrayList<ArrayList<String>>> configMap) {
        // map ????????????
        ArrayList<ArrayList<String>> importBlackLists;
        if ((importBlackLists = configMap.get(AppConfigSheetEnum.BLACK.getDesc())) == null) {
            return;
        }
        // ??????
        List<BlacklistCreateNewParam> whiteListCreateLists = appConfigEntityConvertService.converBlackList(
                importBlackLists, applicationId);
        if (CollectionUtils.isEmpty(whiteListCreateLists)) {
            return;
        }
        // ??????
        BlacklistSearchParam param = new BlacklistSearchParam();
        param.setApplicationId(applicationId);
        // todo ????????????????????????????????????????????????????????????
        List<BlacklistResult> results = blackListDAO.selectList(param);
        List<BlacklistCreateNewParam> createNewParams = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(results)) {
            Map<String, List<BlacklistResult>> blacklistMap = results.stream().collect(
                    Collectors.groupingBy(BlacklistResult::getRedisKey));
            whiteListCreateLists.forEach(create -> {
                List<BlacklistResult> list = blacklistMap.get(create.getRedisKey());
                if (CollectionUtils.isNotEmpty(list)) {
                    list.forEach(updateResult -> {
                        BlacklistUpdateParam updateParam = new BlacklistUpdateParam();
                        BeanUtils.copyProperties(updateResult, updateParam);
                        updateParam.setUseYn(create.getUseYn());
                        blackListDAO.update(updateParam);
                    });
                } else {
                    createNewParams.add(create);
                }
            });
        } else {
            createNewParams.addAll(whiteListCreateLists);
        }
        if (CollectionUtils.isNotEmpty(createNewParams)) {
            blackListDAO.batchInsert(createNewParams);
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param applicationId ??????id
     */
    private void saveWhiteListFromImport(Long applicationId, Map<String, ArrayList<ArrayList<String>>> configMap) {
        // map ????????????
        ArrayList<ArrayList<String>> importWhiteLists;
        if ((importWhiteLists = configMap.get(AppConfigSheetEnum.WHITE.getDesc())) == null) {
            return;
        }
        // ??????
        List<WhitelistImportFromExcelInput> inputs = appConfigEntityConvertService.converWhiteList(importWhiteLists);
        if (CollectionUtils.isEmpty(inputs)) {
            return;
        }
        inputs.forEach(vo -> vo.setApplicationId(applicationId));
        whiteListService.importWhiteListFromExcel(inputs);
    }

    /**
     * ?????? ds, ?????????
     *
     * @param applicationId ??????id
     * @param dsObjects     ?????????/??? ????????????(????????? list ??????)
     */
    private void saveDsFromImport(Long applicationId, ArrayList<ArrayList<String>> dsObjects) {
        if (CollectionUtils.isEmpty(dsObjects)) {
            return;
        }

        for (ArrayList<String> ds : dsObjects) {
            // ds ????????? @see ApplicationDsManageExportVO

            // ??????????????????
            ApplicationDsCreateInput dsCreateRequest = this.getApplicationDsCreateInput(applicationId, ds);

            // ?????? url ?????? ??????????????????
            List<ApplicationDsManageEntity> applicationDsManages = applicationDsManageDao.listByApplicationIdAndDsType(
                    applicationId, dsCreateRequest.getDsType());

            // ?????????, ?????????, ??????Server ???????????????
            if (applicationDsManages.isEmpty()) {
                this.createDsFromImport(dsCreateRequest);
                continue;
            }

            // ???????????????
            ApplicationDsManageEntity applicationDsManage = applicationDsManages.stream()
                    .filter(item -> this.isSameFromImport(dsCreateRequest, item))
                    .findFirst().orElse(null);
            if (applicationDsManage == null) {
                this.createDsFromImport(dsCreateRequest);
            } else {
                this.updateDsFromImport(applicationDsManage, dsCreateRequest);
            }
        }
    }

    /**
     * ??????????????????????????? ds ????????????
     *
     * @param dsCreateRequest     ???????????????
     * @param applicationDsManage ???????????????
     * @return ????????????
     */
    private boolean isSameFromImport(ApplicationDsCreateInput dsCreateRequest,
                                     ApplicationDsManageEntity applicationDsManage) {
        boolean same = false;

        if (DsManageUtil.isEsServerType(dsCreateRequest.getDsType())) {
            return compareImportEsConfig(dsCreateRequest, applicationDsManage);
        }

        if (DsManageUtil.isHbaseServerType(dsCreateRequest.getDsType())) {
            return compareImportEsConfig(dsCreateRequest, applicationDsManage);
        }

        // ??????server, ???????????? master, nodes ??????????????????
        if (DsManageUtil.isServerDsType(dsCreateRequest.getDsType())) {
            // ????????????
            same = this.compareImportServerAndOriginServer(dsCreateRequest.getConfig(),
                    applicationDsManage.getConfig());
        }

        // ??????????????? url
        if (DsManageUtil.isTableDsType(dsCreateRequest.getDsType())) {
            same = Objects.equals(applicationDsManage.getUrl(), dsCreateRequest.getUrl());
        }

        // ??????????????? url
        if (DsManageUtil.isSchemaDsType(dsCreateRequest.getDsType())) {
            if (isNewVersion) {
                // ??????, ???????????? url
                same = Objects.equals(applicationDsManage.getUrl(), dsCreateRequest.getUrl());
            } else {
                // ??????, ?????? config
                same = Objects.equals(applicationDsManage.getUrl(),
                        dsService.parseShadowDbUrl(dsCreateRequest.getConfig()));
            }
        }
        return same;
    }

    /**
     * ??????Hbase????????????
     *
     * @param dsCreateRequest     ????????????
     * @param applicationDsManage ???????????????
     * @return -
     */
    private boolean compareImportHbaseConfig(ApplicationDsCreateInput dsCreateRequest,
                                             ApplicationDsManageEntity applicationDsManage) {
        return compareImportEsConfig(dsCreateRequest, applicationDsManage);
    }

    /**
     * ??????ES????????????
     *
     * @param dsCreateRequest     ????????????
     * @param applicationDsManage ???????????????
     * @return -
     */
    private boolean compareImportEsConfig(ApplicationDsCreateInput dsCreateRequest,
                                          ApplicationDsManageEntity applicationDsManage) {
        boolean same;
        String url = dsCreateRequest.getUrl();
        String exist = applicationDsManage.getUrl();
        same = url.trim().equals(exist.trim());
        return same;
    }

    /**
     * ?????????, ??????ds
     *
     * @param applicationDsManage ???????????? ds
     * @param dsCreateRequest     ??????????????????
     */
    private void updateDsFromImport(ApplicationDsManageEntity applicationDsManage,
                                    ApplicationDsCreateInput dsCreateRequest) {
        ApplicationDsUpdateInput updateRequest = new ApplicationDsUpdateInput();
        BeanUtils.copyProperties(dsCreateRequest, updateRequest);
        updateRequest.setId(applicationDsManage.getId());
        updateRequest.setApplicationName(applicationDsManage.getApplicationName());
        Response response = dsService.dsUpdate(updateRequest);
        // ????????????
        if (response.getError() != null && FALSE_CORE.equals(response.getError().getCode())) {
            throw new RuntimeException(response.getError().getMsg());
        }
    }

    /**
     * ?????????, ??????ds
     *
     * @param dsCreateRequest ??????????????????
     */
    private void createDsFromImport(ApplicationDsCreateInput dsCreateRequest) {
        // ?????????, ??????
        Response response = dsService.dsAdd(dsCreateRequest);
        // ????????????
        if (response.getError() != null && FALSE_CORE.equals(response.getError().getCode())) {
            throw new RuntimeException(response.getError().getMsg());
        }
    }

    /**
     * ???????????? ??????server ????????????
     * ?????? master ??? nodes
     *
     * @param importServerConfig ?????????server??????
     * @param originServerConfig ?????????server??????
     * @return ????????????
     */
    private boolean compareImportServerAndOriginServer(String importServerConfig, String originServerConfig) {
        // ???????????????
        ShadowServerConfigurationResponse importServerConfigResponse = JsonUtil.json2Bean(importServerConfig,
                ShadowServerConfigurationResponse.class);

        // ???????????????
        ShadowServerConfigurationResponse originServerConfigResponse = JsonUtil.json2Bean(originServerConfig,
                ShadowServerConfigurationResponse.class);

        // ??????
        return String.format("%s%s", importServerConfigResponse.getDataSourceBusiness().getMaster(),
                        importServerConfigResponse.getDataSourceBusiness().getNodes())
                .equals(String.format("%s%s", originServerConfigResponse.getDataSourceBusiness().getMaster(),
                        importServerConfigResponse.getDataSourceBusiness().getNodes()));
    }

    /**
     * ????????????, ??????????????????
     *
     * @param applicationId ??????id
     * @param ds            ?????????/??? ??????
     * @return ??????????????????
     */
    private ApplicationDsCreateInput getApplicationDsCreateInput(Long applicationId, ArrayList<String> ds) {
        // ?????????, ??????????????? 0 0

        // ??????, ???????????????2?????????, ???????????? dsType...
        Assert.isTrue(ds.size() >= 2, "??????????????????!");

        ApplicationDsCreateInput createRequest = new ApplicationDsCreateInput();
        int index = 0;
        String dbTypeString = ds.get(index++);
        Assert.isTrue(StringUtils.isNotBlank(dbTypeString), "???????????? ????????????!");
        createRequest.setDbType(Integer.valueOf(dbTypeString));

        // ??????????????????
        String dsTypeString = ds.get(index++);
        Assert.isTrue(StringUtils.isNotBlank(dsTypeString), "???????????? ????????????!");
        Integer dsType = Integer.valueOf(dsTypeString);
        createRequest.setDsType(dsType);

        // ??????????????????, ??????
        if (DsManageUtil.isNewVersionSchemaDsType(dsType, isNewVersion)) {
            // ??????agent, ?????????, ?????????7???????????????, xml ????????????, ??????, ?????? ?????????
            Assert.isTrue(ds.size() >= 9, "??????????????????!");
        } else {
            // ?????????5???
            Assert.isTrue(ds.size() >= 5, "??????????????????!");
        }

        // url ??????
        String url = ds.get(index++);
        Assert.isTrue(StringUtils.isNotBlank(url), "url ????????????!");
        createRequest.setUrl(url);

        // ????????????
        // ????????????
        String statusString = ds.get(index++);
        Assert.isTrue(StringUtils.isNotBlank(statusString), "?????? ????????????!");
        createRequest.setStatus(Integer.valueOf(statusString));

        // ?????? ?????? ??????????????? by ?????? 2022.3.2
        String config = ds.get(index++);
        //Assert.isTrue(StringUtils.isNotBlank(config), "xml ????????????!");
        createRequest.setConfig(config);

        // ??????agent ?????????, ???????????????, ???????????????
        createRequest.setOldVersion(!isNewVersion);
        createRequest.setApplicationId(applicationId);

        // ?????????, ??????????????????
        if (DsManageUtil.isNewVersionSchemaDsType(dsType, isNewVersion)) {
            createRequest.setConfig("");

            String userName = ds.get(index++);
            String shadowDbUrl = ds.get(index++);
            String shadowDbUserName = ds.get(index++);
            String shadowDbPassword = ds.get(index++);

            // ???????????????
            Assert.isTrue(StringUtils.isNotBlank(userName), "????????? ????????????!");
            Assert.isTrue(StringUtils.isNotBlank(shadowDbUrl), "?????????url ????????????!");
            Assert.isTrue(StringUtils.isNotBlank(shadowDbUserName), "?????????????????? ????????????!");
            Assert.isTrue(StringUtils.isNotBlank(shadowDbPassword), "??????????????? ????????????!");

            createRequest.setUserName(userName);
            createRequest.setShadowDbUrl(shadowDbUrl);
            createRequest.setShadowDbUserName(shadowDbUserName);
            createRequest.setShadowDbPassword(shadowDbPassword);

            // ???????????????, ??????agent, 10?????????, ?????????????????????
            if (ds.size() > 9) {
                createRequest.setShadowDbMinIdle(ds.get(index++));
            }

            if (ds.size() > 10) {
                createRequest.setShadowDbMaxActive(ds.get(index));
            }
        }
        return createRequest;
    }

    /**
     * ????????????, ?????? url
     *
     * @param dsType ????????????
     * @param url    ????????? url
     * @param config ??????
     * @return url
     */
    private String getUrlFromImport(Integer dsType, String url, String config) {
        // , url ??????????????????
        if (DsManageUtil.isServerDsType(dsType)) {
            ShadowServerConfigurationResponse serverConfig = JsonUtil.json2Bean(config,
                    ShadowServerConfigurationResponse.class);
            return serverConfig.getDataSourceBusiness().getNodes();
        }

        // ??????agent, ?????????
        if (DsManageUtil.isSchemaDsType(dsType) && !isNewVersion) {
            return dsService.parseShadowDbUrl(config);
        }

        // ???????????? url ??????
        return url;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Response<ImportConfigDTO> importApplicationConfig(MultipartFile file, Long applicationId) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_CONFIG_FILE_VALIDATE_ERROR, "???????????????!");
        }

        if (!originalFilename.endsWith(".xlsx")) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_CONFIG_FILE_VALIDATE_ERROR,
                    "?????????????????????????????????xlsx?????????????????????????????????????????????");
        }

        // ??????xlsx??????
        Map<String, ArrayList<ArrayList<String>>> stringArrayListHashMap = ExcelUtils.readExcelForXlsx(file, 0);

        if (CollectionUtils.isEmpty(stringArrayListHashMap)) {
            return null;
        }

        // ????????????
        List<String> list = this.preCheck(stringArrayListHashMap);
        if (CollectionUtils.isNotEmpty(list)) {
            ImportConfigDTO dto = new ImportConfigDTO();
            dto.setMsg(list);
            return Response.success(dto);
        }

        // ????????????????????????db
        this.saveConfig2Db(applicationId, stringArrayListHashMap);
        return null;
    }

    /**
     * ????????????sheet
     *
     * @param applicationId ??????id
     * @return sheet sheet ??????
     */
    private ExcelSheetVO<LinkGuardExcelVO> getLinkGuardSheet(Long applicationId) {
        List<LinkGuardEntity> linkGuards = linkGuardDAO.listFromExportByApplicationId(applicationId);
        List<LinkGuardExcelVO> linkGuardExcelModelList = this.linkGuard2ExcelModel(linkGuards);
        ExcelSheetVO<LinkGuardExcelVO> guardExcelSheet = new ExcelSheetVO<>();
        guardExcelSheet.setData(linkGuardExcelModelList);
        guardExcelSheet.setExcelModelClass(LinkGuardExcelVO.class);
        guardExcelSheet.setSheetName(AppConfigSheetEnum.GUARD.getDesc());
        guardExcelSheet.setSheetNum(0);
        return guardExcelSheet;
    }

    /**
     * ??????job sheet
     *
     * @param applicationId ??????id
     * @return sheet sheet ??????
     */
    private ExcelSheetVO<ShadowJobExcelVO> getJobSheet(Long applicationId) {
        List<ShadowJobConfigEntity> shadowJobConfigs = shadowJobConfigDAO.listByApplicationId(applicationId);
        List<ShadowJobExcelVO> jobExcelModelList = this.job2ExcelJobModel(shadowJobConfigs);
        ExcelSheetVO<ShadowJobExcelVO> jobSheet = new ExcelSheetVO<>();
        jobSheet.setData(jobExcelModelList);
        jobSheet.setExcelModelClass(ShadowJobExcelVO.class);
        jobSheet.setSheetName(AppConfigSheetEnum.JOB.getDesc());
        jobSheet.setSheetNum(1);
        return jobSheet;
    }

    /**
     * ???????????????sheet
     *
     * @param application ????????????
     * @return sheet sheet ??????
     */
    private ExcelSheetVO<WhiteListExcelVO> getWhiteListSheet(ApplicationDetailResult application) {
        List<WhitelistResult> whiteLists = whiteListDAO.listByApplicationId(application.getApplicationId());
        List<InterfaceVo> whiteListsFromAmDb = whiteListService.getAllInterface(application.getApplicationName());
        List<WhiteListExcelVO> whiteListExcelModels = this.whiteList2ExcelModel(whiteLists, whiteListsFromAmDb);
        ExcelSheetVO<WhiteListExcelVO> whiteSheet = new ExcelSheetVO<>();
        whiteSheet.setData(whiteListExcelModels);
        whiteSheet.setExcelModelClass(WhiteListExcelVO.class);
        whiteSheet.setSheetName(AppConfigSheetEnum.WHITE.name());
        whiteSheet.setSheetNum(2);
        return whiteSheet;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param shadowMqConsumers ???????????????????????????
     * @return ??????????????????
     */
    private List<ShadowConsumerExcelVO> shadowConsumer2ExcelModel(List<ShadowMqConsumerEntity> shadowMqConsumers) {
        if (CollectionUtils.isEmpty(shadowMqConsumers)) {
            return Collections.emptyList();
        }
        return shadowMqConsumers.stream().map(response -> {
            ShadowConsumerExcelVO model = new ShadowConsumerExcelVO();
            model.setTopicGroup(response.getTopicGroup());
            model.setType(response.getType());
            model.setStatus(response.getStatus());
            return model;
        }).collect(Collectors.toList());
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param whiteLists         ?????????????????????
     * @param whiteListsFromAmDb ??????????????????????????????
     * @return ??????????????????
     */
    private List<WhiteListExcelVO> whiteList2ExcelModel(List<WhitelistResult> whiteLists,
                                                        List<InterfaceVo> whiteListsFromAmDb) {

        List<WhiteListExcelVO> models = new ArrayList<>();
        // ???????????????
        List<String> armdString = whiteLists.stream().map(WhitelistResult::getInterfaceName).collect(
                Collectors.toList());
        List<String> existWhite = whiteListService.getExistWhite(armdString, Lists.newArrayList());
        if (CollectionUtils.isNotEmpty(whiteLists)) {
            // ????????????
            List<Long> ids = whiteLists.stream().map(WhitelistResult::getWlistId).collect(Collectors.toList());
            List<WhitelistEffectiveAppResult> appResults = whitelistEffectiveAppDao.getListByWhiteIds(ids);
            Map<Long, List<WhitelistEffectiveAppResult>> appResultsMap = appResults.stream()
                    .collect(Collectors.groupingBy(WhitelistEffectiveAppResult::getWlistId));
            models = whiteLists.stream().map(whiteList -> {
                WhiteListExcelVO model = new WhiteListExcelVO();
                BeanUtils.copyProperties(whiteList, model);
                model.setIsGlobal(getIsGlobal(existWhite, whiteList.getType(), whiteList.getInterfaceName(),
                        whiteList.getIsGlobal()));
                model.setIsHandwork(
                        whiteList.getIsHandwork() == null ? BooleanEnum.getByValue(BooleanEnum.TRUE.getValue()) :
                                BooleanEnum.getByValue(whiteList.getIsHandwork()));
                // ????????????
                List<WhitelistEffectiveAppResult> results = appResultsMap.get(whiteList.getWlistId());
                model.setEffectAppNames(CollectionUtils.isNotEmpty(results) ?
                        results.stream().map(WhitelistEffectiveAppResult::getEffectiveAppName)
                                .collect(Collectors.joining(",")) : "");
                return model;
            }).collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(whiteListsFromAmDb)) {
            models.addAll(whiteListsFromAmDb.stream().map(whiteList -> {
                WhiteListExcelVO model = new WhiteListExcelVO();
                model.setInterfaceName(whiteList.getInterfaceName());
                model.setType(whiteList.getInterfaceType());
                model.setUseYn(STATUS_NOT_JOIN);
                model.setDictType(DEFAULT_DICT_TYPE);
                model.setIsGlobal(
                        getIsGlobal(existWhite, model.getType(), model.getInterfaceName(), BooleanEnum.TRUE.getValue()));
                model.setIsHandwork(BooleanEnum.FALSE.getDesc());
                model.setEffectAppNames("");
                return model;
            }).collect(Collectors.toList()));
        }

        if (models.isEmpty()) {
            return models;
        }

        // ?????? map ??????
        Map<String, WhiteListExcelVO> keyAboutWhiteList = models.stream().collect(Collectors
                .toMap(model -> WhiteListUtil.getInterfaceAndType(model.getInterfaceName(), model.getType()),
                        Function.identity(), (v1, v2) -> v1));
        return new ArrayList<>(keyAboutWhiteList.values());
    }

    private String getIsGlobal(List<String> existWhite, String type, String interfaceName, Boolean isGlobal) {
        if (isCheckDuplicateName) {
            if (WhitelistUtil.isDuplicate(existWhite, WhitelistUtil.buildWhiteId(type, interfaceName))) {
                return BooleanEnum.getByValue(BooleanEnum.FALSE.getValue());
            } else {
                return isGlobal == null ? BooleanEnum.getByValue(BooleanEnum.TRUE.getValue()) : BooleanEnum.getByValue(
                        isGlobal);
            }
        } else {
            return isGlobal == null ? BooleanEnum.getByValue(BooleanEnum.TRUE.getValue()) : BooleanEnum.getByValue(
                    isGlobal);
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param linkGuardEntities ????????????
     * @return ?????????????????????
     */
    private List<LinkGuardExcelVO> linkGuard2ExcelModel(List<LinkGuardEntity> linkGuardEntities) {
        List<LinkGuardExcelVO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(linkGuardEntities)) {
            return result;
        }
        linkGuardEntities.forEach(source -> {
            LinkGuardExcelVO model = new LinkGuardExcelVO();
            BeanUtils.copyProperties(source, model);
            model.setIsEnable(GuardEnableConstants.GUARD_ENABLE == source.getIsEnable());
            result.add(model);
        });
        return result;
    }

    /**
     * ????????????????????????
     *
     * @param shadowJobConfigs ????????????
     * @return ??????????????????
     */
    private List<ShadowJobExcelVO> job2ExcelJobModel(List<ShadowJobConfigEntity> shadowJobConfigs) {
        if (CollectionUtils.isEmpty(shadowJobConfigs)) {
            return Collections.emptyList();
        }
        return shadowJobConfigs.stream().map(source -> {
            ShadowJobExcelVO model = new ShadowJobExcelVO();
            BeanUtils.copyProperties(source, model);
            return model;
        }).collect(Collectors.toList());
    }

    /**
     * ????????????????????????
     *
     * @return -
     */
    private ApplicationSwitchStatusDTO judgeAppSwitchStatus(ApplicationDetailResult dbApp,
                                                            boolean needCalculateNodeNum) {
        if (dbApp == null) {
            return null;
        }
        ApplicationSwitchStatusDTO result = new ApplicationSwitchStatusDTO();
        List errorList = new ArrayList<>();
        String appUniqueKey = dbApp.getApplicationId() + PRADARNODE_SEPERATE_FLAG;
        Set<String> keys = redisTemplate.keys(appUniqueKey + "*");
        String resultStatus = null;
        if (needCalculateNodeNum) {
            if (keys == null || keys.size() < dbApp.getNodeNum()) {
                int uploadNodeNum = keys == null ? 0 : keys.size();
                String errorMsg = "????????????:????????????????????????????????????????????????,??????????????????" + dbApp.getNodeNum() + "; ?????????????????? " + uploadNodeNum;
                errorList.add(errorMsg);
            }
        }
        for (String nodeKey : keys) {
            NodeUploadDataDTO statusDTO = (NodeUploadDataDTO) redisTemplate.opsForValue().get(nodeKey);
            if (statusDTO == null) {
                continue;
            }
            Map<String, Object> exceptionMap = statusDTO.getSwitchErrorMap();
            if (exceptionMap != null && exceptionMap.size() > 0) {
                for (Map.Entry<String, Object> entry : exceptionMap.entrySet()) {
                    String key = entry.getKey();
                    String keySplit = key.contains(":") ? key.split(":")[0] : key;
                    String message = String.valueOf(entry.getValue());
                    if (message.contains("errorCode")) {
                        try {
                            ExceptionInfo exceptionInfo = JSONObject.parseObject(message, ExceptionInfo.class);
                            String errorMsg = keySplit + exceptionInfo.toString();
                            errorList.add(errorMsg);
                        } catch (Exception e) {
                            log.error("?????????????????????", e);
                        }
                    } else {
                        //?????????????????????????????????
                        String errorMsg = keySplit + ":" + message;
                        errorList.add(errorMsg);
                    }
                }
            }
        }
        result.setSwitchStatus(resultStatus);
        result.setApplicationName(dbApp.getApplicationName());
        result.setErrorList(errorList);
        return result;
    }

    @Override
    public Response<Integer> uploadMiddlewareStatus(Map<String, JarVersionVo> requestMap, String appName) {
        try {
            AppMiddlewareQuery query = new AppMiddlewareQuery();
            Long applicationId = applicationService.queryApplicationIdByAppName(appName);
            if (null == applicationId) {
                return Response.fail("??????????????????????????????");
            }

            query.setApplicationId(applicationId);
            List<TAppMiddlewareInfo> tAppMiddlewareInfos = tAppMiddlewareInfoMapper.selectList(query);
            if (null != tAppMiddlewareInfos && tAppMiddlewareInfos.size() > 0) {
                List<Long> ids = tAppMiddlewareInfos.stream().map(TAppMiddlewareInfo::getId).collect(
                        Collectors.toList());
                tAppMiddlewareInfoMapper.deleteBatch(ids);
            }

            List<TAppMiddlewareInfo> tAppMiddlewareInfoList = new ArrayList<>();
            for (Map.Entry<String, JarVersionVo> entry : requestMap.entrySet()) {
                JarVersionVo entryValue = entry.getValue();
                TAppMiddlewareInfo info = new TAppMiddlewareInfo();
                info.setActive(entryValue.isActive());
                info.setApplicationId(applicationId);
                info.setJarName(entryValue.getJarName());
                info.setPluginName(entryValue.getPluginName());
                info.setJarType(entryValue.getJarType());
                info.setUserId(WebPluginUtils.traceUserId());
                info.setHidden(entryValue.getHidden());
                tAppMiddlewareInfoList.add(info);
            }

            if (org.apache.commons.collections4.CollectionUtils.isEmpty(tAppMiddlewareInfoList)) {
                return Response.success();
            }

            int listPageSize = 200;
            if (tAppMiddlewareInfoList.size() > listPageSize) {
                List<List<TAppMiddlewareInfo>> tapeInfos = ListUtil.split(tAppMiddlewareInfoList, listPageSize);
                for (List<TAppMiddlewareInfo> tapInfo : tapeInfos) {
                    this.tAppMiddlewareInfoMapper.batchInsert(tapInfo);
                }
            } else {
                this.tAppMiddlewareInfoMapper.batchInsert(tAppMiddlewareInfoList);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.fail(e.getMessage());
        }

        return Response.success();
    }

    @Override
    public void resumeAllAgent(List<String> appIds) {
        try {
            appIds = this.filterAppIds(appIds,AgentConstants.RESUME);
            if (CollectionUtils.isEmpty(appIds)){
                log.info("?????????????????????????????????????????????");
                return;
            }
            // ??????????????????
            ApplicationQueryParam queryParam = new ApplicationQueryParam();
            queryParam.setPageSize(-1);
            queryParam.setCurrentPage(-1);
            queryParam.setApplicationIds(appIds.stream().map(Long::valueOf).collect(Collectors.toList()));
            PagingList<ApplicationDetailResult> pagingList = applicationDAO.queryApplicationList(queryParam);
            if (CollectionUtil.isEmpty(pagingList.getList())) {
                return;
            }
            List<ApplicationDetailResult> applicationList = pagingList.getList();
            List<String> appNames = applicationList.stream().map(ApplicationDetailResult::getApplicationName).collect(
                    Collectors.toList());
            applicationNodeProbeDAO.delByAppNamesAndOperate(ApplicationNodeProbeOperateEnum.UNINSTALL.getCode(),
                    appNames);
        } catch (Exception e) {
            log.error("????????????????????????", e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_RESUME_AGENT_ERROR, e);
        }
    }

    @Override
    public ApplicationDetailResult getByApplicationIdWithCheck(Long applicationId) {
        // ????????????
        ApplicationDetailResult application = applicationDAO.getApplicationById(applicationId);
        // ??????
        if (application == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_VALIDATE_ERROR, "???????????????!");
        }
        return application;
    }

    List<ApplicationVo> appEntryListToVoList(List<ApplicationDetailResult> tApplicationMntList) {
        List<ApplicationVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tApplicationMntList)) {
            //????????????????????????
            List<String> appNameList = tApplicationMntList.stream()
                    .map(ApplicationDetailResult::getApplicationName)
                    .collect(Collectors.toList());
            List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(appNameList);
            //???????????????????????????
            ApplicationNodeQueryParam queryParam = new ApplicationNodeQueryParam();
            queryParam.setCurrent(0);
            queryParam.setPageSize(99999);
            queryParam.setApplicationNames(appNameList);
            PagingList<ApplicationNodeResult> applicationNodes = applicationNodeDAO.pageNodes(queryParam);
            List<ApplicationNodeResult> applicationNodeResultList = applicationNodes.getList();
            Map<Long, List<ApplicationNodeResult>> applicationNodeResultMap = Maps.newHashMap();
            if (!CollectionUtils.isEmpty(applicationNodeResultList)) {
                for (ApplicationDetailResult tApplicationMnt : tApplicationMntList) {
                    List<ApplicationNodeResult> currentApplicationNodeResultList = applicationNodeResultList
                            .stream()
                            .filter(applicationNodeResult ->
                                    tApplicationMnt.getApplicationName().equals(applicationNodeResult.getAppName())
                            ).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(currentApplicationNodeResultList)) {
                        applicationNodeResultMap.put(tApplicationMnt.getApplicationId(),
                                currentApplicationNodeResultList);
                    }
                }
            }

            if (CollectionUtils.isEmpty(applicationResultList)) {
                for (ApplicationDetailResult param : tApplicationMntList) {
                    List<ApplicationNodeResult> applicationNodeResults = applicationNodeResultMap.get(
                            param.getApplicationId());
                    voList.add(appEntryToVo(param, null, applicationNodeResults));
                }
            } else {
                for (ApplicationDetailResult param : tApplicationMntList) {
                    String applicationName = param.getApplicationName();
                    Optional<ApplicationResult> optional = applicationResultList.stream().filter(
                            applicationResult -> applicationResult.getAppName().equals(applicationName)).findFirst();
                    List<ApplicationNodeResult> applicationNodeResults = applicationNodeResultMap.get(
                            param.getApplicationId());
                    if (optional.isPresent()) {
                        voList.add(appEntryToVo(param, optional.get(), applicationNodeResults));
                    } else {
                        voList.add(appEntryToVo(param, null, applicationNodeResults));
                    }
                }
            }
        }
        return voList;
    }

    ApplicationVo appEntryToVo(ApplicationDetailResult param, ApplicationResult applicationResult,
                               List<ApplicationNodeResult> applicationNodeResultList) {
        ApplicationVo vo = new ApplicationVo();
        vo.setPrimaryKeyId(param.getId());
        vo.setId(String.valueOf(param.getApplicationId()));
        vo.setApplicationName(param.getApplicationName());
        vo.setUpdateTime(param.getUpdateTime());
        vo.setApplicationDesc(param.getApplicationDesc());
        vo.setBasicScriptPath(param.getBasicScriptPath());
        vo.setCacheScriptPath(param.getCacheScriptPath());
        vo.setCleanScriptPath(param.getCleanScriptPath());
        vo.setDdlScriptPath(param.getDdlScriptPath());
        vo.setReadyScriptPath(param.getReadyScriptPath());
        vo.setNodeNum(param.getNodeNum());
        vo.setSwitchStutus(param.getSwitchStatus());
        vo.setUserId(param.getUserId());
        if (Objects.isNull(applicationResult)
                || !applicationResult.getInstanceInfo().getInstanceOnlineAmount().equals(param.getNodeNum())
                || CollectionUtils.isEmpty(applicationNodeResultList)
                || applicationNodeResultList.stream().map(ApplicationNodeResult::getAgentVersion).distinct().count() > 1) {
            vo.setAccessStatus(3);
            vo.setExceptionInfo("agent??????:" + param.getAccessStatus() + ",????????????: 3");
        } else {
            vo.setAccessStatus(param.getAccessStatus());
            String exceptionMsg = "agent??????:" + param.getAccessStatus();
            if (!applicationResult.getInstanceInfo().getInstanceOnlineAmount().equals(param.getNodeNum())
                    || CollectionUtils.isEmpty(applicationNodeResultList)
                    || applicationNodeResultList.stream().map(ApplicationNodeResult::getAgentVersion).distinct().count()
                    > 1) {
                exceptionMsg = exceptionMsg + ",????????????: 3";
            }
            vo.setExceptionInfo(exceptionMsg);
        }
        vo.setUserId(param.getUserId());
        WebPluginUtils.fillQueryResponse(vo);
        return vo;
    }

    private ApplicationCreateParam voToAppEntity(ApplicationVo param) {
        ApplicationCreateParam dbData = new ApplicationCreateParam();
        if (StringUtil.isNotEmpty(param.getId())) {
            dbData.setApplicationId(Long.parseLong(param.getId()));
        }
        dbData.setApplicationName(param.getApplicationName());
        dbData.setApplicationDesc(param.getApplicationDesc());
        dbData.setBasicScriptPath(param.getBasicScriptPath());
        dbData.setCacheScriptPath(param.getCacheScriptPath());
        dbData.setCleanScriptPath(param.getCleanScriptPath());
        dbData.setDdlScriptPath(param.getDdlScriptPath());
        dbData.setReadyScriptPath(param.getReadyScriptPath());
        dbData.setNodeNum(param.getNodeNum());
        dbData.setAccessStatus(param.getAccessStatus());
        dbData.setExceptionInfo(param.getExceptionInfo());
        dbData.setSwitchStatus(param.getSwitchStutus());
        if (param.getAccessStatus() == null) {
            dbData.setAccessStatus(1);
        } else {
            dbData.setAccessStatus(param.getAccessStatus());
        }
        return dbData;
    }

    @Override
    public ApplicationDetailResult queryTApplicationMntByName(String appName) {
        return applicationDAO.getByName(appName);
    }

    @Override
    public Long queryApplicationIdByAppName(String appName) {
        // ???????????????agent???????????????????????????
        String key = ConfCenterService.generateApplicationCacheKey(appName);
        String applicationId = (String) redisTemplate.opsForHash().get(ConfCenterService.APPLICATION_CACHE_PREFIX, key);
        if (StringUtils.isNotBlank(applicationId)) {
            return Long.valueOf(applicationId);
        }
        ApplicationDetailResult detailResult = applicationDAO.getByName(appName);
        Long result = null;
        if (detailResult != null) {
            result = detailResult.getApplicationId();
            redisTemplate.opsForHash().put(ConfCenterService.APPLICATION_CACHE_PREFIX, key, String.valueOf(result));
        }
        return result;
    }

    /**
     * ????????????
     *
     * @param ext
     * @param enable
     * @return
     */
    @Override
    public Response userAppSilenceSwitch(TenantCommonExt ext, Boolean enable) {
        //????????????????????? ???/???
        if (enable == null) {
            return Response.fail(FALSE_CORE, "????????????????????????", null);
        }

        UserExt user = WebPluginUtils.traceUser();
        if (WebPluginUtils.checkUserPlugin() && user == null) {
            return Response.fail(FALSE_CORE, "??????????????????", null);
        }

        String realStatus = getUserSilenceSwitchFromRedis(ext);
        ApplicationVo vo = new ApplicationVo();
        String status;
        String voStatus;
        if (realStatus.equals(AppSwitchEnum.CLOSING.getCode()) || realStatus.equals(AppSwitchEnum.OPENING.getCode())) {
            vo.setSwitchStutus(realStatus);
            return Response.success(realStatus);
        } else {
            status = (enable ? AppSwitchEnum.OPENED : AppSwitchEnum.CLOSED).getCode();
            voStatus = (enable ? AppSwitchEnum.OPENING : AppSwitchEnum.CLOSING).getCode();
            //??????????????????????????????????????????????????????redis
            redisTemplate.opsForValue().set(PRADAR_SILENCE_SWITCH_STATUS_VO + ext.getTenantId() + "_" + ext.getEnvCode(), status);
            redisTemplate.opsForValue().set(PRADAR_SILENCE_SWITCH_STATUS + ext.getTenantId() + "_" + ext.getEnvCode(), voStatus);
        }
        vo.setSwitchStutus(status);
        return Response.success(vo);
    }

    /**
     * ???redis?????????????????????????????????????????????
     *
     * @param ext
     * @return
     */
    private String getUserSilenceSwitchFromRedis(TenantCommonExt ext) {

        if (ext == null || ext.getTenantId() == null || StringUtil.isEmpty(ext.getEnvCode())) {
            throw new RuntimeException("??????id???????????? || ???????????????");
        }
        Object statusObj = redisTemplate.opsForValue().get(PRADAR_SILENCE_SWITCH_STATUS_VO + ext.getTenantId() + "_" + ext.getEnvCode());
        if (statusObj == null) {
            redisTemplate.opsForValue().set(PRADAR_SILENCE_SWITCH_STATUS_VO + ext.getTenantId() + "_" + ext.getEnvCode(), AppSwitchEnum.OPENED.getCode());
            redisTemplate.opsForValue().set(PRADAR_SILENCE_SWITCH_STATUS + ext.getTenantId() + "_" + ext.getEnvCode(), AppSwitchEnum.OPENED.getCode());
        } else {
            return (String) statusObj;
        }
        return AppSwitchEnum.OPENED.getCode();
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    @Override
    public Response userAppSilenceSwitchInfo() {
        ApplicationSwitchStatusDTO result = new ApplicationSwitchStatusDTO();
        UserExt user = WebPluginUtils.traceUser();
        if (WebPluginUtils.checkUserPlugin() && user == null) {
            return Response.fail(FALSE_CORE);
        }
        //?????????????????????????????????
        if (user != null && user.getRole() != null && user.getRole() == 1) {
            result.setSwitchStatus(AppSwitchEnum.OPENED.getCode());
        } else {
            result.setSwitchStatus(getUserSilenceSwitchStatusForVo(WebPluginUtils.traceTenantCommonExt()));
        }
        result.setUserId(WebPluginUtils.traceUserId());
        WebPluginUtils.fillQueryResponse(result);
        return Response.success(result);
    }

    @Override
    public String getUserSilenceSwitchStatusForVo(TenantCommonExt ext) {
        if (ext == null || ext.getTenantId() == null || StringUtil.isEmpty(ext.getEnvCode())) {
            return null;
        }
        Object o = redisTemplate.opsForValue().get(PRADAR_SILENCE_SWITCH_STATUS_VO + ext.getTenantId() + "_" + ext.getEnvCode());
        if (o == null) {
            redisTemplate.opsForValue().set(PRADAR_SILENCE_SWITCH_STATUS_VO + ext.getTenantId() + "_" + ext.getEnvCode(), AppSwitchEnum.OPENED.getCode());
            return AppSwitchEnum.OPENED.getCode();
        } else {
            return (String) o;
        }
    }

    @Override
    public Response getApplicationReportConfigInfo(Integer type, String appName) {
        List<AppAgentConfigReportDetailResult> results = reportDAO.listByBizType(type, appName);
        String silenceSwitchStatus = getUserSilenceSwitchStatusForVo(WebPluginUtils.traceTenantCommonExt());
        //?????????open?????? ?????????agent?????????false????????????????????????false???
        String configValue = AppSwitchEnum.OPENED.getCode().equals(silenceSwitchStatus) ? "true" : "false";
        List<AppAgentConfigReportDetailResult> filter = results.stream().filter(
                x -> configValue.equals(x.getConfigValue())).collect(Collectors.toList());


        return Response.success(filter);
    }

    @Override
    public Boolean silenceSwitchStatusIsTrue(TenantCommonExt ext, AppSwitchEnum appSwitchEnum) {
        String status = this.getUserSilenceSwitchStatusForVo(WebPluginUtils.traceTenantCommonExt());
        return appSwitchEnum.getCode().equals(status);
    }

    @Override
    public boolean existsApplication(Long tenantId, String envCode) {
        return applicationDAO.existsApplication(tenantId, envCode);
    }
}
