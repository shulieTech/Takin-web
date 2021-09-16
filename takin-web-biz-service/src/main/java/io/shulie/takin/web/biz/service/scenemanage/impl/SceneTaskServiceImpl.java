package io.shulie.takin.web.biz.service.scenemanage.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.constant.AppAccessTypeEnum;
import com.pamirs.takin.common.constant.AppSwitchEnum;
import com.pamirs.takin.common.constant.ConfigConstants;
import com.pamirs.takin.common.constant.Constants;
import com.pamirs.takin.entity.dao.confcenter.TApplicationMntDao;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneBusinessActivityRefDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.ScriptCheckDTO;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import com.pamirs.takin.entity.domain.vo.report.SceneActionParamNew;
import com.pamirs.takin.entity.domain.vo.report.ScenePluginParam;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.open.api.scenetask.CloudTaskApi;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneTaskQueryTpsReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneTaskUpdateTpsReq;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp.SceneBusinessActivityRefResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp.SceneSlaRefResp;
import io.shulie.takin.cloud.open.resp.scenetask.SceneActionResp;
import io.shulie.takin.cloud.open.resp.scenetask.SceneJobStateResp;
import io.shulie.takin.cloud.open.resp.scenetask.SceneTaskAdjustTpsResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceTestRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlBatchRefsRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.SqlTestRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.VerifyTaskConfig;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.UpdateTpsRequest;
import io.shulie.takin.web.biz.pojo.response.scene.StartResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.PluginConfigDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.BaseConfigService;
import io.shulie.takin.web.biz.service.DataSourceService;
import io.shulie.takin.web.biz.service.LeakSqlService;
import io.shulie.takin.web.biz.service.async.AsyncService;
import io.shulie.takin.web.biz.service.report.impl.ReportApplicationService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.service.scenemanage.SceneTaskService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.biz.utils.CopyUtils;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.constant.RemoteConstant;
import io.shulie.takin.web.common.domain.ErrorInfo;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.http.HttpWebClient;
import io.shulie.takin.web.common.util.SceneTaskUtils;
import io.shulie.takin.web.common.vo.scene.BaffleAppVO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.result.application.ApplicationResult;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * @author 莫问
 * @date 2020-04-22
 */
@Service
@Slf4j
public class SceneTaskServiceImpl implements SceneTaskService {

    public static final String PRESSURE_REPORT_ID_SCENE_PREFIX = "pressure:reportId:scene:";

    @Value("${takin.cloud.url}")
    private String cloudUrl;

    @Autowired
    private LeakSqlService leakSqlService;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private HttpWebClient httpWebClient;

    @Autowired
    private SceneManageService sceneManageService;

    @Autowired
    private TApplicationMntDao applicationMntDao;

    @Value("${start.task.check.application: true}")
    private Boolean checkApplication;

    @Autowired
    private SceneTaskApi sceneTaskApi;

    @Autowired
    private SceneManageApi sceneManageApi;

    @Autowired
    private ReportApplicationService reportApplicationService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CloudTaskApi cloudTaskApi;

    @Autowired
    private ScriptManageService scriptManageService;

    @Autowired
    private RedisClientUtils redisClientUtils;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private BaseConfigService baseConfigService;

    /**
     * 查询场景业务活动信息，校验业务活动
     *
     * @return 启动结果
     */
    @Override
    public WebResponse<StartResponse> startTask(SceneActionParam param) {

        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(param.getSceneId());
        ResponseResult<SceneManageWrapperResp> resp = sceneManageApi.getSceneDetail(req);
        if (!resp.getSuccess()) {
            ResponseResult.ErrorInfo errorInfo = resp.getError();
            String errorMsg = Objects.isNull(errorInfo) ? "" : errorInfo.getMsg();
            log.error("takin-cloud查询场景信息返回错误，id={},错误信息：{}", param.getSceneId(), errorMsg);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR,
                getCloudMessage(errorInfo.getCode(), errorInfo.getMsg()));
        }
        String jsonString = JsonHelper.bean2Json(resp.getData());
        SceneManageWrapperDTO sceneData = JsonHelper.json2Bean(jsonString, SceneManageWrapperDTO.class);

