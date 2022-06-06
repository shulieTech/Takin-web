package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.constant.Constants;
import com.pamirs.takin.common.constant.VerifyTypeEnum;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneBusinessActivityRefDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import io.shulie.takin.adapter.api.entrypoint.file.CloudFileApi;
import io.shulie.takin.adapter.api.entrypoint.scene.mix.SceneMixApi;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageDeleteReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageQueryReq;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneDetailV2Response;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneActionResp;
import io.shulie.takin.cloud.common.utils.JmxUtil;
import io.shulie.takin.cloud.ext.content.enginecall.PtConfigExt;
import io.shulie.takin.cloud.ext.content.enginecall.ThreadGroupConfigExt;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PressureConfigRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskStartRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.scene.NewSceneRequest;
import io.shulie.takin.web.biz.pojo.request.scene.SceneDetailResponse;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskCreateRequest;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneSchedulerTaskResponse;
import io.shulie.takin.web.biz.service.LeakSqlService;
import io.shulie.takin.web.biz.service.VerifyTaskService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformancePressureService;
import io.shulie.takin.web.biz.service.linkmanage.LinkManageService;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.service.scenemanage.SceneSchedulerTaskService;
import io.shulie.takin.web.biz.service.scenemanage.SceneTaskService;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.common.util.SceneTaskUtils;
import io.shulie.takin.web.data.dao.SceneExcludedApplicationDAO;
import io.shulie.takin.web.data.dao.filemanage.FileManageDAO;
import io.shulie.takin.web.data.dao.interfaceperformance.PerformanceConfigDAO;
import io.shulie.takin.web.data.dao.scriptmanage.ScriptFileRefDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigMapper;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigSceneRelateShipMapper;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceParamMapper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigSceneRelateShipEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceParamEntity;
import io.shulie.takin.web.data.model.mysql.SceneEntity;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import io.shulie.takin.web.data.result.scriptmanage.ScriptFileRefResult;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: vernon
 * @Date: 2022/5/20 10:51
 * @Description:
 */
