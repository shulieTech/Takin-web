package io.shulie.takin.web.biz.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.constant.AppConfigSheetEnum;
import com.pamirs.takin.common.constant.AppSwitchEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.common.util.PageInfo;
import com.pamirs.takin.entity.dao.confcenter.TApplicationMntDao;
import com.pamirs.takin.entity.dao.simplify.TAppMiddlewareInfoMapper;
import com.pamirs.takin.entity.domain.dto.ApplicationSwitchStatusDTO;
import com.pamirs.takin.entity.domain.dto.NodeUploadDataDTO;
import com.pamirs.takin.entity.domain.dto.config.ImportConfigDTO;
import com.pamirs.takin.entity.domain.dto.linkmanage.InterfaceVo;
import com.pamirs.takin.entity.domain.entity.ExceptionInfo;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import com.pamirs.takin.entity.domain.entity.simplify.TAppMiddlewareInfo;
import com.pamirs.takin.entity.domain.entity.simplify.TShadowJobConfig;
import com.pamirs.takin.entity.domain.query.ApplicationQueryParam;
import com.pamirs.takin.entity.domain.query.LinkGuardQueryParam;
import com.pamirs.takin.entity.domain.query.agent.AppMiddlewareQuery;
import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import com.pamirs.takin.entity.domain.vo.JarVersionVo;
import com.pamirs.takin.entity.domain.vo.application.NodeNumParam;
import com.pamirs.takin.entity.domain.vo.dsmanage.Configurations;
import com.pamirs.takin.entity.domain.vo.dsmanage.DataSource;
import com.pamirs.takin.entity.domain.vo.guardmanage.LinkGuardVo;
import io.shulie.takin.cloud.common.constants.Constants;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.input.application.*;
import io.shulie.takin.web.biz.pojo.input.whitelist.WhitelistImportFromExcelInput;
import io.shulie.takin.web.biz.pojo.openapi.response.application.ApplicationListResponse;
import io.shulie.takin.web.biz.pojo.output.application.ShadowConsumerOutput;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeOperateProbeRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationVisualInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityBottleneckResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationNodeDashBoardResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationVisualInfoResponse;
import io.shulie.takin.web.biz.pojo.response.application.ShadowServerConfigurationResponse;
import io.shulie.takin.web.biz.pojo.vo.application.ApplicationDsManageExportVO;
import io.shulie.takin.web.biz.service.*;
import io.shulie.takin.web.biz.service.application.ApplicationNodeService;
import io.shulie.takin.web.biz.service.dsManage.DsService;
import io.shulie.takin.web.biz.service.linkManage.LinkGuardService;
import io.shulie.takin.web.biz.service.linkManage.WhiteListService;
import io.shulie.takin.web.biz.service.simplify.ShadowJobConfigService;
import io.shulie.takin.web.biz.utils.DsManageUtil;
import io.shulie.takin.web.biz.utils.PageUtils;
import io.shulie.takin.web.biz.utils.WhiteListUtil;
import io.shulie.takin.web.biz.utils.xlsx.ExcelUtils;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApplicationConstants;
import io.shulie.takin.web.common.constant.GuardEnableConstants;
import io.shulie.takin.web.common.constant.ProbeConstants;
import io.shulie.takin.web.common.constant.WhiteListConstants;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.activity.info.FlowTypeEnum;
import io.shulie.takin.web.common.enums.application.AppAccessStatusEnum;
import io.shulie.takin.web.common.enums.excel.BooleanEnum;
import io.shulie.takin.web.common.enums.probe.ApplicationNodeProbeOperateEnum;
import io.shulie.takin.web.common.enums.shadow.ShadowMqConsumerType;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.util.JsonUtil;
import io.shulie.takin.web.common.util.MD5Tool;
import io.shulie.takin.web.common.util.whitelist.WhitelistUtil;
import io.shulie.takin.web.common.vo.excel.*;
import io.shulie.takin.web.data.dao.ApplicationNodeProbeDAO;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.dao.application.*;
import io.shulie.takin.web.data.dao.blacklist.BlackListDAO;
import io.shulie.takin.web.data.model.mysql.*;
import io.shulie.takin.web.data.param.application.AppRemoteCallQueryParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallUpdateParam;
import io.shulie.takin.web.data.param.application.ApplicationNodeQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationPluginsConfigParam;
import io.shulie.takin.web.data.param.blacklist.BlacklistCreateNewParam;
import io.shulie.takin.web.data.param.blacklist.BlacklistSearchParam;
import io.shulie.takin.web.data.param.blacklist.BlacklistUpdateParam;
import io.shulie.takin.web.data.result.application.*;
import io.shulie.takin.web.data.result.blacklist.BlacklistResult;
import io.shulie.takin.web.data.result.whitelist.WhitelistEffectiveAppResult;
import io.shulie.takin.web.data.result.whitelist.WhitelistResult;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.mockito.internal.util.collections.Sets;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private static final List<String> CONFIGITEMLIST = Collections.singletonList("redis影子key有效期");

    @Autowired
    private ConfCenterService confCenterService;
    @Resource
    private TApplicationMntDao tApplicationMntDao;
    @Resource
    private TAppMiddlewareInfoMapper tAppMiddlewareInfoMapper;
    @Autowired
    private ActivityService activityService;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;
    @Value("${pradar.switch.processing.wait.time:61}")
    private Integer pradarSwitchProcessingTime;
    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ApplicationNodeDAO applicationNodeDAO;
    @Autowired
    private AgentConfigCacheManager agentConfigCacheManager;
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

    @Value("${fast.debug.upload.log.path:/data/app/config/}")
    private String configPath;

    @Value("${application.ds.config.is.new.version: false}")
    private Boolean isNewVersion;

    @Autowired
    private ApplicationService applicationService;

    /**
     * 是否开启校验白名单重名
     */
    @Value("${whitelist.duplicate.name.check:false}")
    private String isCheckDuplicateName;

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


    @Override
    public List<TApplicationMnt> getApplicationsByUserIdList(List<Long> userIdList) {
        return tApplicationMntDao.getApplicationsByTenants(userIdList);
    }


    //3.添加定时任务
    //或直接指定时间间隔，例如：5秒
    @Override
    public void configureTasks() {
        //针对每个用户进行检查
        Map<String, Long> map = redisTemplate.opsForHash().entries(NEED_VERIFY_USER_MAP);
        if (map.size() > 0) {
            log.info("当前待执行用户数： => " + map.size());
            map.forEach((key, value) -> {
                if (System.currentTimeMillis() - value >= pradarSwitchProcessingTime * 1000) {
                    // 操作完删除，保证只执行一次
                    Long uid = Long.valueOf(key);
                    String switchStatus = getUserSwitchStatusForAgent(uid);
                    redisTemplate.opsForValue().set(PRADAR_SWITCH_STATUS_VO + uid, switchStatus);
                    // agent接收的关闭信息后不再上报信息
                    // reCalculateUserSwitch(Long.valueOf(key.toString()));
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
        // 组装应用
        return results.stream().map(result -> {
            ApplicationListResponse response = new ApplicationListResponse();
            BeanUtils.copyProperties(result, response);
            if (nodeIpMap.get(result.getApplicationName()) != null && nodeIpMap.get(result.getApplicationName()).size()
                > 0) {
                response.setIpsList(nodeIpMap.get(result.getApplicationName())
                    .stream().map(ApplicationNodeResult::getNodeIp).collect(Collectors.toList()));
                response.setNodeNum(nodeIpMap.get(result.getApplicationName()).size());
                // 判断下节点数
                if (!response.getNodeNum().equals(result.getNodeNum())
                    || nodeIpMap.get(result.getApplicationName()).stream().
                    map(ApplicationNodeResult::getAgentVersion).distinct().count() > 1) {
                    response.setAccessStatus(3);
                }
            } else {
                response.setIpsList(Lists.newArrayList());
                response.setNodeNum(0);
                // 无节点
                response.setAccessStatus(3);
            }
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public Response<List<ApplicationVo>> getApplicationList(ApplicationQueryParam queryParam) {
        Map<String, Object> param = new HashMap<>();
        param.put("applicationName", queryParam.getApplicationName());
        param.put("pageSize", queryParam.getPageSize());
        param.put("pageNum", queryParam.getCurrentPage());
        param.put("applicationIds", queryParam.getApplicationIds());
        PageInfo<TApplicationMnt> pageInfo = confCenterService.queryApplicationList(param);
        List<TApplicationMnt> list = pageInfo.getList();
        List<ApplicationVo> applicationVoList = appEntryListToVoList(list);
        //用户ids
        List<Long> userIds = applicationVoList.stream()
            .map(ApplicationVo::getUserId)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        //用户信息Map key:userId  value:user对象
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
    public List<ApplicationVo> getApplicationListVo(ApplicationQueryParam queryParam) {
        Map<String, Object> param = new HashMap<>();
        param.put("applicationName", queryParam.getApplicationName());
        param.put("pageSize", queryParam.getPageSize());
        param.put("pageNum", queryParam.getCurrentPage());
        param.put("applicationIds", queryParam.getApplicationIds());
        PageInfo<TApplicationMnt> pageInfo = confCenterService.queryApplicationList(param);
        List<TApplicationMnt> list = pageInfo.getList();
        List<ApplicationVo> applicationVoList = appEntryListToVoList(list);

        //用户ids
        List<Long> userIds = applicationVoList.stream().map(ApplicationVo::getUserId).filter(Objects::nonNull)
            .collect(
                Collectors.toList());
        //用户信息Map key:userId  value:user对象
        Map<Long, UserExt> userMap = WebPluginUtils.getUserMapByIds(userIds);
        if (!userMap.isEmpty()) {
            applicationVoList.stream().map(data -> {
                //负责人名称
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
     * 根据接入状态查询应用列表
     *
     * @return -
     */
    @Override
    public Response<List<ApplicationVo>> getApplicationList(ApplicationQueryParam param, Integer accessStatus) {
        if (accessStatus == null) {
            return getApplicationList(param);
        }
        //进行内存分页
        Integer pageSize = param.getPageSize();
        Integer currentPage = param.getCurrentPage();
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
    public Response<List<ApplicationVo>> getApplicationList() {
        List<ApplicationVo> applicationVoList = Lists.newArrayList();
        List<Long> userIdList = WebPluginUtils.getQueryAllowUserIdList();
        List<ApplicationDetailResult> applicationDetailResultList = applicationDAO.getApplicationListByUserIds(
            userIdList);
        if (CollectionUtils.isNotEmpty(applicationDetailResultList)) {
            applicationVoList = applicationDetailResultList.stream().map(applicationDetailResult -> {
                ApplicationVo applicationVo = new ApplicationVo();
                applicationVo.setId(String.valueOf(applicationDetailResult.getApplicationId()));
                applicationVo.setApplicationName(applicationDetailResult.getApplicationName());
                return applicationVo;
            }).collect(Collectors.toList());
        }
        return Response.success(applicationVoList);
    }

    @Override
    public Response<ApplicationVo> getApplicationInfo(String id) {
        if (id == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_VALIDATE_ERROR, "应用id不能为null");
        }
        TApplicationMnt tApplicationMnt = confCenterService.queryApplicationinfoById(Long.parseLong(id));
        if (tApplicationMnt == null) {
            return Response.success(new ApplicationVo());
        }
        //取应用节点数信息
        List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(
            Collections.singletonList(tApplicationMnt.getApplicationName()));
        ApplicationResult applicationResult = CollectionUtils.isEmpty(applicationResultList) ? null
            : applicationResultList.get(0);

        //取应用节点版本信息
        ApplicationNodeQueryParam queryParam = new ApplicationNodeQueryParam();
        queryParam.setCurrent(0);
        queryParam.setPageSize(99999);
        queryParam.setApplicationNames(Collections.singletonList(tApplicationMnt.getApplicationName()));
        PagingList<ApplicationNodeResult> applicationNodes = applicationNodeDAO.pageNodes(queryParam);
        List<ApplicationNodeResult> applicationNodeResultList = applicationNodes.getList();
        ApplicationVo vo = this.appEntryToVo(tApplicationMnt, applicationResult,
            applicationNodeResultList);
        return Response.success(vo);
    }

    @Override
    public Response<TApplicationMnt> getApplicationInfoForError(String id) {
        if (id == null) {
            return Response.fail(FALSE_CORE, "应用id不能为null");
        }
        TApplicationMnt tApplicationMnt = confCenterService.queryApplicationinfoByIdAndRole(Long.parseLong(id));
        return Response.success(tApplicationMnt);
    }

    @Override
    public Response<Integer> addApplication(ApplicationVo param) {
        if (param == null) {
            return Response.fail(FALSE_CORE, "应用不能为空");
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
            throw new TakinWebException(TakinWebExceptionEnum.AGENT_PUSH_APPLICATION_VALIDATE_ERROR, "应用不能为空");
        }

        if (StringUtils.isBlank(param.getApplicationName())) {
            throw new TakinWebException(TakinWebExceptionEnum.AGENT_PUSH_APPLICATION_VALIDATE_ERROR, "应用名不能为空");
        }

        if (StringUtil.isEmpty(param.getSwitchStutus())) {
            param.setSwitchStutus(AppSwitchEnum.OPENED.getCode());
        }
        if (param.getNodeNum() == null) {
            param.setNodeNum(1);
        }

        confCenterService.saveAgentRegisteApplication(voToAppEntity(param));
        OperationLogContextHolder.ignoreLog();

        return Response.success();
    }

    @Override
    public Response<Integer> modifyApplication(ApplicationVo param) {
        if (param == null || StringUtil.isEmpty(param.getId())) {
            return Response.fail(FALSE_CORE, "应用id不能为空");
        }
        try {
            Response<ApplicationVo> applicationVoResponse = getApplicationInfo(param.getId());
            if (null == applicationVoResponse.getData().getId()) {
                return Response.fail("该应用不存在");
            }
            ApplicationVo applicationVo = applicationVoResponse.getData();
            String applicationName = param.getApplicationName();
            if (!applicationVo.getNodeNum().equals(param.getNodeNum())) {
                applicationName = applicationName + "｜节点数量：" + param.getNodeNum();
            } else {
                if (applicationVo.getApplicationName().equals(param.getApplicationName())) {
                    OperationLogContextHolder.ignoreLog();
                }
            }
            // 同步更新应用下的配置的appName，目前影子库表，挡板配置，影子消费者
            if (StringUtil.isNotEmpty(param.getApplicationName())) {
                updateConfigAppName(param.getId(), param.getApplicationName());
            }
            OperationLogContextHolder.addVars(BizOpConstants.Vars.APPLICATION, applicationName);
            confCenterService.updateApplicationinfo(voToAppEntity(param));
        } catch (TakinModuleException e) {
            return Response.fail(FALSE_CORE, e.getErrorMessage(), e.getErrorMessage());
        }
        return Response.success();
    }

    private void updateConfigAppName(String id, String appName) {
        // 影子库表，挡板配置，影子消费者
        shadowMqConsumerDAO.updateAppName(Long.valueOf(id), appName);
        linkGuardDAO.updateAppName(Long.valueOf(id), appName);
        applicationDsManageDao.updateAppName(Long.valueOf(id), appName);
        // 远程调用的应用名
        appRemoteCallDAO.updateAppName(Long.valueOf(id), appName);
    }

    @Override
    public Response<Integer> deleteApplication(String appId) {
        if (appId == null) {
            return Response.fail(FALSE_CORE, "应用id不能为空");
        }
        confCenterService.deleteApplicationinfoByIds(appId);
        return Response.success();
    }

    /**
     * 上传应用检查状态
     * 1、上传状态存入redis，定时过期 ； 2、节点数量做对比
     *
     * @return -
     */
    @Override
    public Response<String> uploadAccessStatus(NodeUploadDataDTO param) {
        if (param == null || StringUtil.isEmpty(param.getApplicationName()) || StringUtil.isEmpty(param.getNodeKey())) {
            throw new TakinWebException(TakinWebExceptionEnum.AGENT_PUSH_APPLICATION_STATUS_VALIDATE_ERROR,
                "节点唯一key|应用名称 不能为空");
        }
        UserExt user = WebPluginUtils.getUser();
        if (WebPluginUtils.checkUserData() && user == null) {
            // todo 后续需要修改
            return Response.fail("0000-0000-0000", "未获取到" + WebPluginUtils.getTenantUserAppKey() + "用户信息");
        }

        TApplicationMnt applicationMnt = this.queryTApplicationMntByName(param.getApplicationName());

        if (applicationMnt == null) {
            return Response.fail("0000-0000-0000", "查询不到应用【" + param.getApplicationName() + "】,请先上报应用！");
        }

        if (param.getSwitchErrorMap() != null && !param.getSwitchErrorMap().isEmpty()) {
            //应用id+ agent id唯一键 作为节点信息
            String key = applicationMnt.getApplicationId() + PRADAR_SEPERATE_FLAG + param.getAgentId();
            List<String> nodeUploadDataDTOList = redisTemplate.opsForList().range(key, 0, -1);
            if (CollectionUtils.isEmpty(nodeUploadDataDTOList)) {
                //节点key信息
                String nodeSetKey = applicationMnt.getApplicationId() + PRADARNODE_KEYSET;
                redisTemplate.opsForSet().add(nodeSetKey, key);
                redisTemplate.expire(nodeSetKey, 1, TimeUnit.DAYS);
                //节点异常信息列表
                redisTemplate.opsForList().leftPush(key, JSONObject.toJSONString(param));
                redisTemplate.expire(key, 1, TimeUnit.DAYS);
            } else {
                redisTemplate.opsForList().leftPush(key, JSONObject.toJSONString(param));
            }
            //3：应用异常
            applicationDAO.updateApplicationStatus(applicationMnt.getApplicationId(),
                AppAccessStatusEnum.EXCEPTION.getCode());
        } else {
            // 应用正常
            applicationDAO.updateApplicationStatus(applicationMnt.getApplicationId(),
                AppAccessStatusEnum.NORMAL.getCode());
        }
        return Response.success("上传应用状态信息成功");
    }

    //Scheduled(cron = "0/10 * *  * * ?")
    @Override
    public void syncApplicationAccessStatus() {
        long startTime = System.currentTimeMillis();
        try {
            //查询出所有待检测状态的应用
            List<TApplicationMnt> applicationMntList = tApplicationMntDao.getAllApplicationByStatus(
                Arrays.asList(0, 1, 2, 3));
            if (!CollectionUtils.isEmpty(applicationMntList)) {
                List<String> appNames = applicationMntList.stream().map(TApplicationMnt::getApplicationName).collect(
                    Collectors.toList());
                List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(appNames);
                if (!CollectionUtils.isEmpty(applicationResultList)) {
                    Set<Long> errorApplicationIdSet = Sets.newSet();
                    Set<Long> normalApplicationIdSet = Sets.newSet();
                    applicationMntList.forEach(applicationMnt -> {
                        String appName = applicationMnt.getApplicationName();
                        Optional<ApplicationResult> optional =
                            applicationResultList.stream().filter(
                                applicationResult -> applicationResult.getAppName().equals(appName)).findFirst();
                        if (optional.isPresent()) {
                            ApplicationResult applicationResult = optional.get();
                            Boolean appIsException = applicationResult.getAppIsException();
                            if (appIsException) {
                                //异常
                                if (applicationMnt.getAccessStatus() != 3) {
                                    errorApplicationIdSet.add(applicationMnt.getApplicationId());
                                }
                            } else {
                                //正常
                                if (applicationMnt.getAccessStatus() != 0) {
                                    normalApplicationIdSet.add(applicationMnt.getApplicationId());
                                }
                            }
                        }
                    });
                    if (errorApplicationIdSet.size() > 0) {
                        modifyAccessStatusWithoutAuth(new ArrayList<>(errorApplicationIdSet), 3);
                    }
                    if (normalApplicationIdSet.size() > 0) {
                        modifyAccessStatusWithoutAuth(new ArrayList<>(normalApplicationIdSet), 0);
                    }
                }
            } else {
                log.debug("暂无待检测应用");
            }
        } catch (Exception e) {
            log.error("执行定时同步应用状态异常", e);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("执行定时同步应用状态完成，执行耗时：{}", (endTime - startTime));
        }
    }

    @Override
    public void modifyAccessStatus(String applicationId, Integer accessStatus, String exceptionInfo) {
        //只更新两个字段
        if (StringUtil.isNotEmpty(applicationId) && accessStatus != null) {
            ApplicationVo dbData = new ApplicationVo();
            dbData.setId(applicationId);
            dbData.setExceptionInfo(exceptionInfo);
            dbData.setAccessStatus(accessStatus);
            // 原先掉前端接口
            this.modifyApplication(dbData);
        }
    }

    @Override
    public void modifyAccessStatusWithoutAuth(List<Long> applicationIds, Integer accessStatus) {
        if (CollectionUtils.isNotEmpty(applicationIds)) {
            tApplicationMntDao.updateApplicationStatus(applicationIds, accessStatus);
        }
    }

    @Override
    public List<TApplicationMnt> getAllApplications() {
        return tApplicationMntDao.getAllApplications();
    }

    @Override
    public String getIdByName(String applicationName) {
        return tApplicationMntDao.getIdByName(applicationName);
    }

    @Override
    public Response<Integer> uploadMiddleware(Map<String, String> requestMap) {
        return Response.success();
    }

    @Override
    public Response<Integer> calculateUserSwitch(Long uid) {
        if (uid == null) {
            UserExt user = WebPluginUtils.getUser();
            if (user == null) {
                return Response.fail(FALSE_CORE, "当前用户为空");
            }
            uid = user.getId();
        }
        // reCalculateUserSwitch(uid);
        return Response.success();
    }

    @Override
    public ApplicationSwitchStatusDTO agentGetUserSwitchInfo() {
        ApplicationSwitchStatusDTO result = new ApplicationSwitchStatusDTO();
        result.setSwitchStatus(getUserSwitchStatusForAgent(WebPluginUtils.getCustomerId()));
        return result;
    }

    /**
     * 重新计算
     */
    private void reCalculateUserSwitch(Long customerId) throws IllegalArgumentException {
        if (customerId == null) {
            throw new IllegalArgumentException("uid can not by null !");
        }
        List<TApplicationMnt> tApplicationMnts = tApplicationMntDao.queryApplicationByTenant(customerId);
        List<ApplicationVo> errorVoList = new ArrayList<>();
        if (tApplicationMnts != null && tApplicationMnts.size() > 0) {
            for (TApplicationMnt app : tApplicationMnts) {
                ApplicationSwitchStatusDTO dto = judgeAppSwitchStatus(app, false);
                if (dto != null && dto.getErrorList().size() > 0) {
                    List<ApplicationVo> errorList = dto.getErrorList();
                    for (Object s : errorList) {
                        ApplicationVo vo = new ApplicationVo();
                        vo.setApplicationName(app.getApplicationName());
                        vo.setExceptionInfo(s.toString());
                        errorVoList.add(vo);
                    }
                }
            }
        }
        String oldStatus = getUserSwitchStatusForAgent(customerId);
        String voStatus = null;
        String userStatus = null;
        if (errorVoList.size() > 0) {
            if (StringUtil.isNotEmpty(oldStatus) && oldStatus.equals(AppSwitchEnum.OPENED.getCode())) {
                voStatus = AppSwitchEnum.OPEN_FAILING.getCode();
            } else if (StringUtil.isNotEmpty(oldStatus) && oldStatus.equals(AppSwitchEnum.CLOSED.getCode())) {
                voStatus = AppSwitchEnum.CLOSE_FAILING.getCode();
            }
            userStatus = AppSwitchEnum.CLOSED.getCode();
        } else {
            voStatus = oldStatus;
            userStatus = oldStatus;
        }
        redisTemplate.opsForValue().set(PRADAR_SWITCH_STATUS + customerId, userStatus);
        redisTemplate.opsForValue().set(PRADAR_SWITCH_STATUS_VO + customerId, voStatus);
        redisTemplate.opsForValue().set(PRADAR_SWITCH_ERROR_INFO_UID + customerId, errorVoList);

    }

    private String getUserSwitchStatusForAgent(Long customerId) {
        Object o = redisTemplate.opsForValue().get(PRADAR_SWITCH_STATUS + customerId);
        if (o == null) {
            redisTemplate.opsForValue().set(PRADAR_SWITCH_STATUS + customerId, AppSwitchEnum.OPENED.getCode());
            return AppSwitchEnum.OPENED.getCode();
        } else {
            return (String)o;
        }
    }

    @Override
    public String getUserSwitchStatusForVo() {
        Long uid = -1L;
        if (WebPluginUtils.getUser() != null) {
            uid = WebPluginUtils.getUser().getId();
        }
        Object o = redisTemplate.opsForValue().get(PRADAR_SWITCH_STATUS_VO + uid);
        if (o == null) {
            redisTemplate.opsForValue().set(PRADAR_SWITCH_STATUS_VO + uid, AppSwitchEnum.OPENED.getCode());
            return AppSwitchEnum.OPENED.getCode();
        } else {
            return (String)o;
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
        ApplicationDetailResult application = applicationDAO.getApplicationById(applicationId);
        Assert.notNull(application, "应用不存在!");

        List<ExcelSheetVO<?>> sheets = new ArrayList<>();

        // 影子库/表
        sheets.add(this.getShadowSheet(applicationId));

        // 出口挡板配置
        sheets.add(this.getLinkGuardSheet(applicationId));

        // job 配置
        sheets.add(this.getJobSheet(applicationId));

        // 白名单配置
        sheets.add(this.getWhiteListSheet(application));

        // 影子消费者配置
        sheets.add(this.getShadowConsumerSheet(applicationId));

        // 导出黑名单
        sheets.add(this.getBlacklistSheet(applicationId));

        // 导出插件管理的配置项
        sheets.add(this.getPluginsConfigSheet(applicationId));

        // 导出远程调用数据
        sheets.add(this.getRemoteCallConfigSheet(applicationId));

        try {
            ExcelUtils.exportExcelManySheet(response, application.getApplicationName(), sheets);
        } catch (Exception e) {
            log.error("应用配置导出错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取插件管理配置项的sheet
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
                excelVO.setConfigValue("与业务key一致");
            }
            return excelVO;
        }).collect(Collectors.toList());

        ExcelSheetVO<ApplicationPluginsConfigExcelVO> pluginsListSheet = new ExcelSheetVO<>();
        pluginsListSheet.setData(excelVoList);
        pluginsListSheet.setExcelModelClass(ApplicationPluginsConfigExcelVO.class);
        pluginsListSheet.setSheetName(AppConfigSheetEnum.PLUGINS_CONFIG.name());
        pluginsListSheet.setSheetNum(AppConfigSheetEnum.values().length - 1);
        return pluginsListSheet;
    }

    /**
     * 获取远程调用的sheet
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
                StringUtils.isBlank(t.getServerAppName()) ? Constants.NULL_SIGN : t.getServerAppName());
            excelVO.setMockReturnValue(
                StringUtils.isBlank(t.getMockReturnValue()) ? Constants.NULL_SIGN : t.getMockReturnValue());
            return excelVO;
        }).collect(Collectors.toList());

        ExcelSheetVO<ApplicationRemoteCallConfigExcelVO> remoteCallSheet = new ExcelSheetVO<>();
        remoteCallSheet.setData(excelVoList);
        remoteCallSheet.setExcelModelClass(ApplicationRemoteCallConfigExcelVO.class);
        remoteCallSheet.setSheetName(AppConfigSheetEnum.REMOTE_CALL.name());
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
        blacklistSheet.setSheetName(AppConfigSheetEnum.BLACK.name());
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
     * 获得 影子库/表 sheet 组织
     *
     * @param applicationId 应用id
     * @return sheet sheet 数据
     */
    private ExcelSheetVO<?> getShadowSheet(Long applicationId) {
        // 根据应用id, 查询 ds 列表
        List<ApplicationDsManageEntity> applicationDsManages = applicationDsManageDao.listByApplicationId(
            applicationId);

        // 导出对象创建
        ExcelSheetVO<ApplicationDsManageExportVO> sheet = new ExcelSheetVO<>();
        sheet.setExcelModelClass(ApplicationDsManageExportVO.class);
        sheet.setSheetName(AppConfigSheetEnum.DADABASE.name());
        sheet.setSheetNum(AppConfigSheetEnum.DADABASE.getSheetNumber());

        // ds 数据判断
        if (applicationDsManages.isEmpty()) {
            // ds 列表为空, 空数据
            sheet.setData(Collections.emptyList());
            return sheet;
        }

        // 不为空, 处理, 返回 sheet
        List<ApplicationDsManageExportVO> exportList = applicationDsManages.stream()
            .map(ds -> {
                // 旧版本, 影子库, 直接吐出 config
                ApplicationDsManageExportVO dsExportVO = new ApplicationDsManageExportVO();
                // 状态, 存储, 方案, 等...
                BeanUtils.copyProperties(ds, dsExportVO);

                // 如果是新版本, 转换一下 影子库/表 配置
                if (StringUtils.isBlank(ds.getParseConfig())) {
                    return dsExportVO;
                }

                // 不是新版本, 不是影子库, 则不需要转换
                if (!(DsManageUtil.isNewVersionSchemaDsType(ds.getDsType(), isNewVersion))) {
                    return dsExportVO;
                }

                Configurations configurations = JsonUtil.json2bean(ds.getParseConfig(), Configurations.class);
                if (configurations == null) {
                    return dsExportVO;
                }

                dsExportVO.setUserName(configurations.getDataSources().get(0).getUsername());

                // 兼容导入 0 0
                dsExportVO.setConfig("无");

                // 转换 影子库/表 配置
                DataSource dsDataSource = configurations.getDataSources().get(1);
                dsExportVO.setShadowDbUrl(dsDataSource.getUrl());
                dsExportVO.setShadowDbUserName(dsDataSource.getUsername());
                dsExportVO.setShadowDbPassword(dsDataSource.getPassword());
                dsExportVO.setShadowDbMinIdle(dsDataSource.getMinIdle());
                dsExportVO.setShadowDbMaxActive(dsDataSource.getMaxActive());
                return dsExportVO;
            })
            .collect(Collectors.toList());
        sheet.setData(exportList);
        return sheet;
    }

    /**
     * 影子消费者 聚合
     *
     * @param applicationId 应用id
     * @return sheet sheet 数据
     */
    private ExcelSheetVO<ShadowConsumerExcelVO> getShadowConsumerSheet(Long applicationId) {
        List<ShadowMqConsumerEntity> shadowMqConsumers = shadowMqConsumerDAO.listByApplicationId(applicationId);
        List<ShadowConsumerExcelVO> excelModelList = this.shadowConsumer2ExcelModel(shadowMqConsumers);
        ExcelSheetVO<ShadowConsumerExcelVO> shadowConsumerSheet = new ExcelSheetVO<>();
        shadowConsumerSheet.setData(excelModelList);
        shadowConsumerSheet.setExcelModelClass(ShadowConsumerExcelVO.class);
        shadowConsumerSheet.setSheetName(AppConfigSheetEnum.CONSUMER.name());
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
        // 查询应用
        ApplicationDetailResult application = applicationDAO.getApplicationById(applicationId);
        // 判断
        if (application == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_VALIDATE_ERROR, "应用不存在!");
        }
        return application.getApplicationName();
    }

    @Override
    public void uninstallAllAgent(List<String> appIds) {
        try {
            // 查询所有应用
            List<TApplicationMnt> applicationList = tApplicationMntDao.queryApplicationList(null, appIds);
            if (CollectionUtil.isEmpty(applicationList)) {
                return;
            }
            for (TApplicationMnt application : applicationList) {
                try {
                    ApplicationNodeOperateProbeRequest nodeRequest = new ApplicationNodeOperateProbeRequest();
                    nodeRequest.setApplicationId(application.getApplicationId());
                    nodeRequest.setAgentId(ProbeConstants.ALL_AGENT_ID);
                    nodeRequest.setAppName(application.getApplicationName());
                    nodeRequest.setOperateType(ApplicationNodeProbeOperateEnum.UNINSTALL.getCode());
                    applicationNodeService.operateProbe(nodeRequest);
                } catch (Exception e) {
                    log.error("一键卸载探针异常, {}", e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("一键卸载探针异常", e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_UNSTALL_AGENT_ERROR, e);

        }
    }

    @Override
    public void modifyAppNodeNum(List<NodeNumParam> numParamList) {
        applicationDAO.batchUpdateAppNodeNum(numParamList, WebPluginUtils.getCustomerId());
    }

    /**
     * 应用监控查询接口
     *
     * @param request 包含应用名称及服务名称
     */
    @Override
    public Response<List<ApplicationVisualInfoResponse>> getApplicationVisualInfo(ApplicationVisualInfoQueryRequest request) {
        //TODO 1.关注
        List<ApplicationAttentionListEntity> attentionList = doGetAttentionList(request.getAppName());
        //TODO 2.根据应用名称查询大数据性能数据
        List<String> attentionInterfaces = attentionList.stream().map(ApplicationAttentionListEntity::getInterfaceName).collect(Collectors.toList());
        request.setAttentionList(attentionInterfaces);
        Map<List<ApplicationVisualInfoResponse>, Integer> infoResponseMap = doGetAppDataByAppName(request);
        List<ApplicationVisualInfoResponse> infoDTOList = new ArrayList<>();
        Integer total = 0;
        if (null != infoResponseMap) {
            Map.Entry<List<ApplicationVisualInfoResponse>, Integer> infoEntry = null;
            for (Map.Entry<List<ApplicationVisualInfoResponse>, Integer> ApplicationVisualInfoEntry : infoResponseMap.entrySet()) {
                infoEntry = ApplicationVisualInfoEntry;
                break;
            }
            infoDTOList = infoEntry.getKey();
            total = infoEntry.getValue();
            //TODO 2.1补充关联业务活动ID
            //TODO 3.查询健康度(卡慢、接口异常)
            doGetHealthIndicator(infoDTOList, request);
        }
        return Response.success(infoDTOList, total);
    }

    /**
     * 关注服务
     *
     * @param request 应用名➕服务名➕是否关注
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
        applicationDAO.attendApplicationService(param);
    }

    @Override
    public void gotoActivityInfo(ActivityCreateRequest request) {
        activityService.createActivity(request);
    }


    /**
     * 关注列表
     *
     * @param applicationName
     */
    private List<ApplicationAttentionListEntity> doGetAttentionList(String applicationName) {
        return applicationDAO.getAttentionList(applicationName);
    }

    /**
     * 获取应用健康度数据
     *
     * @param infoDTOList
     * @param request
     */
    private void doGetHealthIndicator(List<ApplicationVisualInfoResponse> infoDTOList, ApplicationVisualInfoQueryRequest request) {
        if (CollectionUtils.isEmpty(infoDTOList)) {
            return;
        }
        infoDTOList.stream().forEach(dto -> {
            String appName = dto.getAppName();
            String serviceAndMethod = dto.getServiceAndMethod();
            int clusterTest = request.getClusterTest();
            FlowTypeEnum flowTypeEnum = FlowTypeEnum.getByType(clusterTest);
            LocalDateTime startTime = request.getStartTimeDate();
            LocalDateTime endTime = request.getEndTimeDate();
            Map<String, String> activeMap = dto.getActiveIdAndName();
            List<ActivityInfoQueryRequest> activityList = activeMap.keySet().stream().map(id -> new ActivityInfoQueryRequest(Long.parseLong(id), flowTypeEnum, startTime, endTime)).collect(Collectors.toList());
            ActivityBottleneckResponse response = activityService.getBottleneckByActivityList(dto);
            dto.setResponse(response);
        });
    }

    /**
     * 调用大数据获取性能数据
     */
    private Map<List<ApplicationVisualInfoResponse>, Integer> doGetAppDataByAppName(ApplicationVisualInfoQueryRequest request) {
        //TODO 大数据接口未定义
        String url = properties.getUrl().getAmdb() + "/amdb/db/api/metrics/metricsDetailes";
        try {
            AmdbResult<List<ApplicationVisualInfoResponse>> appDataResult = AmdbHelper.newInStance().httpMethod(HttpMethod.POST)
                    .url(url)
                    .param(request)
                    .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                    .eventName("根据应用名称查询大数据性能数据")
                    .list(ApplicationVisualInfoResponse.class);
            //TODO 大数据待清洗
            List<ApplicationVisualInfoResponse> data = appDataResult.getData();
            Long total = appDataResult.getTotal();
            List<String> attentionList = request.getAttentionList();
            String orderBy = request.getOrderBy();
            int current = request.getCurrent();
            int pageSize = request.getPageSize();
            return doSortAndPageAndConvertActivityId(data, attentionList, orderBy, pageSize, current,total);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }

    private Map<List<ApplicationVisualInfoResponse>, Integer> doSortAndPageAndConvertActivityId(List<ApplicationVisualInfoResponse> data, List<String> attentionList, String orderBy, int pageSize, int current, Long total) {
        if (CollectionUtils.isEmpty(data) || data.size() <= pageSize * (current)) {
            return null;
        }
//        String[] orderList = orderBy.split(" ");
//        if (orderList.length != 2) {
//            return null;
//        }
//        int total = data.size();
//        String orderName = orderList[0];
//        String orderType = orderList[1];
        List<ApplicationVisualInfoResponse> visualInfoDTOList = data.stream()
//                .sorted((d1, d2) -> {
//            boolean b1 = attentionList.contains(d1.getServiceAndMethod());
//            boolean b2 = attentionList.contains(d2.getServiceAndMethod());
//            if ((b1 && b2) || (!b1 && !b2)) {
//                Number result = 0;
//                switch (orderName) {
//                    case "QPS":
//                        result = d1.getRequestCount() - d2.getRequestCount();
//                        break;
//                    case "TPS":
//                        result = d1.getTps() - d2.getTps();
//                        break;
//                    case "RT":
//                        result = d1.getResponseConsuming() - d2.getResponseConsuming();
//                        break;
//                    case "SUCCESSRATE":
//                        result = d1.getSuccessRatio() - d2.getSuccessRatio();
//                }
//                int diff = result.doubleValue() > 0 ? 1 : -1;
//                return "asc".equals(orderType) ? diff : -diff;
//            } else {
//                return b1 ? -1 : 1;
//            }
//        })
                .map(dto -> {
            dto.setAttend(attentionList.contains(dto.getServiceAndMethod()));
            String[] activeList = dto.getActiveList();
            Map<String, String> activityResult = new HashMap<>();
            if (activeList != null) {
                for (String active : activeList) {
                    String[] split = active.split("#");
                    String appName = split[0];
                    String entrance = ActivityUtil.buildEntrance(appName, split[2], split[1], "%");
                    List<Map<String, String>> serviceList = activityDAO.findActivityIdByServiceName(appName, entrance);
                    if (!CollectionUtils.isEmpty(serviceList)) {
                        serviceList.stream().forEach(serviceName -> activityResult.put(String.valueOf(serviceName.get("linkId")), serviceName.get("linkName")));
                    }
                }
            }
            dto.setActiveIdAndName(activityResult);
            return dto;
        }).collect(Collectors.toList());

//        List<ApplicationVisualInfoResponse> infoDTOPageList = new ArrayList<>();
//        for (int i = current * pageSize; i < (current + 1) * pageSize && i < visualInfoDTOList.size(); i++) {
//            infoDTOPageList.add(visualInfoDTOList.get(i));
//        }

        Map result = new HashMap<>();
        result.put(visualInfoDTOList, total);
        return result;
    }

    /**
     * 校验数据格式
     *
     * @param stringArrayListHashMap excel 读取后的数据
     */
    private List<String> preCheck(Map<String, ArrayList<ArrayList<String>>> stringArrayListHashMap) {
        List<String> result = new ArrayList<>();
        for (AppConfigSheetEnum sheetEnum : AppConfigSheetEnum.values()) {
            ArrayList<ArrayList<String>> arrayLists = stringArrayListHashMap.get(sheetEnum.name());
            // 影子库不在这里判断
            if (CollectionUtils.isEmpty(arrayLists)
                || AppConfigSheetEnum.DADABASE.name().equals(sheetEnum.name())) {
                continue;
            }

            arrayLists.stream()
                .filter(Objects::nonNull)
                .forEach(strings -> {
                    Integer columnNum = sheetEnum.getColumnNum();
                    strings = (ArrayList<String>)strings.stream().filter(StringUtil::isNotEmpty).collect(
                        Collectors.toList());
                    if (strings.size() < columnNum) {
                        String msg = sheetEnum.name() + strings;
                        result.add(msg);
                    }
                });
            if (result.size() == 0) {
                if (AppConfigSheetEnum.PLUGINS_CONFIG.name().equals(sheetEnum.name())) {
                    arrayLists.forEach(t -> {
                        String inputStr = t.get(1);
                        boolean number = NumberUtil.isNumber(inputStr);
                        if (number) {
                            BigDecimal num = new BigDecimal(inputStr);
                            if (num.compareTo(BigDecimal.ZERO) < 0) {
                                result.add(sheetEnum.name() + "，配置值【" + num + "】小于0！");
                            }
                        }
                    });
                    //校验插件配置项
                    List<ArrayList<String>> notExsistsList = arrayLists.stream().filter(
                        t -> !CONFIGITEMLIST.contains(t.get(0))).collect(
                        Collectors.toList());
                    if (!CollectionUtils.isEmpty(notExsistsList)) {
                        String notExsists = notExsistsList.stream().map(t -> t.get(0)).collect(Collectors.joining(","));
                        result.add(sheetEnum.name() + "，配置项【" + notExsists + "】不存在！");
                    }
                    //维护的配置项列表CONFIGITEMLIST中存在 则判断配置项是否重复
                    //                HashSet<String> hashSet = new HashSet<>();
                    //                arrayLists.forEach(list->{
                    //                    String configItemImport = list.get(0);
                    //                    if(configItemImport.trim().equals("redis影子key有效期") && !hashSet.add
                    //                    (configItemImport)){
                    //                        result.add(sheetEnum.name() + "，配置项【"+configItemImport+"】有重复，请检查！");
                    //                    }
                    //                });
                }
            }
        }
        return result;
    }

    /**
     * 将配置文件保存到db
     */
    private void saveConfig2Db(Long applicationId, Map<String, ArrayList<ArrayList<String>>> configMap) {
        ApplicationDetailResult application = applicationDAO.getApplicationById(applicationId);
        if (application == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_VALIDATE_ERROR, "应用不存在!");
        }

        // 保存 影子库/表
        this.saveDsFromImport(applicationId, configMap.get(AppConfigSheetEnum.DADABASE.name()));

        // 保存挡板
        if (configMap.containsKey(AppConfigSheetEnum.GUARD.name())) {
            ArrayList<ArrayList<String>> guardList = configMap.get(AppConfigSheetEnum.GUARD.name());
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

        // 保存 job
        if (configMap.containsKey(AppConfigSheetEnum.JOB.name())) {
            ArrayList<ArrayList<String>> arrayLists = configMap.get(AppConfigSheetEnum.JOB.name());
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

        // 保存白名单
        this.saveWhiteListFromImport(applicationId, configMap);

        // 保存黑名单
        this.saveBlacklistFromImport(applicationId, configMap);

        // 保存影子消费者配置
        if (configMap.containsKey(AppConfigSheetEnum.CONSUMER.name())) {
            ArrayList<ArrayList<String>> arrayLists = configMap.get(AppConfigSheetEnum.CONSUMER.name());
            List<ShadowConsumerCreateInput> shadowConsumerCreateInputs = appConfigEntityConvertService
                .converComsumerList(arrayLists);
            if (CollectionUtils.isNotEmpty(shadowConsumerCreateInputs)) {
                shadowConsumerCreateInputs.forEach(request -> {
                    request.setApplicationId(applicationId);

                    try {
                        ShadowConsumerQueryInput queryRequest = new ShadowConsumerQueryInput();
                        queryRequest.setType(request.getType());
                        queryRequest.setTopicGroup(request.getTopicGroup());
                        queryRequest.setApplicationId(applicationId);
                        PagingList<ShadowConsumerOutput> pagingList = shadowConsumerService
                            .pageMqConsumers(queryRequest);
                        if (CollectionUtils.isNotEmpty(pagingList.getList())) {
                            ShadowConsumerOutput dbData = pagingList.getList().get(0);
                            ShadowConsumerUpdateInput updateRequest = new ShadowConsumerUpdateInput();
                            BeanUtils.copyProperties(request, updateRequest);
                            updateRequest.setId(dbData.getId());
                            shadowConsumerService.updateMqConsumers(updateRequest);
                        } else {
                            shadowConsumerService.createMqConsumers(request);
                        }
                    } catch (Exception e) {
                        log.error("影子消费者导入失败", e);
                        throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_CONFIG_FILE_IMPORT_ERROR,
                            "影子消费者导入失败!");
                    }
                });
            }
        }

        //保存插件管理配置项
        this.savePluginsListFromImport(application, configMap);
        // 导入远程调用
        this.saveRemoteCallFromImport(applicationId, configMap);
    }

    private void saveRemoteCallFromImport(Long applicationId, Map<String, ArrayList<ArrayList<String>>> configMap) {
        // map 取出数据
        ArrayList<ArrayList<String>> importRemoteCall;
        if ((importRemoteCall = configMap.get(AppConfigSheetEnum.REMOTE_CALL.name())) == null) {
            return;
        }
        // 转换
        List<AppRemoteCallUpdateParam> inputs = appConfigEntityConvertService.converRemoteCall(importRemoteCall,
            applicationId);
        if (CollectionUtils.isEmpty(inputs)) {
            return;
        }
        appRemoteCallDAO.batchSaveOrUpdate(inputs);
    }

    private void savePluginsListFromImport(ApplicationDetailResult application,
        Map<String, ArrayList<ArrayList<String>>> configMap) {
        List<ArrayList<String>> lists = configMap.get(AppConfigSheetEnum.PLUGINS_CONFIG.name());
        if (CollectionUtils.isEmpty(lists)) {
            return;
        }
        //获取应用的插件配置项
        ApplicationPluginsConfigParam param = new ApplicationPluginsConfigParam();
        param.setApplicationId(application.getApplicationId());
        List<ApplicationPluginsConfigEntity> list = pluginsConfigDAO.findList(param);
        if (CollectionUtils.isEmpty(list)) {
            list = Collections.emptyList();
        }
        //数据库已存在的配置项
        List<String> configItemResult = list.stream().map(ApplicationPluginsConfigEntity::getConfigItem).collect(
            Collectors.toList());
        //待更新的配置项
        List<ArrayList<String>> existList = lists.stream().filter(t -> configItemResult.contains(t.get(0))).collect(
            Collectors.toList());
        //待插入的配置项
        List<ArrayList<String>> notExistList = lists.stream().filter(t -> !configItemResult.contains(t.get(0))).collect(
            Collectors.toList());

        if (CollectionUtils.isNotEmpty(notExistList)) {
            List<ApplicationPluginsConfigParam> saveParamList = Lists.newArrayList();
            notExistList.forEach(t -> {
                if ("redis影子key有效期".equals(t.get(0))) {
                    ApplicationPluginsConfigParam configParam = new ApplicationPluginsConfigParam();
                    configParam.setConfigItem("redis影子key有效期");
                    configParam.setConfigKey("redis_expire");
                    configParam.setConfigDesc("可自定义设置redis影子key有效期，默认与业务key有效期一致。若设置时间比业务key有效期长，不生效，仍以业务key有效期为准。");
                    configParam.setConfigValue("与业务key一致".equals(t.get(1).trim()) ? "-1" : t.get(1));
                    configParam.setApplicationName(application.getApplicationName());
                    configParam.setApplicationId(application.getApplicationId());
                    saveParamList.add(configParam);
                }
            });
            //批量插入
            pluginsConfigService.addBatch(saveParamList);
        }
        if (CollectionUtils.isNotEmpty(existList)) {
            List<ApplicationPluginsConfigParam> updateParamList = Lists.newArrayList();
            List<ApplicationPluginsConfigEntity> finalList = list;
            existList.forEach(t -> {
                if ("redis影子key有效期".equals(t.get(0))) {
                    List<ApplicationPluginsConfigEntity> entityList = finalList.stream().filter(
                        entity -> "redis_expire".equals(entity.getConfigKey())).collect(Collectors.toList());
                    ApplicationPluginsConfigParam configParam = new ApplicationPluginsConfigParam();
                    configParam.setConfigItem("redis影子key有效期");
                    configParam.setConfigKey("redis_expire");
                    configParam.setConfigValue("与业务key一致".equals(t.get(1).trim()) ? "-1" : t.get(1));
                    configParam.setId(entityList.get(0).getId());
                    configParam.setApplicationName(application.getApplicationName());
                    configParam.setApplicationId(application.getApplicationId());
                    updateParamList.add(configParam);
                }
            });

            //批量更新
            pluginsConfigService.updateBatch(updateParamList);
        }
    }

    private void saveBlacklistFromImport(Long applicationId, Map<String, ArrayList<ArrayList<String>>> configMap) {
        // map 取出数据
        ArrayList<ArrayList<String>> importBlackLists;
        if ((importBlackLists = configMap.get(AppConfigSheetEnum.BLACK.name())) == null) {
            return;
        }
        // 转换
        List<BlacklistCreateNewParam> whiteListCreateLists = appConfigEntityConvertService.converBlackList(
            importBlackLists, applicationId);
        if (CollectionUtils.isEmpty(whiteListCreateLists)) {
            return;
        }
        // 去重
        BlacklistSearchParam param = new BlacklistSearchParam();
        param.setApplicationId(applicationId);
        // todo 糟糕的代码，没心思写，之后优化下，啦啦啦
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
     * 导入下创建或更新白名单
     *
     * @param applicationId 应用id
     */
    private void saveWhiteListFromImport(Long applicationId, Map<String, ArrayList<ArrayList<String>>> configMap) {
        // map 取出数据
        ArrayList<ArrayList<String>> importWhiteLists;
        if ((importWhiteLists = configMap.get(AppConfigSheetEnum.WHITE.name())) == null) {
            return;
        }
        // 转换
        List<WhitelistImportFromExcelInput> inputs = appConfigEntityConvertService.converWhiteList(importWhiteLists);
        if (CollectionUtils.isEmpty(inputs)) {
            return;
        }
        inputs.forEach(vo -> vo.setApplicationId(applicationId));
        whiteListService.importWhiteListFromExcel(inputs);
    }

    /**
     * 导入 ds, 持久化
     *
     * @param applicationId 应用id
     * @param dsObjects     影子库/表 对象列表(这里用 list 表示)
     */
    private void saveDsFromImport(Long applicationId, ArrayList<ArrayList<String>> dsObjects) {
        if (CollectionUtils.isEmpty(dsObjects)) {
            return;
        }

        for (ArrayList<String> ds : dsObjects) {
            // ds 的结构 @see ApplicationDsManageExportVO

            // 创建所需参数
            ApplicationDsCreateInput dsCreateRequest = this.getApplicationDsCreateInput(applicationId, ds);

            // 根据 url 判断 配置是否存在
            List<ApplicationDsManageEntity> applicationDsManages = applicationDsManageDao.listByApplicationIdAndDsType(
                applicationId, dsCreateRequest.getDsType());

            // 影子表, 影子库, 影子Server 判断唯一性
            if (applicationDsManages.isEmpty()) {
                this.createDsFromImport(dsCreateRequest);
                continue;
            }

            // 找到重复的
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
     * 判断导入的与存在的 ds 是否相同
     *
     * @param dsCreateRequest     导入的参数
     * @param applicationDsManage 存在的实例
     * @return 是否相同
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

        // 影子server, 需要根据 master, nodes 来做唯一判断
        if (DsManageUtil.isServerDsType(dsCreateRequest.getDsType())) {
            // 判断唯一
            same = this.compareImportServerAndOriginServer(dsCreateRequest.getConfig(),
                applicationDsManage.getConfig());
        }

        // 影子表判断 url
        if (DsManageUtil.isTableDsType(dsCreateRequest.getDsType())) {
            same = Objects.equals(applicationDsManage.getUrl(), dsCreateRequest.getUrl());
        }

        // 影子库判断 url
        if (DsManageUtil.isSchemaDsType(dsCreateRequest.getDsType())) {
            if (isNewVersion) {
                // 新版, 直接对比 url
                same = Objects.equals(applicationDsManage.getUrl(), dsCreateRequest.getUrl());
            } else {
                // 旧版, 解析 config
                same = Objects.equals(applicationDsManage.getUrl(),
                    dsService.parseShadowDbUrl(dsCreateRequest.getConfig()));
            }
        }
        return same;
    }

    /**
     * 比较Hbase类型配置
     *
     * @param dsCreateRequest     创建对象
     * @param applicationDsManage 数据库对象
     * @return -
     */
    private boolean compareImportHbaseConfig(ApplicationDsCreateInput dsCreateRequest,
        ApplicationDsManageEntity applicationDsManage) {
        return compareImportEsConfig(dsCreateRequest, applicationDsManage);
    }

    /**
     * 比较ES类型配置
     *
     * @param dsCreateRequest     创建对象
     * @param applicationDsManage 数据库对象
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
     * 导入中, 更新ds
     *
     * @param applicationDsManage 已存在的 ds
     * @param dsCreateRequest     更新所需参数
     */
    private void updateDsFromImport(ApplicationDsManageEntity applicationDsManage,
        ApplicationDsCreateInput dsCreateRequest) {
        ApplicationDsUpdateInput updateRequest = new ApplicationDsUpdateInput();
        BeanUtils.copyProperties(dsCreateRequest, updateRequest);
        updateRequest.setId(applicationDsManage.getId());
        updateRequest.setApplicationName(applicationDsManage.getApplicationName());
        Response response = dsService.dsUpdate(updateRequest);
        // 错误判断
        if (response.getError() != null && FALSE_CORE.equals(response.getError().getCode())) {
            throw new RuntimeException(response.getError().getMsg());
        }
    }

    /**
     * 导入中, 创建ds
     *
     * @param dsCreateRequest 创建所需参数
     */
    private void createDsFromImport(ApplicationDsCreateInput dsCreateRequest) {
        // 不存在, 创建
        Response response = dsService.dsAdd(dsCreateRequest);
        // 错误判断
        if (response.getError() != null && FALSE_CORE.equals(response.getError().getCode())) {
            throw new RuntimeException(response.getError().getMsg());
        }
    }

    /**
     * 比较两个 影子server 是否相同
     * 比较 master 和 nodes
     *
     * @param importServerConfig 导入的server配置
     * @param originServerConfig 原有的server配置
     * @return 是否相同
     */
    private boolean compareImportServerAndOriginServer(String importServerConfig, String originServerConfig) {
        // 解析传入的
        ShadowServerConfigurationResponse importServerConfigResponse = JsonUtil.json2bean(importServerConfig,
            ShadowServerConfigurationResponse.class);

        // 解析原来的
        ShadowServerConfigurationResponse originServerConfigResponse = JsonUtil.json2bean(originServerConfig,
            ShadowServerConfigurationResponse.class);

        // 对比
        return String.format("%s%s", importServerConfigResponse.getDataSourceBusiness().getMaster(),
                importServerConfigResponse.getDataSourceBusiness().getNodes())
            .equals(String.format("%s%s", originServerConfigResponse.getDataSourceBusiness().getMaster(),
                importServerConfigResponse.getDataSourceBusiness().getNodes()));
    }

    /**
     * 通过文件, 获取创建参数
     *
     * @param applicationId 应用id
     * @param ds            影子库/表 配置
     * @return 创建所需参数
     */
    private ApplicationDsCreateInput getApplicationDsCreateInput(Long applicationId, ArrayList<String> ds) {
        // 太难了, 前人的设计 0 0

        // 首先, 必须要大于2个字段, 为了获取 dsType...
        Assert.isTrue(ds.size() >= 2, "缺少必填字段!");

        ApplicationDsCreateInput createRequest = new ApplicationDsCreateInput();
        int index = 0;
        String dbTypeString = ds.get(index++);
        Assert.isTrue(StringUtils.isNotBlank(dbTypeString), "存储类型 必须填写!");
        createRequest.setDbType(Integer.valueOf(dbTypeString));

        // 方案类型枚举
        String dsTypeString = ds.get(index++);
        Assert.isTrue(StringUtils.isNotBlank(dsTypeString), "方案类型 必须填写!");
        Integer dsType = Integer.valueOf(dsTypeString);
        createRequest.setDsType(dsType);

        // 根据方案类型, 判断
        if (DsManageUtil.isNewVersionSchemaDsType(dsType, isNewVersion)) {
            // 新版agent, 影子库, 必须有7个字段以上, xml 不用填写, 最小, 最大 非必填
            Assert.isTrue(ds.size() >= 9, "缺少必填字段!");
        } else {
            // 其他是5个
            Assert.isTrue(ds.size() >= 5, "缺少必填字段!");
        }

        // url 判断
        String url = ds.get(index++);
        Assert.isTrue(StringUtils.isNotBlank(url), "url 必须填写!");
        createRequest.setUrl(url);

        // 状态描述
        // 状态转换
        String statusString = ds.get(index++);
        Assert.isTrue(StringUtils.isNotBlank(statusString), "状态 必须填写!");
        createRequest.setStatus(Integer.valueOf(statusString));

        // 配置 判断
        String config = ds.get(index++);
        Assert.isTrue(StringUtils.isNotBlank(config), "xml 必须填写!");
        createRequest.setConfig(config);

        // 新版agent 影子库, 不需要判断, 其他都判断
        createRequest.setOldVersion(!isNewVersion);
        createRequest.setApplicationId(applicationId);

        // 新版本, 添加下面属性
        if (DsManageUtil.isNewVersionSchemaDsType(dsType, isNewVersion)) {
            createRequest.setConfig("");

            String userName = ds.get(index++);
            String shadowDbUrl = ds.get(index++);
            String shadowDbUserName = ds.get(index++);
            String shadowDbPassword = ds.get(index++);

            // 影子库判断
            Assert.isTrue(StringUtils.isNotBlank(userName), "用户名 必须填写!");
            Assert.isTrue(StringUtils.isNotBlank(shadowDbUrl), "影子库url 必须填写!");
            Assert.isTrue(StringUtils.isNotBlank(shadowDbUserName), "影子库用户名 必须填写!");
            Assert.isTrue(StringUtils.isNotBlank(shadowDbPassword), "影子库密码 必须填写!");

            createRequest.setUserName(userName);
            createRequest.setShadowDbUrl(shadowDbUrl);
            createRequest.setShadowDbUserName(shadowDbUserName);
            createRequest.setShadowDbPassword(shadowDbPassword);

            // 影子库导出, 新版agent, 10个字段, 最后两个非必填
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
     * 不同类型, 获得 url
     *
     * @param dsType 方案类型
     * @param url    传入的 url
     * @param config 配置
     * @return url
     */
    private String getUrlFromImport(Integer dsType, String url, String config) {
        // , url 需要解析获取
        if (DsManageUtil.isServerDsType(dsType)) {
            ShadowServerConfigurationResponse serverConfig = JsonUtil.json2bean(config,
                ShadowServerConfigurationResponse.class);
            return serverConfig.getDataSourceBusiness().getNodes();
        }

        // 旧版agent, 影子表
        if (DsManageUtil.isSchemaDsType(dsType) && !isNewVersion) {
            return dsService.parseShadowDbUrl(config);
        }

        // 其他直接 url 获取
        return url;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Response<ImportConfigDTO> importApplicationConfig(MultipartFile file, Long applicationId) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_CONFIG_FILE_VALIDATE_ERROR, "文件不存在!");
        }

        if (!originalFilename.endsWith(".xlsx")) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_CONFIG_FILE_VALIDATE_ERROR,
                "文件格式不正确，必须为xlsx格式文件，请重新导出配置文件！");
        }

        // 读取xlsx文件
        Map<String, ArrayList<ArrayList<String>>> stringArrayListHashMap = ExcelUtils.readExcelForXlsx(file, 0);

        if (CollectionUtils.isEmpty(stringArrayListHashMap)) {
            return null;
        }

        // 数据校验
        List<String> list = this.preCheck(stringArrayListHashMap);
        if (CollectionUtils.isNotEmpty(list)) {
            ImportConfigDTO dto = new ImportConfigDTO();
            dto.setMsg(list);
            return Response.success(dto);
        }

        // 将文件内容写入到db
        this.saveConfig2Db(applicationId, stringArrayListHashMap);
        return null;
    }

    /**
     * 组装挡板sheet
     *
     * @param applicationId 应用id
     * @return sheet sheet 数据
     */
    private ExcelSheetVO<LinkGuardExcelVO> getLinkGuardSheet(Long applicationId) {
        List<LinkGuardEntity> linkGuards = linkGuardDAO.listFromExportByApplicationId(applicationId);
        List<LinkGuardExcelVO> linkGuardExcelModelList = this.linkGuard2ExcelModel(linkGuards);
        ExcelSheetVO<LinkGuardExcelVO> guardExcelSheet = new ExcelSheetVO<>();
        guardExcelSheet.setData(linkGuardExcelModelList);
        guardExcelSheet.setExcelModelClass(LinkGuardExcelVO.class);
        guardExcelSheet.setSheetName(AppConfigSheetEnum.GUARD.name());
        guardExcelSheet.setSheetNum(0);
        return guardExcelSheet;
    }

    /**
     * 组装job sheet
     *
     * @param applicationId 应用id
     * @return sheet sheet 数据
     */
    private ExcelSheetVO<ShadowJobExcelVO> getJobSheet(Long applicationId) {
        List<ShadowJobConfigEntity> shadowJobConfigs = shadowJobConfigDAO.listByApplicationId(applicationId);
        List<ShadowJobExcelVO> jobExcelModelList = this.job2ExcelJobModel(shadowJobConfigs);
        ExcelSheetVO<ShadowJobExcelVO> jobSheet = new ExcelSheetVO<>();
        jobSheet.setData(jobExcelModelList);
        jobSheet.setExcelModelClass(ShadowJobExcelVO.class);
        jobSheet.setSheetName(AppConfigSheetEnum.JOB.name());
        jobSheet.setSheetNum(1);
        return jobSheet;
    }

    /**
     * 组装白名单sheet
     *
     * @param application 应用名称
     * @return sheet sheet 数据
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
     * 影子消费者实例列表转导出实例列表
     *
     * @param shadowMqConsumers 影子消费者实例列表
     * @return 导出实例列表
     */
    private List<ShadowConsumerExcelVO> shadowConsumer2ExcelModel(List<ShadowMqConsumerEntity> shadowMqConsumers) {
        if (CollectionUtils.isEmpty(shadowMqConsumers)) {
            return Collections.emptyList();
        }
        return shadowMqConsumers.stream().map(response -> {
            ShadowConsumerExcelVO model = new ShadowConsumerExcelVO();
            model.setTopicGroup(response.getTopicGroup());
            model.setType(ShadowMqConsumerType.of(response.getType()).name());
            model.setStatus(response.getStatus());
            return model;
        }).collect(Collectors.toList());
    }

    /**
     * 白名单实例列表转导出实例列表
     *
     * @param whiteLists         白名单实例列表
     * @param whiteListsFromAmDb 大数据获取的的白名单
     * @return 导出实例列表
     */
    private List<WhiteListExcelVO> whiteList2ExcelModel(List<WhitelistResult> whiteLists,
        List<InterfaceVo> whiteListsFromAmDb) {

        List<WhiteListExcelVO> models = new ArrayList<>();
        // 重名白名单
        List<String> armdString = whiteLists.stream().map(WhitelistResult::getInterfaceName).collect(
            Collectors.toList());
        List<String> existWhite = whiteListService.getExistWhite(armdString, Lists.newArrayList());
        if (CollectionUtils.isNotEmpty(whiteLists)) {
            // 生效应用
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
                // 生效应用
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

        // 使用 map 去重
        Map<String, WhiteListExcelVO> keyAboutWhiteList = models.stream().collect(Collectors
            .toMap(model -> WhiteListUtil.getInterfaceAndType(model.getInterfaceName(), model.getType()),
                Function.identity(), (v1, v2) -> v1));
        return new ArrayList<>(keyAboutWhiteList.values());
    }

    private String getIsGlobal(List<String> existWhite, String type, String interfaceName, Boolean isGlobal) {
        if (Boolean.parseBoolean(isCheckDuplicateName)) {
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
     * 挡板实体对象转导出对象
     *
     * @param linkGuardEntities 实体对象
     * @return 导出示实例列表
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
     * 任务转成导出实例
     *
     * @param shadowJobConfigs 影子任务
     * @return 导出实例列表
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
     * 判断应用接入状态
     *
     * @return -
     */
    private ApplicationSwitchStatusDTO judgeAppSwitchStatus(TApplicationMnt dbApp, boolean needCalculateNodeNum) {
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
                String errorMsg = "节点异常:设置的节点数和开关上报节点数不同,设置节点数：" + dbApp.getNodeNum() + "; 上报节点数： " + uploadNodeNum;
                errorList.add(errorMsg);
            }
        }
        for (String nodeKey : keys) {
            NodeUploadDataDTO statusDTO = (NodeUploadDataDTO)redisTemplate.opsForValue().get(nodeKey);
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
                            log.error("异常转换失败：", e);
                        }
                    } else {
                        //兼容历史版本的异常展示
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
            UserExt userExt = WebPluginUtils.queryUserByKey();
            AppMiddlewareQuery query = new AppMiddlewareQuery();
            TApplicationMnt tApplicationMnt = applicationService.queryTApplicationMntByName(appName);
            if (null == tApplicationMnt) {
                return Response.fail("未查询到应用相关数据");
            }

            query.setApplicationId(tApplicationMnt.getApplicationId());
            List<TAppMiddlewareInfo> tAppMiddlewareInfos = tAppMiddlewareInfoMapper.selectList(query);
            if (null != tAppMiddlewareInfos && tAppMiddlewareInfos.size() > 0) {
                List<Long> ids = tAppMiddlewareInfos.stream().map(TAppMiddlewareInfo::getId).collect(
                    Collectors.toList());
                tAppMiddlewareInfoMapper.deleteBatch(ids);
            }

            for (Map.Entry<String, JarVersionVo> entry : requestMap.entrySet()) {
                JarVersionVo entryValue = entry.getValue();
                TAppMiddlewareInfo info = new TAppMiddlewareInfo();
                info.setActive(entryValue.isActive());
                info.setApplicationId(tApplicationMnt.getApplicationId());
                info.setJarName(entryValue.getJarName());
                info.setPluginName(entryValue.getPluginName());
                info.setJarType(entryValue.getJarType());
                info.setUserId(userExt.getId());
                info.setHidden(entryValue.getHidden());
                tAppMiddlewareInfoMapper.insert(info);
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
            // 查询所有应用
            List<TApplicationMnt> applicationList = tApplicationMntDao.queryApplicationList(null, appIds);
            if (CollectionUtil.isEmpty(applicationList)) {
                return;
            }

            List<String> appNames = applicationList.stream().map(TApplicationMnt :: getApplicationName).collect(Collectors.toList());
            applicationNodeProbeDAO.delByAppNamesAndOperate(WebPluginUtils.getCustomerId(), ApplicationNodeProbeOperateEnum.UNINSTALL.getCode(), appNames);
        } catch (Exception e) {
            log.error("一键恢复探针异常", e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_RESUME_AGENT_ERROR, e);
        }
    }

    @Override
    public ApplicationDetailResult getByApplicationIdWithCheck(Long applicationId) {
        // 查询应用
        ApplicationDetailResult application = applicationDAO.getApplicationById(applicationId);
        // 判断
        if (application == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_VALIDATE_ERROR, "应用不存在!");
        }
        return application;
    }

    List<ApplicationVo> appEntryListToVoList(List<TApplicationMnt> tApplicationMnts) {
        List<ApplicationVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tApplicationMnts)) {
            //取应用节点数信息
            List<String> appNameList = tApplicationMnts.stream()
                .map(TApplicationMnt::getApplicationName)
                .collect(Collectors.toList());
            List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(appNameList);
            //取应用节点版本信息
            ApplicationNodeQueryParam queryParam = new ApplicationNodeQueryParam();
            queryParam.setCurrent(0);
            queryParam.setPageSize(99999);
            queryParam.setApplicationNames(appNameList);
            PagingList<ApplicationNodeResult> applicationNodes = applicationNodeDAO.pageNodes(queryParam);
            List<ApplicationNodeResult> applicationNodeResultList = applicationNodes.getList();
            Map<Long, List<ApplicationNodeResult>> applicationNodeResultMap = Maps.newHashMap();
            if (!CollectionUtils.isEmpty(applicationNodeResultList)) {
                for (TApplicationMnt tApplicationMnt : tApplicationMnts) {
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
                for (TApplicationMnt param : tApplicationMnts) {
                    List<ApplicationNodeResult> applicationNodeResults = applicationNodeResultMap.get(
                        param.getApplicationId());
                    voList.add(appEntryToVo(param, null, applicationNodeResults));
                }
            } else {
                for (TApplicationMnt param : tApplicationMnts) {
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

    ApplicationVo appEntryToVo(TApplicationMnt param, ApplicationResult applicationResult,
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
            vo.setExceptionInfo("agent状态:" + param.getAccessStatus() + ",节点状态: 3");
        } else {
            vo.setAccessStatus(param.getAccessStatus());
            String exceptionMsg = "agent状态:" + param.getAccessStatus();
            if (!applicationResult.getInstanceInfo().getInstanceOnlineAmount().equals(param.getNodeNum())
                || CollectionUtils.isEmpty(applicationNodeResultList)
                || applicationNodeResultList.stream().map(ApplicationNodeResult::getAgentVersion).distinct().count()
                > 1) {
                exceptionMsg = exceptionMsg + ",节点状态: 3";
            }
            vo.setExceptionInfo(exceptionMsg);
        }
        vo.setUserId(param.getUserId());
        WebPluginUtils.fillQueryResponse(vo);
        return vo;
    }

    TApplicationMnt voToAppEntity(ApplicationVo param) {
        TApplicationMnt dbData = new TApplicationMnt();
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
    public TApplicationMnt queryTApplicationMntByName(String appName) {
        return tApplicationMntDao.queryApplicationInfoByNameAndTenant(appName,
            WebPluginUtils.checkUserData() ? WebPluginUtils.getCustomerId() : null);
    }

    /**
     * 应用异常状态检查
     *
     * @param vo 应用信息
     */
    private void checkAccessStatus(ApplicationVo vo) {
        ApplicationNodeDashBoardResponse applicationNodeDashBoardResponse =
            applicationNodeService.getApplicationNodeDashBoardResponse(vo.getApplicationName(), vo.getNodeNum());
        if (StringUtils.isNotBlank(applicationNodeDashBoardResponse.getErrorMsg())) {
            vo.setAccessStatus(ApplicationConstants.APPLICATION_ACCESS_STATUS_EXCEPTION);
        }
    }

    /**
     * 静默开关
     *
     * @param uid
     * @param enable
     * @return
     */
    @Override
    public Response userAppSilenceSwitch(Long uid, Boolean enable) {
        //全局开关只保留 开/关
        if (enable == null) {
            return Response.fail(FALSE_CORE, "开关状态不能为空", null);
        }
        if (uid == null) {
            UserExt user = WebPluginUtils.getUser();
            if (user == null) {
                return Response.fail(FALSE_CORE, "当前用户为空", null);
            }
            uid = user.getCustomerId();
        }

        String realStatus = getUserSilenceSwitchFromRedis(uid);
        ApplicationVo vo = new ApplicationVo();
        String status;
        String voStatus;
        if (realStatus.equals(AppSwitchEnum.CLOSING.getCode()) || realStatus.equals(AppSwitchEnum.OPENING.getCode())) {
            vo.setSwitchStutus(realStatus);
            return Response.success(realStatus);
        } else {
            status = (enable ? AppSwitchEnum.OPENED : AppSwitchEnum.CLOSED).getCode();
            voStatus = (enable ? AppSwitchEnum.OPENING : AppSwitchEnum.CLOSING).getCode();
            //开关状态、开关开启、关闭的时间存放在redis
            redisTemplate.opsForValue().set(PRADAR_SILENCE_SWITCH_STATUS_VO + uid, status);
            redisTemplate.opsForValue().set(PRADAR_SILENCE_SWITCH_STATUS + uid, voStatus);
        }
        vo.setSwitchStutus(status);
        return Response.success(vo);
    }


    /**
     * 从redis获取用户静默开关状态，默认开启
     *
     * @param uid
     * @return
     */
    private String getUserSilenceSwitchFromRedis(Long uid) {

        if (uid == null) {
            throw new RuntimeException("用户id不能为空");
        }
        Object statusObj = redisTemplate.opsForValue().get(PRADAR_SILENCE_SWITCH_STATUS_VO + uid);
        if (statusObj == null) {
            redisTemplate.opsForValue().set(PRADAR_SILENCE_SWITCH_STATUS_VO + uid, AppSwitchEnum.OPENED.getCode());
            redisTemplate.opsForValue().set(PRADAR_SILENCE_SWITCH_STATUS + uid, AppSwitchEnum.OPENED.getCode());
        } else {
            return (String)statusObj;
        }
        return AppSwitchEnum.OPENED.getCode();
    }

    /**
     * 获取静默开关状态
     *
     * @return
     */
    @Override
    public Response userAppSilenceSwitchInfo() {
        ApplicationSwitchStatusDTO result = new ApplicationSwitchStatusDTO();
        UserExt user = WebPluginUtils.getUser();
        if (user == null) {
            String userAppKey = WebPluginUtils.getTenantUserAppKey();
             user = WebPluginUtils.getUserByAppKey(userAppKey);
        }
        if (user == null) {
            return Response.fail(FALSE_CORE);
        }
        //体验用户默认状态为开启
        if (user.getRole() != null && user.getRole() == 1) {
            result.setSwitchStatus(AppSwitchEnum.OPENED.getCode());
        } else {
            result.setSwitchStatus(getUserSilenceSwitchStatusForVo(user.getCustomerId()));
        }

        return Response.success(result);
    }

    @Override
    public String getUserSilenceSwitchStatusForVo(Long uid) {
        if (uid == null) {
            return null;
        }
        Object o = redisTemplate.opsForValue().get(PRADAR_SILENCE_SWITCH_STATUS_VO + uid);
        if (o == null) {
            redisTemplate.opsForValue().set(PRADAR_SILENCE_SWITCH_STATUS_VO + uid, AppSwitchEnum.OPENED.getCode());
            return AppSwitchEnum.OPENED.getCode();
        } else {
            return (String)o;
        }
    }

    @Override
    public Response getApplicationReportConfigInfo(Integer type,String appName) {
        List<AppAgentConfigReportDetailResult> results = reportDAO.listByBizType(type,appName);
        //todo 现在只有一种配置 先这么写
        String silenceSwitchStatus = getUserSilenceSwitchStatusForVo(WebPluginUtils.getCustomerId());
        String configValue =  AppSwitchEnum.OPENED.getCode().equals(silenceSwitchStatus)?"true":"false";
        List<AppAgentConfigReportDetailResult> filter = results.stream().filter(x -> configValue.equals(x.getConfigValue())).collect(Collectors.toList());
        return Response.success(filter);
    }

    @Override
    public Boolean silenceSwitchStatusIsTrue(Long uid, AppSwitchEnum appSwitchEnum) {
        String status = this.getUserSilenceSwitchStatusForVo(WebPluginUtils.getCustomerId());
        return appSwitchEnum.getCode().equals(status);
    }


}