        // 校验该场景是否正在压测中
        if (redisClientUtils.hasKey(SceneTaskUtils.getSceneTaskKey(param.getSceneId()))) {
            // 正在压测中
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_STATUS_ERROR,
                "场景，id=" + param.getSceneId() + "已启动压测，请刷新页面！");
        } else {
            // 记录key 过期时长为压测时长
            redisClientUtils.setString(SceneTaskUtils.getSceneTaskKey(param.getSceneId()),
                DateUtil.now(),
                Integer.parseInt(sceneData.getPressureTestTime().getTime().toString()), TimeUnit.MINUTES);
        }

        preCheckStart(sceneData);

        if (sceneData.getScriptId() != null) {
            ScriptManageDeployDetailResponse scriptManageDeployDetail = scriptManageService.getScriptManageDeployDetail(
                sceneData.getScriptId());
            List<PluginConfigDetailResponse> pluginConfigDetailResponseList = scriptManageDeployDetail
                .getPluginConfigDetailResponseList();
            if (CollectionUtils.isNotEmpty(pluginConfigDetailResponseList)) {
                List<Long> pluginIds = pluginConfigDetailResponseList.stream().map(o -> Long.parseLong(o.getName()))
                    .collect(Collectors.toList());
                param.setEnginePluginIds(pluginIds);
                param.setEnginePlugins(pluginConfigDetailResponseList.stream()
                    .map(detail -> new ScenePluginParam() {{
                        setPluginId(Long.parseLong(detail.getName()));
                        setPluginVersion(detail.getVersion());
                    }}).collect(Collectors.toList()));
            }
        }

        param.setRequestUrl(RemoteConstant.SCENE_TASK_START_URL);
        param.setHttpMethod(HttpMethod.POST);
        //封装
        WebResponse<StartResponse> response;
        //兼容老版本
        if (StringUtils.isEmpty(param.getContinueRead())) {
            param.setContinueRead("-1");
        }
        //新版本位点
        SceneActionParamNew paramNew = this.getNewParam(param);
        response = httpWebClient.request(paramNew);

        if (!response.getSuccess()) {
            ErrorInfo errorInfo = response.getError();
            String errorMsg = Objects.isNull(errorInfo) ? "" : errorInfo.getMsg();
            log.error("takin-cloud启动压测场景返回错误，id={},错误信息：{}", param.getSceneId(), errorMsg);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR,
                getCloudMessage(errorInfo.getCode(), errorInfo.getMsg()));
        }
        // 缓存 报告id
        cacheReportId(response, param);

        return response;
    }

    /**
     * 返回cloud 数据
     *
     * @param code     -
     * @param errorMsg -
     * @return -
     */
    private String getCloudMessage(String code, String errorMsg) {
        return String.format("takin-cloud启动场景失败，异常代码【%s】,异常原因【%s】", code, errorMsg);
    }

    private void cacheReportId(WebResponse<?> request, SceneActionParam param) {
        Object data = request.getData();
        if (data == null) {
            log.info("start scene response return data is  illegal！ sceneId:{}", param.getSceneId());
            return;
        }
        String jsonString = JsonHelper.bean2Json(data);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        if (!jsonObject.containsKey("data")) {
            log.info("start scene response return data is  illegal！ sceneId:{}", param.getSceneId());
            return;
        }
        Integer reportId = (Integer)jsonObject.get("data");
        redisTemplate.opsForValue().set(getCacheReportId(param.getSceneId()), reportId);
        redisTemplate.expire(getCacheReportId(param.getSceneId()), 1L, TimeUnit.DAYS);
    }

    private SceneActionParamNew getNewParam(SceneActionParam param) {
        SceneActionParamNew paramNew = CopyUtils.copyFields(param, SceneActionParamNew.class);
        try {
            //            paramNew.setContinueRead(false);
            //            if (!param.getContinueRead().equals("-1")) {
            //                Object hasUnread = redisTemplate.opsForValue().get("hasUnread_" + param.getSceneId());
            //                if (hasUnread == null) {
            //                    throw ApiException.create(500, "缺少参数hasUnread！无法判断继续压测还是从头压测，请检查redis或者cloud返回的位点数据是否有问题，id=" + param.getSceneId());
            //                }
            //                if (param.getContinueRead().equals("1")) {
            //                    paramNew.setContinueRead(Boolean.parseBoolean(hasUnread + ""));
            //                } else {
            //                    paramNew.setContinueRead(false);
            //                }
            //            }
            paramNew.setContinueRead("1".equals(param.getContinueRead()));
        } catch (Exception e) {
            log.error("未知异常", e);
        } finally {
            //            redisTemplate.delete("hasUnread_" + param.getSceneId());
        }
        return paramNew;
    }

    private String getCacheReportId(Long sceneId) {
        return PRESSURE_REPORT_ID_SCENE_PREFIX + sceneId;
    }

    @Override
    public ResponseResult<String> stopTask(SceneActionParam param) {
        // 释放 场景锁
        redisClientUtils.delete(SceneTaskUtils.getSceneTaskKey(param.getSceneId()));
        // 停止先删除 redis中的
        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(param.getSceneId());
        ResponseResult<SceneActionResp> response = sceneTaskApi.checkTask(req);
        if (!response.getSuccess()) {
            throw new TakinWebException(ExceptionCode.SCENE_STOP_ERROR, response.getError());
        }
        SceneActionResp resp = response.getData();
        redisClientUtils.del(String.format(WebRedisKeyConstant.PTING_APPLICATION_KEY, resp.getReportId()));
        // 最后删除
        return sceneTaskApi.stopTask(req);
    }

    @Override
    public ResponseResult<SceneActionResp> checkStatus(Long sceneId, Long frontReportId) {
        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(sceneId);
        req.setReportId(frontReportId);
        ResponseResult<SceneActionResp> response = sceneTaskApi.checkTask(req);
        if (!response.getSuccess()) {
            throw new TakinWebException(ExceptionCode.SCENE_CHECK_ERROR, response.getError());
        }
        SceneActionResp resp = response.getData();
        //非压测中
        if (response.getData() == null || resp.getData() != 2L) {
            // 解除 场景锁
            redisClientUtils.delete(SceneTaskUtils.getSceneTaskKey(sceneId));
            return response;
        }
        Long reportId = resp.getReportId();
        //获取场景SLA 内存 | cpu
        ResponseResult<SceneManageWrapperResp> detailResp = sceneManageApi.getSceneDetail(req);
        if (!detailResp.getSuccess()) {
            return response;
        }
        SceneManageWrapperResp wrapperResp = JSONObject.parseObject(JSON.toJSONString(detailResp.getData()),
            SceneManageWrapperResp.class);
        // 缓存压测中的应用
        List<SceneBusinessActivityRefResp> sceneBusinessActivityRefList = wrapperResp.getBusinessActivityConfig();
        //从活动中提取应用ID，去除重复ID
        List<Long> applicationIds = sceneBusinessActivityRefList.stream()
            .map(SceneBusinessActivityRefResp::getApplicationIds)
            .filter(StringUtils::isNotEmpty)
            .flatMap(appIds -> Arrays.stream(appIds.split(",")).map(Long::parseLong))
            .filter(data -> data > 0L).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(applicationIds)) {
            List<TApplicationMnt> applicationMntList = applicationMntDao.queryApplicationMntListByIds(applicationIds);
            List<String> applicationNames = applicationMntList.stream().map(TApplicationMnt::getApplicationName)
                .collect(Collectors.toList());
            // 过期时间，根据 压测时间 + 10s
            redisClientUtils.set(String.format(WebRedisKeyConstant.PTING_APPLICATION_KEY, reportId), applicationNames,
                wrapperResp.getPressureTestSecond() + 10);
        }
        Map<String, List<SceneSlaRefResp>> slaMap = getSceneSla(wrapperResp);
        if (MapUtils.isNotEmpty(slaMap)) {
            //获取应用列表
            List<String> appNames = reportApplicationService.getReportApplication(reportId).getApplicationNames();
            // 创建监控线程，监控cpu及memory
            if (CollectionUtils.isNotEmpty(appNames)) {
                asyncService.monitorCpuMemory(sceneId, reportId, appNames, slaMap.get("stop"), slaMap.get("warn"));
            }
        }
        return response;
    }

    @Override
    public void updateTaskTps(UpdateTpsRequest request) {
        SceneTaskUpdateTpsReq req = new SceneTaskUpdateTpsReq();
        req.setSceneId(request.getSceneId());
        req.setReportId(request.getReportId());
        req.setTpsNum(request.getTargetTps());
        req.setLicense(WebPluginUtils.getTenantUserAppKey());
        ResponseResult<String> responseResult = cloudTaskApi.updateSceneTaskTps(req);
        if (responseResult == null || !responseResult.getSuccess()) {
            throw new RuntimeException("修改TPS失败");
        }
    }

    @Override
    public Long queryTaskTps(Long reportId, Long sceneId) {
        SceneTaskQueryTpsReq req = new SceneTaskQueryTpsReq();
        req.setSceneId(sceneId);
        req.setReportId(reportId);
        req.setLicense(WebPluginUtils.getTenantUserAppKey());
        ResponseResult<SceneTaskAdjustTpsResp> respResponseResult = cloudTaskApi.queryAdjustTaskTps(req);
        if (respResponseResult != null && respResponseResult.getData() != null) {
            return respResponseResult.getData().getTotalTps();
        }
        return null;
    }

    @Override
    public List<String> checkBusinessActivitiesConfig(List<Long> businessActivityIds) {
        // 校验压测场景下是否存在漏数配置
        LeakSqlBatchRefsRequest refsRequest = new LeakSqlBatchRefsRequest();
        refsRequest.setBusinessActivityIds(businessActivityIds);
        List<VerifyTaskConfig> verifyTaskConfigList = leakSqlService.getVerifyTaskConfig(refsRequest);

        if (CollectionUtils.isEmpty(verifyTaskConfigList)) {
            return Collections.singletonList("关联的业务活动暂未配置漏数脚本");
        }

        // 校验数据源是否能正常连接
        List<String> errorDbMsgList = verifyTaskConfigList.stream()
            .map(verifyTaskConfig -> {
                DataSourceTestRequest testRequest = new DataSourceTestRequest();
                testRequest.setJdbcUrl(verifyTaskConfig.getJdbcUrl());
                testRequest.setUsername(verifyTaskConfig.getUsername());
                testRequest.setPassword(verifyTaskConfig.getPassword());
                testRequest.setType(verifyTaskConfig.getType());
                return dataSourceService.testConnection(testRequest);
            })
            .filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(errorDbMsgList)) {
            return errorDbMsgList.stream().sorted().collect(Collectors.toList());
        }

        // 校验sql是否能正常运行
        List<String> errorSqlMsgList = verifyTaskConfigList.stream()
            .map(verifyTaskConfig -> {
                SqlTestRequest testRequest = new SqlTestRequest();
                testRequest.setDatasourceId(verifyTaskConfig.getDatasourceId());
                testRequest.setSqls(verifyTaskConfig.getSqls());
                return leakSqlService.testSqlConnection(testRequest);
            }).filter(StringUtils::isNotBlank).map(msg -> {
                if (msg.contains(AppConstants.COMMA)) {
                    return Arrays.asList(msg.split(AppConstants.COMMA));
                } else {
                    return Collections.singletonList(msg);
                }
            }).flatMap(Collection::stream).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(errorSqlMsgList)) {
            return errorSqlMsgList.stream().sorted().collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private void preCheckStart(SceneManageWrapperDTO sceneData) {
        //检查压测开关，压测开关处于关闭状态时禁止压测
        String userSwitchStatusForVo = applicationService.getUserSwitchStatusForVo();
        if (StringUtils.isBlank(userSwitchStatusForVo) || !userSwitchStatusForVo.equals(
            AppSwitchEnum.OPENED.getCode())) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_STATUS_ERROR, "压测开关处于关闭状态，禁止压测");
        }

        //检查场景是否存可以开启启压测
        List<SceneBusinessActivityRefDTO> sceneBusinessActivityRefList = sceneData.getBusinessActivityConfig();
        if (CollectionUtils.isEmpty(sceneBusinessActivityRefList)) {
            log.error("[{}]场景没有配置业务活动", sceneData.getId());
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_VALIDATE_ERROR, "启动压测失败，没有配置业务活动，场景ID为" + sceneData.getId());
        }

        // 业务活动相关检查
        this.checkBusinessActivity(sceneData, sceneBusinessActivityRefList);
    }

    /**
     * 检查业务活动相关
     *
     * @param sceneData                    入参
     * @param sceneBusinessActivityRefList 业务活动
     */
    private void checkBusinessActivity(SceneManageWrapperDTO sceneData,
        List<SceneBusinessActivityRefDTO> sceneBusinessActivityRefList) {
        //需求要求，业务验证异常需要详细输出
        StringBuilder errorMsg = new StringBuilder();

        //从活动中提取应用ID，去除重复ID
        List<Long> applicationIds = sceneBusinessActivityRefList.stream()
            .map(SceneBusinessActivityRefDTO::getApplicationIds).filter(StringUtils::isNotEmpty)
            .flatMap(appIds -> Arrays.stream(appIds.split(","))
                .map(Long::valueOf)).filter(data -> data > 0L).distinct().collect(Collectors.toList());

        // 应用相关检查
        if (!CollectionUtils.isEmpty(applicationIds) && checkApplication) {
            List<TApplicationMnt> applicationMntList = applicationMntDao.queryApplicationMntListByIds(applicationIds);
            // todo 临时方案，过滤挡板应用
            TBaseConfig config = baseConfigService.queryByConfigCode(ConfigConstants.SCENE_BAFFLE_APP_CONFIG);
            if (config != null && StringUtils.isNotBlank(config.getConfigValue())) {
                try {
                    List<BaffleAppVO> baffleAppVos = JsonHelper.json2List(config.getConfigValue(), BaffleAppVO.class);
                    List<String> appNames = Optional
                        .of(baffleAppVos.stream().filter(appVO -> sceneData.getId() != null && sceneData.getId().equals(appVO.getSceneId()))
                            .collect(Collectors.toList()))
                        .map(t -> t.get(0)).map(BaffleAppVO::getAppName).orElse(Lists.newArrayList());
                    List<Long> appIds = Lists.newArrayList();

                    List<TApplicationMnt> tempApps = applicationMntList.stream().filter(app -> {
                        if (appNames.contains(app.getApplicationName())) {
                            // 用于过滤应用id
                            appIds.add(app.getApplicationId());
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    List<Long> tempAppIds = applicationIds.stream().filter(id -> !appIds.contains(id)).collect(Collectors.toList());
                    applicationMntList = tempApps;
                    applicationIds = tempAppIds;
                } catch (Exception e) {
                    log.error("场景挡板配置转化异常：配置项：{},配置项内容:{}", ConfigConstants.SCENE_BAFFLE_APP_CONFIG, config.getConfigValue());
                }
            }
            if (CollectionUtils.isEmpty(applicationMntList) || applicationMntList.size() != applicationIds.size()) {
                log.error("启动压测失败, 没有找到关联的应用信息，场景ID：{}", sceneData.getId());
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_VALIDATE_ERROR, "启动压测失败, 没有找到关联的应用信息，场景ID：" + sceneData.getId());
            }

            // 检查应用相关
            errorMsg.append(this.checkApplicationCorrelation(applicationMntList));
        }

        // 压测脚本文件检查
        String scriptCorrelation = this.checkScriptCorrelation(sceneData);
        errorMsg.append(scriptCorrelation == null ? "" : scriptCorrelation);
        if (errorMsg.length() > 0) {
            String msg;
            if (errorMsg.toString().endsWith(Constants.SPLIT)) {
                msg = StringUtils.substring(errorMsg.toString(), 0, errorMsg.toString().length() - 1);
            } else {
                msg = errorMsg.toString();
            }
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_VALIDATE_ERROR, msg);
        }
    }

    /**
     * 脚本相关检查
     *
     * @param sceneData 所需参数
     */
    @Override
    public String checkScriptCorrelation(SceneManageWrapperDTO sceneData) {
        ScriptCheckDTO scriptCheck = sceneManageService.checkBusinessActivityAndScript(sceneData);
        return scriptCheck.getErrmsg();
    }

    /**
     * 应用相关检查
     *
     * @param applicationMntList 应用列表
     * @return 错误信息
     */
    @Override
    public String checkApplicationCorrelation(List<TApplicationMnt> applicationMntList) {
        // 查询下 应用节点信息 节点不一致 也需要返回异常
        List<String> appNames = applicationMntList.stream().map(TApplicationMnt::getApplicationName).collect(Collectors.toList());
        // 从大数据里查出数据 todo 目前大数据不区分客户，所以有可能存在不准确问题
        List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(appNames);
        Map<String, List<ApplicationResult>> listMap = applicationResultList.stream().collect(Collectors.groupingBy(ApplicationResult::getAppName));

        List<String> applicationNameList = applicationMntList.stream().map(application -> {
            boolean statusError = false;

            if (!AppSwitchEnum.OPENED.getCode().equals(application.getSwitchStatus())) {
                log.error("应用[{}]未开启", application.getApplicationName());
                statusError = true;
            }

            if (!AppAccessTypeEnum.NORMAL.getValue().equals(application.getAccessStatus())) {
                log.error("应用[{}]接入状态不是开启状态，当前状态[{}]", application.getApplicationName(),
                    application.getAccessStatus());
                statusError = true;
            }
            //判断节点数是否异常
            Integer totalNodeCount = application.getNodeNum();
            List<ApplicationResult> results = listMap.get(application.getApplicationName());
            if (CollectionUtils.isEmpty(results) || !totalNodeCount.equals(results.get(0).getInstanceInfo().getInstanceOnlineAmount())) {
                log.error("应用[{}]在线节点数与节点总数不一致", application.getApplicationName());
                statusError = true;
            }

            return statusError ? application.getApplicationName() : "";
        }).filter(StrUtil::isNotBlank).collect(Collectors.toList());

        if (applicationNameList.isEmpty()) {
            return "";
        }

        return String.format("%d个应用状态不正常：%s", applicationNameList.size(),
            StringUtils.join(applicationNameList.toArray(), AppConstants.COMMA)) + Constants.SPLIT;
    }

    @Override
    public Long getReportIdFromCache(Long sceneId) {
        String cacheReportId = getCacheReportId(sceneId);
        Object reportId = redisTemplate.opsForValue().get(cacheReportId);
        if (reportId == null) {
            return null;
        }
        return Long.valueOf((Integer)reportId);
    }

    @Override
    public ResponseResult<SceneJobStateResp> checkSceneJobStatus(Long sceneId) {
        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(sceneId);
        ResponseResult<SceneJobStateResp> response = sceneTaskApi.checkJobStateStatus(req);
        if (!response.getSuccess()) {
            throw new TakinWebException(ExceptionCode.SCENE_CHECK_ERROR, response.getError());
        }

        return response;
    }

    private Map<String, List<SceneSlaRefResp>> getSceneSla(SceneManageWrapperResp detailResp) {
        Map<String, List<SceneSlaRefResp>> dataMap = Maps.newHashMap();
        List<SceneSlaRefResp> stopList = Optional.ofNullable(detailResp.getStopCondition()).orElse(
            Lists.newArrayList());
        stopList = stopList.stream().filter(data -> data.getRule().getIndexInfo() >= 4).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(stopList)) {
            dataMap.put("stop", stopList);
        }
        List<SceneSlaRefResp> warnList = Optional.ofNullable(detailResp.getWarningCondition()).orElse(
            Lists.newArrayList());
        warnList = warnList.stream().filter(data -> data.getRule().getIndexInfo() >= 4).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(warnList)) {
            dataMap.put("warn", warnList);
        }
        return dataMap;
    }
}