public abstract class AbstractPerformancePressureService
        extends PerformancePressureAdaptor implements PerformancePressureService {


    @Value("${takin.cloud.backend.url}")
    protected String urlOfCloud;

    protected String uriOfScriptGenerator = "/script/build";

    Long fetchSceneId(Long apiId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("api_id", apiId);
        queryWrapper.eq("is_deleted", 0);
        return performanceConfigSceneRelateShipMapper.selectOne(queryWrapper).getSceneId();
    }

    @Override
    public Long querySceneId(Long apiId) {
        return fetchSceneId(apiId);
    }

    /**
     * 删除业务流程
     * 删除关联映射
     *
     * @param apiId
     */
    public void doAfterDelete(Long apiId) {
        //删业务流程
        Long flowId = fetBizFlowId(apiId);
        if (Objects.nonNull(flowId)) {
            // linkManageService.deleteScene(flowId.toString());
        }
        //最后删映射表
        QueryWrapper deleteWrapper = new QueryWrapper();
        deleteWrapper.eq("api_id", apiId);
        performanceConfigSceneRelateShipMapper.delete(deleteWrapper);
    }

    @Override
    public ResponseResult delete(Long apiId) {
        Long sceneId = fetchSceneId(apiId);
        SceneManageDeleteReq deleteReq = new SceneManageDeleteReq();
        deleteReq.setId(sceneId);
        sceneManageService.deleteScene(deleteReq);
        doAfterDelete(apiId);
        return ResponseResult.success();
    }


    @Override
    public Boolean update(PerformanceDataFileRequest input) {
        try {
            Long apiId = input.getId();

            PerformanceConfigCreateInput updateIn = new PerformanceConfigCreateInput();
            NewSceneRequest configRequest = NewSceneRequest.DEFAULT;
            ResponseResult<SceneDetailResponse> responseResponseResult = query(apiId);
            if (responseResponseResult != null) {
                SceneDetailResponse pressureConfigDetail = responseResponseResult.getData();
                //build baseInfo
                NewSceneRequest.BasicInfo basicInfo = buildBaseInfo(pressureConfigDetail);
                //build PtConfig
                NewSceneRequest.PtConfig ptConfig = buildPtConfig(pressureConfigDetail);
                configRequest.setBasicInfo(basicInfo);
                configRequest.setConfig(ptConfig);
                SceneRequest.Goal targetGoal = buildTargetGoal(pressureConfigDetail);
                configRequest.setTargetGoal(targetGoal);
                NewSceneRequest.ThreadGroup threadConfig = buildThreadConfig(pressureConfigDetail);
                configRequest.setThreadConfig(threadConfig);

            }

            configRequest.setId(apiId);
            updateIn.setId(apiId);
            updateIn.setPressureConfigRequest(configRequest);
            //填充基本字段
            InterfacePerformanceConfigEntity configEntity = configMapper.selectById(apiId);
            updateIn.setName(configEntity.getName());
            updateIn.setId(configEntity.getId());
            updateIn.setBody(configEntity.getBody());
            updateIn.setCookies(configEntity.getCookies());
            updateIn.setHeaders(configEntity.getHeaders());
            updateIn.setHttpMethod(configEntity.getHttpMethod());
            updateIn.setEntranceAppName(configEntity.getEntranceAppName());
            updateIn.setRequestUrl(configEntity.getRequestUrl());


            this.update(updateIn);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
        return true;
    }

    private NewSceneRequest.ThreadGroup buildThreadConfig(SceneDetailResponse pressureConfigDetail) {
        NewSceneRequest.ThreadGroup threadGroup = new NewSceneRequest.ThreadGroup();

        PtConfigExt ptConfigExt = pressureConfigDetail.getConfig();
        ThreadGroupConfigExt threadGroupConfigExt =
                ptConfigExt.getThreadGroupConfigMap().values().iterator().next();

        threadGroup.setUnit(ptConfigExt.getUnit());
        threadGroup.setSteps(threadGroupConfigExt.getSteps());
        threadGroup.setThreadNum(threadGroupConfigExt.getThreadNum());
        threadGroup.setType(threadGroupConfigExt.getType());
        threadGroup.setDuration(ptConfigExt.getDuration());
        threadGroup.setRampUpUnit(threadGroupConfigExt.getRampUpUnit());
        threadGroup.setRampUp(threadGroupConfigExt.getRampUp());
        threadGroup.setPodNum(ptConfigExt.getPodNum());
        threadGroup.setMode(threadGroupConfigExt.getMode());
        return threadGroup;
    }

    private SceneRequest.Goal buildTargetGoal(SceneDetailResponse pressureConfigDetail) {
        SceneRequest.Goal source = pressureConfigDetail.getGoal().values().iterator().next();
        return source;
    }

    @Override
    public ResponseResult<Boolean> update(PerformanceConfigCreateInput in) throws Throwable {
      /*  PressureConfigRequest request = in.getPressureConfigRequest();
        request.getBasicInfo().setSceneId(fetchSceneId(in.getId()));
        if (null == request.getBasicInfo().getSceneId()) {
            return ResponseResult.fail(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR.getErrorCode(), "压测场景ID不能为空");
        }
        SceneRequest sceneRequest = buildSceneRequest(request);
        WebPluginUtils.fillCloudUserData(sceneRequest);
        Boolean updateResult = multipleSceneApi.update(sceneRequest);
        sceneSchedulerTaskService.deleteBySceneId(request.getBasicInfo().getSceneId());
        if (Boolean.TRUE.equals(request.getBasicInfo().getIsScheduler())) {
            sceneSchedulerTaskService.insert(new SceneSchedulerTaskCreateRequest() {{
                setSceneId(request.getBasicInfo().getSceneId());
                setExecuteTime(request.getBasicInfo().getExecuteTime());
            }});
        }

        // 先删除
        sceneExcludedApplicationDAO.removeBySceneId(request.getBasicInfo().getSceneId());

        // 忽略检测的应用
        sceneManageService.createSceneExcludedApplication(request.getBasicInfo().getSceneId(), request.getDataValidation().getExcludedApplicationIds());
*/
        /**
         * 逻辑太复杂了,直接删了重建吧
         */
        delete(in.getId());
        add(in);
        return ResponseResult.success(Boolean.TRUE);
    }

    @Override
    public ResponseResult<Long> add(PerformanceConfigCreateInput in) throws Throwable {
        NewSceneRequest request = in.getPressureConfigRequest();
        SceneRequest sceneRequest = buildSceneRequest(request);

        WebPluginUtils.fillCloudUserData(sceneRequest);
        Long sceneId = multipleSceneApi.create(sceneRequest);
        if (Boolean.TRUE.equals(request.getBasicInfo().getIsScheduler())) {
            sceneSchedulerTaskService.insert(new SceneSchedulerTaskCreateRequest() {{
                setSceneId(sceneId);
                setExecuteTime(request.getBasicInfo().getExecuteTime());
            }});
        }

        // 忽略检测的应用
        sceneManageService.createSceneExcludedApplication(request.getBasicInfo().getSceneId(), request.getDataValidation().getExcludedApplicationIds());

        // 操作日志
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_ID, String.valueOf(sceneId));

        if (null != request.getBasicInfo() && null != request.getBasicInfo().getName()) {
            OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_NAME, request.getBasicInfo().getName());
        }
        Long apiId = in.getId();
        InterfacePerformanceConfigSceneRelateShipEntity
                entity = new InterfacePerformanceConfigSceneRelateShipEntity();
        entity.setApiId(apiId);
        entity.setSceneId(sceneId);
        entity.setIsDeleted(0);
        entity.setEnvCode(in.getEnvCode());
        entity.setTenantId(in.getTenantId());
        this.upsert(entity);
        return ResponseResult.success(sceneId);
    }

    public void upsert(InterfacePerformanceConfigSceneRelateShipEntity entity) {
        Long apiId = entity.getApiId();

        QueryWrapper exitQuery = new QueryWrapper();
        exitQuery.eq("api_id", apiId);
        exitQuery.eq("is_deleted", 0);
        boolean exit = false;
        exit = performanceConfigSceneRelateShipMapper.selectCount(exitQuery) > 0 ? true : exit;
        if (!exit) {
            performanceConfigSceneRelateShipMapper.insert(entity);
            return;
        }
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("api_id", apiId);
        performanceConfigSceneRelateShipMapper.update(entity, updateWrapper);
    }

    @Override
    public SceneEntity bizFlowDetailByApiId(Long apiId) {
        Long flowId = fetBizFlowId(apiId);
        if (flowId == null) {
            return null;
        }
        return bizFlowDetailByFlowId(flowId);
    }

    public SceneEntity bizFlowDetailByFlowId(Long flowId) {
        return sceneService.businessActivityFlowDetail(flowId);
    }

    @Resource
    InterfacePerformanceConfigMapper configMapper;
    @Resource
    InterfacePerformanceParamMapper paramMapper;

    /**
     * 文件管理的dao
     */
    @Resource
    private FileManageDAO fileManageDAO;

    @Autowired
    SceneExcludedApplicationDAO sceneExcludedApplicationDAO;
    @Resource
    SceneService sceneService;
    @Resource
    FileManageDAO fileManageDao;
    @Resource
    LeakSqlService leakSqlService;
    @Resource
    SceneMixApi multipleSceneApi;
    @Resource
    ScriptFileRefDAO scriptFileRefDao;
    @Resource
    SceneManageService sceneManageService;

    /**
     * 场景和接口关联表
     */
    @Resource
    InterfacePerformanceConfigSceneRelateShipMapper performanceConfigSceneRelateShipMapper;

    @Resource
    PerformanceConfigDAO performanceConfigDAO;

    @Resource
    private LinkManageService linkManageService;

    /**
     * 文件处理
     */
    @Resource
    CloudFileApi cloudFileApi;
    /**
     * 定时压测
     */
    @Resource
    SceneSchedulerTaskService sceneSchedulerTaskService;

    @Autowired
    private SceneExcludedApplicationDAO excludedApplicationDAO;
    @Autowired
    private SceneTaskService sceneTaskService;

    @Autowired
    private RedisClientUtil redisClientUtil;

    @Autowired
    private SceneTaskApi sceneTaskApi;

    @Autowired
    private VerifyTaskService verifyTaskService;

    List<InterfacePerformanceParamEntity> fetchParamEntryByApiId(Long apiId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("config_id", apiId);
        return paramMapper.selectList(queryWrapper);
    }

    private boolean doStartCheck() {
        // TODO: 2022/5/20   后面从5.6.0合过来
        return true;
    }

    private void setStartLogVars(SceneManageWrapperDTO sceneData) {
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_ID, String.valueOf(sceneData.getId()));
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_NAME, sceneData.getPressureTestSceneName());
    }

    private List<String> checkVerifyTaskConfig(SceneActionParam param, SceneManageWrapperDTO sceneData) {
        //校验漏数验证时间间隔是否配置
        if ((param.getLeakSqlEnable() != null && param.getLeakSqlEnable())) {
            if (Objects.isNull(sceneData.getScheduleInterval())) {
                return Collections.singletonList("请先配置漏数验证时间间隔");
            }

            //校验压测场景下是否存在漏数配置
            List<Long> businessActivityIds = sceneData.getBusinessActivityConfig().stream()
                    .map(SceneBusinessActivityRefDTO::getBusinessActivityId)
                    .collect(Collectors.toList());
            return sceneTaskService.checkBusinessActivitiesConfig(businessActivityIds);
        }

        return null;
    }

    private void startCheckLeakTask(SceneActionParam param, SceneManageWrapperDTO sceneData) {
        if (param.getLeakSqlEnable() != null && param.getLeakSqlEnable()) {
            //添加开始漏数的查询效果
            LeakVerifyTaskStartRequest startRequest = new LeakVerifyTaskStartRequest();
            startRequest.setRefType(VerifyTypeEnum.SCENE.getCode());
            startRequest.setRefId(param.getSceneId());
            //查询报告id
            SceneManageIdReq req = new SceneManageIdReq();
            req.setId(param.getSceneId());
            WebPluginUtils.fillCloudUserData(req);
            ResponseResult<SceneActionResp> response = sceneTaskApi.checkTask(req);
            SceneActionResp resp = JsonHelper.json2Bean(JsonHelper.bean2Json(response.getData()),
                    SceneActionResp.class);
            startRequest.setReportId(resp.getReportId());
            startRequest.setTimeInterval(sceneData.getScheduleInterval());
            List<Long> businessActivityIds =
                    sceneData.getBusinessActivityConfig().stream().map(
                            SceneBusinessActivityRefDTO::getBusinessActivityId).collect(
                            Collectors.toList());
            startRequest.setBusinessActivityIds(businessActivityIds);
            verifyTaskService.start(startRequest);
        }
    }

    public ResponseResult<SceneActionResp> start(SceneActionParam param) {
        // TODO: 2022/5/20 报错信息合并后再改
        Assert.isTrue(doStartCheck(), "启动检查失败.");
        Long sceneId = fetchSceneId(param.getSceneId());
        param.setSceneId(sceneId);
        try {
            ResponseResult<SceneManageWrapperResp> webResponse = sceneManageService.detailScene(param.getSceneId());
            OperationLogContextHolder.operationType(BizOpConstants.OpTypes.START);

            SceneManageWrapperDTO sceneData = BeanUtil.copyProperties(webResponse.getData(), SceneManageWrapperDTO.class);
            setStartLogVars(sceneData);

            //检测验证数据漏数任务
            List<String> errorMsgList = checkVerifyTaskConfig(param, sceneData);
            if (CollectionUtils.isNotEmpty(errorMsgList)) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, StringUtils.join(errorMsgList, Constants.SPLIT));
            }
            param.setResourceName(sceneData.getPressureTestSceneName());
            SceneActionResp startTaskResponse = sceneTaskService.startTask(param);
            // 开启漏数
            startCheckLeakTask(param, sceneData);
            return ResponseResult.success(startTaskResponse);
        } catch (TakinWebException ex) {
            // 解除 场景锁
            redisClientUtil.delete(SceneTaskUtils.getSceneTaskKey(param.getSceneId()));
            SceneActionResp sceneStart = new SceneActionResp();
            //sceneStart.setMsg(Arrays.asList(StringUtils.split(ex.getMessage(), Constants.SPLIT)));
            List<String> message = Lists.newArrayList();
            if (StringUtils.isNotBlank(ex.getMessage())) {
                message.addAll(Collections.singletonList(ex.getMessage()));
            }
            if (ex.getSource() != null) {
                message.addAll(Collections.singletonList(JsonHelper.bean2Json(ex.getSource())));
            }
            sceneStart.setMsg(message.stream().distinct().collect(Collectors.toList()));
            return ResponseResult.success(sceneStart);
        }
    }


    public NewSceneRequest convert(SceneDetailResponse in, Long apiId) {
        NewSceneRequest result = new NewSceneRequest();


        SceneRequest.Goal goal = in.getGoal().values().iterator().next();
        result.setTargetGoal(goal);

        NewSceneRequest.ThreadGroup threadGroup = new NewSceneRequest.ThreadGroup();
        PtConfigExt ptConfigExt = in.getConfig();

        ThreadGroupConfigExt threadGroupConfigExt =
                ptConfigExt.getThreadGroupConfigMap().values().iterator().next();

        threadGroup.setDuration(ptConfigExt.getDuration());
        threadGroup.setThreadNum(threadGroupConfigExt.getThreadNum());
        threadGroup.setMode(threadGroupConfigExt.getMode());
        threadGroup.setType(threadGroupConfigExt.getType());
        threadGroup.setPodNum(ptConfigExt.getPodNum());
        threadGroup.setRampUp(threadGroupConfigExt.getRampUp());
        threadGroup.setRampUpUnit(threadGroupConfigExt.getRampUpUnit());
        threadGroup.setSteps(threadGroupConfigExt.getSteps());
        threadGroup.setUnit(ptConfigExt.getUnit());
        result.setThreadConfig(threadGroup);
        //=====
        result.setWarnMonitoringGoal(Lists.newArrayList());
        result.setDestroyMonitoringGoal(Lists.newArrayList());
        result.setGoal(Maps.newHashMap());
        result.setBasicInfo(new NewSceneRequest.BasicInfo());
        result.setConfig(new NewSceneRequest.PtConfig());
        result.setDataValidation(new NewSceneRequest.DataValidation());
        return result;
    }

    @Override
    public ResponseResult<SceneDetailResponse> query(Long apiId) throws Throwable {
        Long sceneId = fetchSceneId(apiId);
        SceneManageQueryReq request = new SceneManageQueryReq() {
            {
                setSceneId(sceneId);
            }
        };
        WebPluginUtils.fillCloudUserData(request);
        SceneDetailV2Response detailResult = null;
        try {
            detailResult = multipleSceneApi.detail(request);
        } catch (RuntimeException e) {
            return null;
        }

        SceneDetailResponse copyDetailResult = BeanUtil.copyProperties(detailResult, SceneDetailResponse.class);
        copyDetailResult.setBasicInfo(BeanUtil.copyProperties(detailResult.getBasicInfo(), SceneDetailResponse.BasicInfo.class));
        //计算场景的定时执行时间
        SceneSchedulerTaskResponse sceneSchedulerResponse = sceneSchedulerTaskService.selectBySceneId(sceneId);
        if (sceneSchedulerResponse == null) {
            copyDetailResult.getBasicInfo().setIsScheduler(false);
        } else {
            copyDetailResult.getBasicInfo().setIsScheduler(true);
            copyDetailResult.getBasicInfo().setExecuteTime(DateUtil.formatDateTime(sceneSchedulerResponse.getExecuteTime()));
        }

        // 添加排除的应用
        List<Long> excludedApplicationIds = excludedApplicationDAO.listApplicationIdsBySceneId(sceneId);
        copyDetailResult.getDataValidation().setExcludedApplicationIds(DataTransformUtil.list2list(excludedApplicationIds, String.class));
        copyDetailResult.setId(sceneId);
        return ResponseResult.success(copyDetailResult);
    }

    /**
     * 生成场景请求入参-请求Cloud用
     *
     * @param request 前端入参
     * @return 调用Cloud接口用的入参
     */
    public SceneRequest buildSceneRequest(NewSceneRequest request) {
        // 1. 基本信息
        SceneRequest sceneRequest = BeanUtil.copyProperties(request, SceneRequest.class);
        // 2. 线程组施压配置 （字段相同，但是由于类型不同，导致的无法拷贝属性，需要手动转换）
        {
            // 1. 构建实例
            PtConfigExt ptConfig = BeanUtil.copyProperties(request.getConfig(), PtConfigExt.class);
            // 2. 构建实例内容
            ptConfig.setThreadGroupConfigMap(new HashMap<>(0));
            // 3. 填充实例内容
            request.getConfig().getThreadGroupConfigMap().forEach((k, v) -> ptConfig.getThreadGroupConfigMap().put(k, BeanUtil.copyProperties(v, ThreadGroupConfigExt.class)));
            sceneRequest.setConfig(ptConfig);
        }
        // 3. 填充脚本解析结果
        {
            SceneResult scene = sceneService.getScene(sceneRequest.getBasicInfo().getBusinessFlowId());
            if (scene == null) {
                throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "未获取到业务流程");
            }
            if (StrUtil.isBlank(scene.getScriptJmxNode())) {
                throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "业务流程未保存脚本解析结果");
            }
            if (!scene.getLinkRelateNum().equals(scene.getTotalNodeNum())) {
                throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "业务流程尚未匹配完成");
            }
            sceneRequest.setAnalysisResult(JSONObject.parseArray(scene.getScriptJmxNode(), ScriptNode.class));
            sceneRequest.getBasicInfo().setType(0);
            sceneRequest.getBasicInfo().setScriptType(0);
            sceneRequest.getBasicInfo().setScriptId(scene.getScriptDeployId());
        }
        // 4. 填充压测内容
        {
            // 1. 获取业务流程关联的业务活动
            List<SceneLinkRelateResult> links = sceneService.getSceneLinkRelates(sceneRequest.getBasicInfo().getBusinessFlowId());
            if (CollectionUtils.isEmpty(links)) {
                throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "未获取到业务流程关联的业务活动");
            }
            // 2. 转换业务活动为压测内容
            List<SceneRequest.Content> content = links.stream().map(t -> new SceneRequest.Content() {{
                setPathMd5(t.getScriptXpathMd5());
                setBusinessActivityId(Long.valueOf(t.getBusinessLinkId()));
            }}).collect(Collectors.toList());
            // 3. 根据脚本解析结果，填充压测内容
            // 3.1. 脚本解析结果转换为一维数据
            List<ScriptNode> nodes = JmxUtil.toOneDepthList(sceneRequest.getAnalysisResult());
            if (CollectionUtils.isEmpty(nodes)) {
                throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "脚本解析结果转换为一维数据失败");
            }
            // 3.2. 一维数据转换为Map，获得xPathMD5 和 脚本节点名称的对应关系
            Map<String, String> nodeMap = nodes.stream().collect(Collectors.toMap(ScriptNode::getXpathMd5, ScriptNode::getTestName));
            // 3.3 遍历压测内容并从Map中填充数据
            for (SceneRequest.Content item : content) {
                if (!nodeMap.containsKey(item.getPathMd5())) {
                    throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "脚本解析结果存在不能匹配的业务活动");
                }
                item.setName(nodeMap.get(item.getPathMd5()));
                item.setApplicationId(sceneManageService.getAppIdsByBusinessActivityId(item.getBusinessActivityId()));
            }
            // 3.4 补充非业务活动目标到压测内容
            List<String> contentNode = content.stream().map(SceneRequest.Content::getPathMd5).distinct().collect(Collectors.toList());
            List<String> needFillNode = sceneRequest.getGoal().keySet().stream().filter(t -> !contentNode.contains(t)).distinct().collect(Collectors.toList());
            needFillNode.forEach(t -> content.add(new SceneRequest.Content() {{
                if (!nodeMap.containsKey(t)) {
                    throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "脚本解析结果存在不能匹配的非业务活动的压测目标");
                }
                setPathMd5(t);
                setName(nodeMap.get(t));
                setBusinessActivityId(0L);
                setApplicationId(new ArrayList<>(0));
            }}));
            sceneRequest.setContent(content);
        }
        // 5. 填充SLA
        {
            // 1. 销毁的监控目标
            List<SceneRequest.MonitoringGoal> destroyMonitoringGoal = request.getDestroyMonitoringGoal().stream().map(t -> BeanUtil.copyProperties(t, SceneRequest.MonitoringGoal.class)).peek(t -> t.setType(0)).collect(Collectors.toList());
            // 2. 警告的监控目标
            List<SceneRequest.MonitoringGoal> warnMonitoringGoal = request.getWarnMonitoringGoal().stream().map(t -> BeanUtil.copyProperties(t, SceneRequest.MonitoringGoal.class)).peek(t -> t.setType(1)).collect(Collectors.toList());
            // 3. 组合警告目标
            List<SceneRequest.MonitoringGoal> monitoringGoal = new ArrayList<>(destroyMonitoringGoal.size() + warnMonitoringGoal.size());
            monitoringGoal.addAll(destroyMonitoringGoal);
            monitoringGoal.addAll(warnMonitoringGoal);
            // 4. 填充
            sceneRequest.setMonitoringGoal(monitoringGoal);
        }
        // 6. 填充压测文件
        sceneRequest.setFile(assembleFileList(sceneRequest.getBasicInfo().getScriptId()));
        return sceneRequest;
    }


    /**
     * 组装场景文件列表
     *
     * @param scriptId 脚本主键
     * @return 文件列表
     */
    private List<SceneRequest.File> assembleFileList(long scriptId) {
        // 根据脚本主键获取文件主键集合
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDao.selectFileIdsByScriptDeployId(scriptId);
        // 根据文件主键集合查询文件信息
        List<Long> fileIds = scriptFileRefResults.stream().map(ScriptFileRefResult::getFileId).collect(Collectors.toList());
        // 组装返回数据
        return fileManageDao.selectFileManageByIds(fileIds).stream().map(t -> {
            Map<String, Object> extend = JSONObject.parseObject(t.getFileExtend(), new TypeReference<Map<String, Object>>() {
            });
            return new SceneRequest.File() {{
                setName(t.getFileName());
                setPath(t.getUploadPath());
                setType(t.getFileType());
                setExtend(extend);
            }};
        }).collect(Collectors.toList());
    }


    public Long fetBizFlowId(Long apiId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("api_id", apiId);
        queryWrapper.eq("is_deleted", 0);
        return performanceConfigSceneRelateShipMapper.selectOne(queryWrapper).getFlowId();
    }

    /**
     * 上传数据文件
     *
     * @param request
     * @return
     */
    public ResponseResult uploadDataFile(BusinessFlowDataFileRequest request) {
        Long bizFlowId = fetBizFlowId(request.getId());
        request.setId(bizFlowId);
        sceneService.uploadDataFile(request);
        return ResponseResult.success();

    }
}
