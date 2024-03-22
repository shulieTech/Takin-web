package io.shulie.takin.web.biz.service.scene.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.cloud.entity.dao.report.TReportMapper;
import com.pamirs.takin.common.constant.SceneManageConstant;
import com.pamirs.takin.common.constant.TimeUnitEnum;
import com.pamirs.takin.common.exception.ApiException;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.common.util.ListHelper;
import com.pamirs.takin.common.util.parse.UrlUtil;
import com.pamirs.takin.entity.domain.dto.report.ReportTraceDetailDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneBusinessActivityRefDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneScriptRefDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.ScriptCheckDTO;
import com.pamirs.takin.entity.domain.entity.scenemanage.SceneBusinessActivityRef;
import com.pamirs.takin.entity.domain.vo.scenemanage.*;
import io.shulie.takin.adapter.api.entrypoint.scene.manage.CloudSceneManageApi;
import io.shulie.takin.adapter.api.model.common.TimeBean;
import io.shulie.takin.adapter.api.model.request.scenemanage.*;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptCheckAndUpdateReq.EnginePlugin;
import io.shulie.takin.adapter.api.model.request.scenetask.SceneStartCheckResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageListResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest;
import io.shulie.takin.adapter.api.model.response.scenemanage.ScriptCheckResp;
import io.shulie.takin.adapter.api.model.response.strategy.StrategyResp;
import io.shulie.takin.adapter.api.model.response.watchman.WatchmanNode;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneService;
import io.shulie.takin.cloud.common.influxdb.InfluxUtil;
import io.shulie.takin.cloud.data.mapper.mysql.ReportBusinessActivityDetailMapper;
import io.shulie.takin.cloud.data.mapper.mysql.SceneManageMapper;
import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.ext.content.script.ScriptVerityExt.FileVerifyItem;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.amdb.api.TraceClient;
import io.shulie.takin.web.amdb.bean.query.trace.TraceStatisticsQueryReq;
import io.shulie.takin.web.amdb.bean.result.trace.EntryTraceAvgCostDTO;
import io.shulie.takin.web.biz.constant.BaseLinkProblemReasonEnum;
import io.shulie.takin.web.biz.pojo.input.scenemanage.SceneManageListOutput;
import io.shulie.takin.web.biz.pojo.output.scene.*;
import io.shulie.takin.web.biz.pojo.request.filemanage.ScriptAndActivityVerifyRequest;
import io.shulie.takin.web.biz.pojo.request.scene.BaseLineQueryReq;
import io.shulie.takin.web.biz.pojo.request.scene.ListSceneForSelectRequest;
import io.shulie.takin.web.biz.pojo.request.scene.ListSceneReportRequest;
import io.shulie.takin.web.biz.pojo.request.scene.SceneBaseLineInsertDto;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.report.ReportLinkDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.*;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneMachineResponse.SceneMachineCluster;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.PluginConfigDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.report.ReportRealTimeService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.scene.ApplicationBusinessActivityService;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.biz.service.scene.TReportBaseLinkProblemService;
import io.shulie.takin.web.biz.service.scene.TSceneBaseLineService;
import io.shulie.takin.web.biz.service.scenemanage.EngineClusterService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.service.scenemanage.SceneSchedulerTaskService;
import io.shulie.takin.web.biz.service.scenemanage.SceneTagService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.biz.utils.business.script.ScriptManageUtil;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.enums.script.FileTypeEnum;
import io.shulie.takin.web.common.enums.script.ScriptTypeEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.util.ActivityUtil.EntranceJoinEntity;
import io.shulie.takin.web.common.util.BeanCopyUtils;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.common.InfluxDatabaseWriter;
import io.shulie.takin.web.data.dao.SceneExcludedApplicationDAO;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.linkmanage.BusinessLinkManageDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigSceneRelateShipMapper;
import io.shulie.takin.web.data.mapper.mysql.TReportBaseLinkProblemMapper;
import io.shulie.takin.web.data.mapper.mysql.TSceneBaseLineMapper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigSceneRelateShipEntity;
import io.shulie.takin.web.data.model.mysql.TReportBaseLinkProblem;
import io.shulie.takin.web.data.model.mysql.TSceneBaseLine;
import io.shulie.takin.web.data.param.CreateSceneExcludedApplicationParam;
import io.shulie.takin.web.data.result.activity.ActivityResult;
import io.shulie.takin.web.data.result.linkmange.BusinessLinkResult;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import io.shulie.takin.web.ext.entity.tenant.EngineType;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author qianshui
 * @date 2020/4/17 下午3:32
 */
@Service
@Slf4j
public class SceneManageServiceImpl implements SceneManageService {
    @Resource
    private SceneTaskApi sceneTaskApi;
    @Resource
    private SceneService sceneService;
    @Resource
    private ApplicationDAO applicationDAO;
    @Resource
    private CloudSceneManageApi cloudSceneManageApi;
    @Resource
    private SceneManageApi sceneManageApi;
    @Resource
    private SceneTagService sceneTagService;
    @Resource
    private ScriptManageService scriptManageService;
    @Resource
    private BusinessLinkManageDAO businessLinkManageDAO;
    @Resource
    private SceneSchedulerTaskService sceneSchedulerTaskService;
    @Resource
    private ApplicationBusinessActivityService applicationBusinessActivityService;

    @Autowired
    private SceneExcludedApplicationDAO sceneExcludedApplicationDAO;

    @Autowired
    private InfluxDatabaseWriter influxDatabaseManager;

    @Autowired
    private SceneExcludedApplicationDAO excludedApplicationDAO;

    @Resource
    private EngineClusterService engineClusterService;

    @Resource
    private TSceneBaseLineMapper sceneBaseLineMapper;
    @Resource
    private TSceneBaseLineService baseLineService;
    @Resource
    private ReportService reportService;
    @Resource
    private CloudSceneService cloudSceneService;
    @Resource
    private ReportBusinessActivityDetailMapper detailMapper;
    @Resource
    private ReportRealTimeService reportRealTimeService;
    @Resource
    private TReportMapper tReportMapper;
    @Resource
    private TraceClient traceClient;
    @Resource
    private TReportBaseLinkProblemService reportBaseLinkProblemService;
    @Resource
    private TReportBaseLinkProblemMapper reportBaseLinkProblemMapper;
    @Resource
    private SceneManageMapper sceneManageMapper;

    @Resource
    private ActivityService activityService;

    @Resource
    private ActivityDAO activityDAO;

    @Override
    public SceneDetailResponse getById(Long sceneId) {
        ResponseResult<SceneManageWrapperResp> sceneResult = this.detailScene(sceneId);
        if (sceneResult == null) {
            return null;
        }

        SceneDetailResponse sceneDetailResponse = DataTransformUtil.copyBeanPropertiesWithNull(sceneResult.getData(),
                SceneDetailResponse.class);
        if (sceneDetailResponse == null) {
            return null;
        }

        // 查询排除的应用
        List<Long> excludedApplicationIds = excludedApplicationDAO.listApplicationIdsBySceneId(sceneId);
        sceneDetailResponse.setExcludedApplicationIds(DataTransformUtil.list2list(excludedApplicationIds, String.class));
        return sceneDetailResponse;
    }

    @Override
    public ResponseResult<List<SceneManageWrapperResp>> getByIds(SceneManageQueryByIdsReq req) {
        return sceneManageApi.getByIds(req);
    }

    @Override
    public WebResponse<List<SceneScriptRefOpen>> addScene(SceneManageWrapperVO vo) {
        /*
         * 0、校验业务活动和脚本文件是否匹配
         *    1、是否有脚本脚本文件
         *    2、Jmeter脚本文件是否唯一
         *    3、业务活动必须存在于脚本文件
         * 1、保存基本信息+施压配置
         * 2、保存业务活动
         * 3、保存脚本
         * 4、保存SLA
         */
        if (!Objects.isNull(vo.getScheduleInterval())) {
            Long scheduleInterval = convertTime(Long.parseLong(String.valueOf(vo.getScheduleInterval())),
                    TimeUnitEnum.MINUTE.getValue());
            TimeVO timeVo = vo.getPressureTestTime();
            Long pressureTestSecond = convertTime(timeVo.getTime(), timeVo.getUnit());
            if (scheduleInterval > pressureTestSecond) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "漏数验证时间间隔不能大于压测时长");
            }
        }

        SceneManageWrapperReq req = new SceneManageWrapperReq();
        WebResponse<List<SceneScriptRefOpen>> webResponse = sceneManageVo2Req(vo, req);
        try {
            ResponseResult<Long> result = sceneManageApi.saveScene(req);
            if (vo.getIsScheduler() != null && vo.getIsScheduler() && result != null
                    && result.getSuccess() && vo.getExecuteTime() != null) {
                //保存场景定时信息
                Long sceneId = JSON.parseObject(JSON.toJSONString(result.getData()), Long.class);
                SceneSchedulerTaskCreateRequest createRequest = new SceneSchedulerTaskCreateRequest();
                createRequest.setSceneId(sceneId);
                createRequest.setExecuteTime(vo.getExecuteTime());
                // 插入拦截sql
                sceneSchedulerTaskService.insert(createRequest);
            }
            webResponse.setSuccess(true);

            // 忽略检测的应用
            if (result != null && result.getSuccess()) {
                this.createSceneExcludedApplication(result.getData(), vo.getExcludedApplicationIds());
            }

        } catch (Exception e) {
            log.error("新增压测场景失败", e);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_ADD_ERROR, "新增压测场景失败！");
        }

        return webResponse;
    }

    /**
     * 场景转关联
     *
     * @param dto 更新所需要的参数
     * @param req 关联实例
     * @return 结果
     */
    private WebResponse<List<SceneScriptRefOpen>> sceneManageVo2Req(SceneManageWrapperVO dto, SceneManageWrapperReq req) {
        WebResponse<List<SceneScriptRefOpen>> webResponse = this.buildSceneManageRef(dto);
        List<SceneScriptRefOpen> data = webResponse.getData();
        if (!webResponse.getSuccess()) {
            return webResponse;
        }
        BeanUtils.copyProperties(dto, req);
        if (data != null) {
            req.setUploadFile(data);
        }
        //补充压测时间
        if (dto.getPressureTestTime() != null) {
            TimeBean presTime = new TimeBean();
            presTime.setUnit(dto.getPressureTestTime().getUnit());
            presTime.setTime(dto.getPressureTestTime().getTime());
            req.setPressureTestTime(presTime);
        }
        //补充递增时间
        if (dto.getIncreasingTime() != null) {
            TimeBean increasingTime = new TimeBean();
            increasingTime.setTime(dto.getIncreasingTime().getTime());
            increasingTime.setUnit(dto.getIncreasingTime().getUnit());
            req.setIncreasingTime(increasingTime);
            req.setIncreasingTime(increasingTime);
        }
        assembleFeatures(dto, req);
        return webResponse;
    }

    /**
     * 将特殊字段放在features
     *
     * @param vo  -
     * @param req -
     */
    public void assembleFeatures(SceneManageWrapperVO vo, SceneManageWrapperReq req) {
        Map<String, Object> map = new HashMap<>(5);
        map.put(SceneManageConstant.FEATURES_BUSINESS_FLOW_ID, vo.getBusinessFlowId());
        Integer configType = vo.getConfigType();
        map.put(SceneManageConstant.FEATURES_CONFIG_TYPE, configType);
        if (configType != null && configType == 1) {
            if (CollectionUtils.isNotEmpty(vo.getBusinessActivityConfig())) {
                map.put(SceneManageConstant.FEATURES_SCRIPT_ID, vo.getBusinessActivityConfig().get(0).getScriptId());
            }
        } else {
            map.put(SceneManageConstant.FEATURES_SCRIPT_ID, vo.getScriptId());
        }
        map.put(SceneManageConstant.FEATURES_SCHEDULE_INTERVAL, vo.getScheduleInterval());
        req.setFeatures(JSON.toJSONString(map));
    }

    @Override
    public ResponseResult<List<SceneManageListOutput>> getPageList(SceneManageQueryVO vo) {

        SceneManageQueryReq req = BeanUtil.copyProperties(vo, SceneManageQueryReq.class);
        if (vo.getTagId() != null) {
            List<Long> tagIds = Collections.singletonList(vo.getTagId());
            List<SceneTagRefResponse> sceneTagRefBySceneIds = sceneTagService.getTagRefByTagIds(tagIds);
            if (CollectionUtils.isNotEmpty(sceneTagRefBySceneIds)) {
                List<Long> sceneIds = sceneTagRefBySceneIds.stream().map(SceneTagRefResponse::getSceneId).distinct().collect(Collectors.toList());
                String sceneIdStr = StringUtils.join(sceneIds, ",");
                req.setSceneIds(sceneIdStr);
            } else {
                return ResponseResult.success(new ArrayList<>(0), 0L);
            }
        }
        ResponseResult<List<SceneManageListResp>> sceneList = sceneManageApi.getSceneList(req);
        List<SceneManageListOutput> listData = convertData(sceneList.getData());
        List<Long> sceneIds = listData.stream().map(SceneManageListOutput::getId).collect(Collectors.toList());
        //计算场景的定时执行时间
        List<SceneSchedulerTaskResponse> responseList = sceneSchedulerTaskService.selectBySceneIds(sceneIds);
        Map<Long, Date> sceneExecuteTimeMap = new HashMap<>(responseList.size());
        responseList.forEach(response -> sceneExecuteTimeMap.put(response.getSceneId(), response.getExecuteTime()));
        listData.forEach(t -> {
            Date date = sceneExecuteTimeMap.get(t.getId());
            if (date != null) {
                t.setIsScheduler(true);
                t.setScheduleExecuteTime(DateUtil.formatDateTime(date));
            }

            //查询是否关联单接口压测
            QueryWrapper queryWrapper = new QueryWrapper();
            Long sceneId = t.getId();
            queryWrapper.eq("scene_id", sceneId);
            queryWrapper.eq("is_deleted", 0);
            InterfacePerformanceConfigSceneRelateShipEntity entity =
                    performanceConfigSceneRelateShipMapper.selectOne(queryWrapper);
            if (Objects.nonNull(entity)) {
                t.setConfigId(entity.getApiId());
            } else {
                // nothing
            }

        });

        if (Objects.nonNull(vo.getSource()) && vo.getSource() == 1) {
            listData = listData.stream().filter(t -> Objects.isNull(t.getConfigId())).collect(Collectors.toList());
        }
        if (Objects.nonNull(vo.getSource()) && vo.getSource() == 2 && Objects.isNull(vo.getConfigId())) {
            listData = listData.stream().filter(t -> Objects.nonNull(t.getConfigId())).collect(Collectors.toList());
        }
        if (Objects.nonNull(vo.getSource()) && vo.getSource() == 2 && Objects.nonNull(vo.getConfigId())) {
            listData = listData.stream().filter(t -> Objects.nonNull(t.getConfigId()) && vo.getConfigId().equals(t.getConfigId()))
                    .collect(Collectors.toList());
        }
        if (Objects.nonNull(vo.getSource()) && vo.getSource() == 1 && Objects.nonNull(vo.getConfigId())) {
            listData = new ArrayList<>();
        }
        return ResponseResult.success(listData, sceneList.getTotalNum());
    }

    @Resource
    InterfacePerformanceConfigSceneRelateShipMapper performanceConfigSceneRelateShipMapper;

    /**
     * 转换bean
     *
     * @return -
     */
    private List<SceneManageListOutput> convertData(List<SceneManageListResp> data) {
        if (CollectionUtils.isNotEmpty(data)) {
            //获取场景标签数组
            List<TagManageResponse> allSceneTags = sceneTagService.getAllSceneTags();
            Map<Long, TagManageResponse> tagMap = new HashMap<>(allSceneTags.size());
            if (CollectionUtils.isNotEmpty(allSceneTags)) {
                allSceneTags.forEach(tagManageResponse -> tagMap.put(tagManageResponse.getId(), tagManageResponse));
            }
            List<Long> sceneIds = data.stream().map(SceneManageListResp::getId).collect(Collectors.toList());
            List<SceneTagRefResponse> sceneTagRefList = sceneTagService.getSceneTagRefBySceneIds(sceneIds);
            Map<Long, List<SceneTagRefResponse>> sceneMap = sceneTagRefList.stream().collect(
                    Collectors.groupingBy(SceneTagRefResponse::getSceneId));

            return data.stream().map(resp -> {
                SceneManageListOutput output = new SceneManageListOutput();
                BeanUtils.copyProperties(resp, output);
                // 补充数据
                WebPluginUtils.fillQueryResponse(output);

                //组装标签关联关系
                List<TagManageResponse> tags = new ArrayList<>();
                if (sceneMap.containsKey(resp.getId())) {
                    List<SceneTagRefResponse> refTagList = sceneMap.get(resp.getId());
                    for (SceneTagRefResponse response : refTagList) {
                        Long tagId = response.getTagId();
                        if (tagMap.containsKey(tagId)) {
                            tags.add(tagMap.get(tagId));
                        }
                    }
                }
                output.setTag(tags);
                return output;
            }).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    @Override
    public ResponseResult<StrategyResp> getIpNum(Integer concurrenceNum, Integer tpsNum) {
        SceneIpNumReq req = new SceneIpNumReq();
        req.setConcurrenceNum(concurrenceNum);
        req.setTpsNum(tpsNum);
        return sceneManageApi.getIpNum(req);
    }

    /**
     * 场景关联
     *
     * @param dto 更新场景的入参
     * @return 结果
     */
    private WebResponse<List<SceneScriptRefOpen>> buildSceneManageRef(SceneManageWrapperVO dto) {
        // 文件脚本id，
        Long scriptId = 0L;
        boolean hasScriptId = false;
        if (dto.getConfigType() != null && dto.getConfigType() == 2) {
            scriptId = dto.getScriptId();
            hasScriptId = true;
        } else {
            // 业务活动类型的关联业务活动只能有一个
            List<SceneBusinessActivityRefVO> list = dto.getBusinessActivityConfig();
            if (CollectionUtils.isNotEmpty(list) && list.size() == 1) {
                scriptId = list.get(0).getScriptId();
                hasScriptId = true;
            }
        }
        if (!hasScriptId) {
            WebResponse.fail("scriptId can not be null !");
        }

        // 根据scriptId获取脚本信息
        ScriptManageDeployDetailResponse scriptManageDeployDetail = scriptManageService.getScriptManageDeployDetail(
                scriptId);
        if (scriptManageDeployDetail == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "脚本实例不存在!");
        }
        // 关联业务流程, 查出关联的业务流程
        if (ScriptManageUtil.deployRefBusinessFlowType(scriptManageDeployDetail.getRefType())) {
            // 业务流程id
            Long businessFlowId = Long.valueOf(scriptManageDeployDetail.getRefValue());
            SceneResult scene = sceneService.getScene(businessFlowId);
            if (null != scene) {
                dto.setScriptAnalysisResult(scene.getScriptJmxNode());
            }
        }

        // 设置脚本类型
        dto.setScriptType(scriptManageDeployDetail.getType());
        // 获得脚本列表
        List<SceneScriptRefOpen> scriptList = ScriptManageUtil.buildScriptRef(scriptManageDeployDetail);
        WebResponse<List<SceneScriptRefOpen>> response = this.checkScript(scriptManageDeployDetail.getType(), scriptList);
        if (!response.getSuccess()) {
            return response;
        }
        List<SceneScriptRefOpen> execList = response.getData();

        List<SceneBusinessActivityRef> businessActivityList =
                this.buildSceneBusinessActivityRef(dto.getBusinessActivityConfig());

        // 在上传文件时已经校验脚本和业务活动的关联关系，此处不再校验
        if (ScriptTypeEnum.JMETER.getCode().equals(dto.getScriptType())) {
            ScriptAndActivityVerifyRequest param = ScriptAndActivityVerifyRequest.builder()
                    .isPressure(dto.isPressure()).sceneId(dto.getId()).sceneName(dto.getPressureTestSceneName())
                    .scriptType(dto.getScriptType()).scriptList(execList).watchmanIdList(dto.getWatchmanIdList())
                    .absolutePath(true).businessActivityList(businessActivityList)
                    .deployDetail(scriptManageDeployDetail).version(scriptManageDeployDetail.getScriptVersion()).build();
            ScriptCheckDTO checkDTO = this.checkScriptAndActivity(param);
            if (StringUtils.isNoneBlank(checkDTO.getErrmsg())) {
                throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, checkDTO.getErrmsg());
            }
        }

        //填充绑定关系
        Map<Long, SceneBusinessActivityRef> dataMap = ListHelper.transferToMap(businessActivityList,
                SceneBusinessActivityRef::getBusinessActivityId, data -> data);
        dto.getBusinessActivityConfig().forEach(vo -> {
            SceneBusinessActivityRef ref = dataMap.get(vo.getBusinessActivityId());
            if (ref != null) {
                vo.setBindRef(ref.getBindRef());
                vo.setApplicationIds(ref.getApplicationIds());
            }
        });
        WebResponse<List<SceneScriptRefOpen>> webResponse = new WebResponse<>();
        webResponse.setSuccess(true);
        webResponse.setData(scriptList);
        return webResponse;
    }

    @Override
    public WebResponse<String> updateScene(SceneManageWrapperVO dto) {
        if (Objects.nonNull(dto.getScheduleInterval())) {
            // 间隔转换为分钟
            Long scheduleInterval = this.convertTime(dto.getScheduleInterval().longValue(),
                    TimeUnitEnum.MINUTE.getValue());
            TimeVO timeVo = dto.getPressureTestTime();
            Long pressureTestSecond = this.convertTime(timeVo.getTime(), timeVo.getUnit());
            if (scheduleInterval > pressureTestSecond) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "漏数验证时间间隔不能大于压测时长!");
            }
        }

        //处理定时任务
        if (dto.getIsScheduler() == null || !dto.getIsScheduler()) {
            sceneSchedulerTaskService.deleteBySceneId(dto.getId());
        } else {
            SceneSchedulerTaskResponse dbData = sceneSchedulerTaskService.selectBySceneId(dto.getId());
            if (dto.getExecuteTime() != null && dbData != null) {
                Date executeTime = dbData.getExecuteTime();
                if (dto.getExecuteTime().compareTo(executeTime) != 0) {
                    SceneSchedulerTaskUpdateRequest updateParam = new SceneSchedulerTaskUpdateRequest();
                    updateParam.setId(dbData.getId());
                    updateParam.setExecuteTime(dto.getExecuteTime());
                    sceneSchedulerTaskService.update(updateParam, true);
                }
            } else if (dbData == null) {
                SceneSchedulerTaskCreateRequest createRequest = new SceneSchedulerTaskCreateRequest();
                createRequest.setSceneId(dto.getId());
                createRequest.setExecuteTime(dto.getExecuteTime());
                createRequest.setUserId(WebPluginUtils.traceUser().getId());
                sceneSchedulerTaskService.insert(createRequest);
            }
        }

        SceneManageWrapperReq req = new SceneManageWrapperReq();
        this.sceneManageVo2Req(dto, req);
        dto.setUploadFile(this.sceneScriptRefOpenToVoList(req.getUploadFile()));
        // 调用Cloud
        ResponseResult<String> cloudResult = sceneManageApi.updateScene(req);
        if (cloudResult.getError() != null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR, "takin-cloud返回错误，" + cloudResult.getError().getMsg());
        }
        WebResponse<String> webResponse = new WebResponse<>();
        webResponse.setData(cloudResult.getData());
        webResponse.setSuccess(cloudResult.getSuccess());
        webResponse.setTotal(cloudResult.getTotalNum());

        // 先删除
        sceneExcludedApplicationDAO.removeBySceneId(dto.getId());
        // 排除应用更新
        this.createSceneExcludedApplication(dto.getId(), dto.getExcludedApplicationIds());
        return webResponse;
    }

    public SceneScriptRefVO sceneScriptRefOpenToVo(SceneScriptRefOpen open) {
        SceneScriptRefVO vo = new SceneScriptRefVO();
        BeanUtils.copyProperties(open, vo);
        return vo;
    }

    public List<SceneScriptRefVO> sceneScriptRefOpenToVoList(List<SceneScriptRefOpen> source) {
        List<SceneScriptRefVO> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(source)) {
            source.forEach(open -> list.add(sceneScriptRefOpenToVo(open)));
        }
        return list;
    }

    @Override
    public String deleteScene(SceneManageDeleteReq vo) {
        return cloudSceneManageApi.deleteScene(vo);
    }

    @Override
    public ResponseResult<SceneManageWrapperResp> detailScene(Long id) {
        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(id);
        // cloud 获得场景详情
        ResponseResult<SceneManageWrapperResp> sceneDetail = sceneManageApi.getSceneDetail(req);

        if (Objects.isNull(sceneDetail) || Objects.isNull(sceneDetail.getData())) {
            if (sceneDetail.getError().getMsg().contains("19800-T0103")) {
                throw new TakinWebException(TakinWebExceptionEnum.DATA_SIGN_ERROR, "数据签名异常,请联系管理员!");
            }
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "该压测场景不存在!");
        }
        if (!sceneDetail.getSuccess()) {
            ResponseResult.ErrorInfo error = sceneDetail.getError();
            String errorMsg = Objects.isNull(error) ? "" : error.getMsg();
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR, "takin-cloud返回的详情信息有误！原因：" + errorMsg);
        }

        SceneManageWrapperResp data = sceneDetail.getData();
        //组装场景定时时间
        SceneSchedulerTaskResponse sceneScheduler = sceneSchedulerTaskService.selectBySceneId(id);
        if (sceneScheduler != null && !sceneScheduler.getIsDeleted()) {
            Date executeTime = sceneScheduler.getExecuteTime();
            if (executeTime != null) {
                data.setIsScheduler(true);
                data.setExecuteTime(DateUtils.dateToString(executeTime, DateUtils.FORMATE_YMDHMS));
            }
        } else {
            data.setIsScheduler(false);
        }
        sceneDetail.setData(data);

        return sceneDetail;
    }

    @Override
    public ScriptCheckDTO checkBusinessActivityAndScript(SceneManageWrapperDTO sceneData) {
        int scriptType = sceneData.getScriptType();

        List<SceneScriptRefOpen> scriptList = Lists.newArrayList();
        sceneData.getUploadFile().forEach(data -> scriptList.add(buildSceneScriptRef(data)));

        ScriptCheckDTO dto = new ScriptCheckDTO();
        try {
            WebResponse<List<SceneScriptRefOpen>> response = checkScript(scriptType, scriptList);
            if (!response.getSuccess()) {
                dto.setErrmsg(response.getError().getMsg());
                return dto;
            }
            List<SceneScriptRefOpen> execList = response.getData();
            List<SceneBusinessActivityRef> businessActivityList = Lists.newArrayList();
            sceneData.getBusinessActivityConfig().forEach(
                    data -> businessActivityList.add(buildSceneBusinessActivityRef(data)));
            if (ScriptTypeEnum.JMETER.getCode().equals(scriptType)) {
                ScriptAndActivityVerifyRequest param = ScriptAndActivityVerifyRequest.builder()
                        .isPressure(true).scriptType(scriptType).scriptList(execList)
                        .sceneId(sceneData.getId()).sceneName(sceneData.getPressureTestSceneName())
                        .absolutePath(sceneData.getIsAbsoluteScriptPath())
                        .businessActivityList(businessActivityList).watchmanIdList(sceneData.getWatchmanIdList())
                        .deployDetail(scriptManageService.getScriptManageDeployDetail(sceneData.getScriptId())).build();
                dto = checkScriptAndActivity(param);
            }
        } catch (ApiException apiException) {
            dto.setMatchActivity(false);
            dto.setPtTag(false);
        }
        return dto;
    }

    private SceneBusinessActivityRef buildSceneBusinessActivityRef(SceneBusinessActivityRefDTO activityDTO) {
        SceneBusinessActivityRef activityRef = new SceneBusinessActivityRef();
        activityRef.setBusinessActivityId(activityDTO.getBusinessActivityId());
        activityRef.setBusinessActivityName(activityDTO.getBusinessActivityName());
        return activityRef;
    }

    private SceneScriptRefOpen buildSceneScriptRef(SceneScriptRefDTO scriptDTO) {
        SceneScriptRefOpen scriptRef = new SceneScriptRefOpen();
        scriptRef.setFileType(scriptDTO.getFileType());
        scriptRef.setIsDeleted(scriptDTO.getIsDeleted());
        scriptRef.setId(scriptDTO.getId());
        scriptRef.setUploadPath(scriptDTO.getUploadPath());
        return scriptRef;
    }

    /**
     * 通过业务活动id关联出应用id列表
     *
     * @param businessActivityId 业务活动id
     * @return 应用id列表
     */
    @Override
    public List<String> getAppIdsByBusinessActivityId(Long businessActivityId) {
        return getAppIdsByNameAndUser(getAppsById(businessActivityId));
    }

    /**
     * 业务活动列表 转换
     *
     * @param voList 业务活动
     * @return 业务活动列表
     */
    private List<SceneBusinessActivityRef> buildSceneBusinessActivityRef(List<SceneBusinessActivityRefVO> voList) {
        List<SceneBusinessActivityRef> businessActivityList = Lists.newArrayList();
        voList.forEach(data -> {
            SceneBusinessActivityRef ref = new SceneBusinessActivityRef();
            ref.setBusinessActivityId(data.getBusinessActivityId());
            ref.setBusinessActivityName(data.getBusinessActivityName());
            List<String> ids = getAppIdsByNameAndUser(getAppsById(data.getBusinessActivityId()));
            ref.setApplicationIds(convertListToString(ids));
            ref.setGoalValue(buildGoalValue(data));
            businessActivityList.add(ref);
        });
        return businessActivityList;
    }

    private String buildGoalValue(SceneBusinessActivityRefVO vo) {
        Map<String, Object> map = Maps.newHashMap();
        map.put(SceneManageConstant.TPS, vo.getTargetTPS());
        map.put(SceneManageConstant.RT, vo.getTargetRT());
        map.put(SceneManageConstant.SUCCESS_RATE, vo.getTargetSuccessRate());
        map.put(SceneManageConstant.SA, vo.getTargetSA());
        return JSON.toJSONString(map);
    }

    /**
     * 检查脚本和活动
     *
     * @param verifyParam 校验请求
     * @return dto
     */
    private ScriptCheckDTO checkScriptAndActivity(ScriptAndActivityVerifyRequest verifyParam) {
        Integer scriptType = verifyParam.getScriptType();
        ScriptCheckDTO dto = new ScriptCheckDTO();
        if (scriptType == null) {
            return new ScriptCheckDTO(false, false, "无脚本文件");
        }
        if (scriptType == 1) {
            return new ScriptCheckDTO();
        }

        if (!ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.TAKIN_SCRIPT_CHECK)) {
            return new ScriptCheckDTO();
        }

        List<SceneBusinessActivityRef> businessActivityList = verifyParam.getBusinessActivityList();

        ScriptCheckAndUpdateReq request = buildVerifyRequest(verifyParam);
        List<String> requestUrl = request.getRequest();

        List<Long> businessActivityIds = businessActivityList.stream().map(SceneBusinessActivityRef::getBusinessActivityId).distinct().collect(
                Collectors.toList());

        // 用户获取业务活动类型
        List<BusinessLinkResult> results = businessLinkManageDAO.getListByIds(businessActivityIds);
        Map<Long, List<BusinessLinkResult>> businessLinkMap = results.stream().collect(Collectors.groupingBy(BusinessLinkResult::getLinkId));

        businessActivityList.forEach(data -> {
            List<BusinessLinkResult> businessLinkResults = businessLinkMap.get(data.getBusinessActivityId());
            if (CollectionUtils.isEmpty(businessLinkResults)) {
                return;
            }
            BusinessLinkResult businessLinkResult = businessLinkResults.get(0);
            EntranceJoinEntity entranceJoinEntity;
            if (ActivityUtil.isNormalBusiness(businessLinkResult.getType())) {
                entranceJoinEntity = ActivityUtil.covertEntrance(businessLinkResult.getEntrace());
            } else {
                entranceJoinEntity = ActivityUtil.covertVirtualEntrance(businessLinkResult.getEntrace());
            }

            String convertUrl = UrlUtil.convertUrl(entranceJoinEntity);
            data.setBindRef(businessLinkResult.getLinkName());
            requestUrl.add(convertUrl);
        });
        ResponseResult<ScriptCheckResp> scriptCheckResp = sceneManageApi.checkAndUpdateScript(request);
        if (scriptCheckResp == null || !scriptCheckResp.getSuccess()) {
            log.error("cloud检查并更新脚本出错：{}", request.getUploadPath());
            dto.setErrmsg(String.format("|cloud检查并更新【%s】脚本异常,异常内容【%s】", request.getUploadPath(), scriptCheckResp.getError().getMsg()));
            return dto;
        }

        if (!scriptCheckResp.getSuccess()) {
            dto.setErrmsg(String.format("cloud检查并更新【%s】脚本异常,异常内容【%s】", request.getUploadPath(), scriptCheckResp.getError().getMsg()));
            return dto;
        }

        if (scriptCheckResp.getData() != null && CollectionUtils.isNotEmpty(scriptCheckResp.getData().getErrorMsg())) {
            dto.setErrmsg(StringUtils.join(scriptCheckResp.getData().getErrorMsg(), "|"));
        }

        return dto;
    }

    /**
     * 校验脚本
     *
     * @return -
     */
    private WebResponse<List<SceneScriptRefOpen>> checkScript(Integer scriptType, List<SceneScriptRefOpen> scriptList) {
        if (CollectionUtils.isEmpty(scriptList)) {
            return WebResponse.fail("500", "找不到脚本文件");
        }

        // 可执行的脚本
        List<SceneScriptRefOpen> execList = scriptList.stream()
                .filter(data -> data.getFileType() != null && FileTypeEnum.SCRIPT.getCode().equals(data.getFileType())
                        && data.getIsDeleted() != null && data.getIsDeleted() == 0)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(execList)) {
            return WebResponse.fail("500", "找不到脚本文件");
        }

        if (0 == scriptType && execList.size() > 1) {
            return WebResponse.fail("500", "JMeter类型的脚本文件只允许有一个");
        }

        return WebResponse.success(execList);
    }

    private List<String> getAppIdsByNameAndUser(List<String> nameList) {
        if (CollectionUtils.isEmpty(nameList)) {
            return new ArrayList<>(0);
        }
        return applicationDAO.queryIdsByNameAndTenant(nameList, WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode());
    }

    private List<String> getAppsById(Long id) {
        return applicationBusinessActivityService.processAppNameByBusinessActiveId(id);
    }

    private String convertListToString(List<String> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String data : dataList) {
            sb.append(data);
            sb.append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    /**
     * 秒转化为时、分、秒
     *
     * @param time 时间
     * @param unit 单位
     * @return 对应的 时分秒
     */
    private Long convertTime(Long time, String unit) {
        if (time == null) {
            return 0L;
        }

        long t;
        if (TimeUnitEnum.HOUR.getValue().equals(unit)) {
            t = time * 60 * 60;
        } else if (TimeUnitEnum.MINUTE.getValue().equals(unit)) {
            t = time * 60;
        } else {
            t = time;
        }
        return t;
    }

    @Override
    public void checkParam(SceneManageWrapperVO sceneVO) {
        List<SceneBusinessActivityRefVO> activityRefVOList = sceneVO.getBusinessActivityConfig();
        if (CollectionUtils.isEmpty(activityRefVOList)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "业务活动配置不能为空");
        }
        for (SceneBusinessActivityRefVO sceneBusinessActivityRefVO : activityRefVOList) {
            if (Objects.isNull(sceneBusinessActivityRefVO.getTargetTPS())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "目标TPS不能为空");
            }
            if (Objects.isNull(sceneBusinessActivityRefVO.getTargetRT())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "目标RT不能为空");
            }
            if (Objects.isNull(sceneBusinessActivityRefVO.getTargetSuccessRate())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "目标成功率不能为空");
            }
            if (Objects.isNull(sceneBusinessActivityRefVO.getTargetSA())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "目标SA不能为空");
            }
            if (sceneVO.getPressureType().equals(0) && sceneVO.getConcurrenceNum() < sceneVO.getIpNum()) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "最大并发数不能小于IP数");
            }
        }
    }

    @Override
    public WebResponse<List<SceneScriptRefOpen>> buildSceneForFlowVerify(SceneManageWrapperVO vo, SceneManageWrapperReq req, Long userId) {
        vo.setPressure(true);
        // 流量验证和巡检会调用此处，在此处默认选择一个集群
        WatchmanClusterResponse engine = engineClusterService.selectOne();
        String machineId = engine.getId();
        vo.setWatchmanIdList(Collections.singletonList(machineId));
        req.setMachineId(machineId);
        req.setEngineType(engine.getType());
        return sceneManageVo2Req(vo, req);
    }

    @Override
    public ResponseResult<List<ScenePositionPointResponse>> getPositionPoint(Long sceneId) {
        List<ScenePositionPointResponse> list = Lists.newArrayList();
        SceneStartPreCheckReq checkReq = new SceneStartPreCheckReq();
        checkReq.setSceneId(sceneId);
        ResponseResult<SceneStartCheckResp> result = sceneTaskApi.sceneStartPreCheck(checkReq);
        if (Objects.isNull(result)) {
            return ResponseResult.success(list);
        }
        if (!result.getSuccess()) {
            ResponseResult.ErrorInfo error = result.getError();
            if (error != null) {
                throw new TakinWebException(ExceptionCode.SCENE_CHECK_ERROR, "cloud查询位点返回错误！" + error.getMsg());
            }
        }
        SceneStartCheckResp resultData = result.getData();
        if (Objects.isNull(resultData)) {
            return ResponseResult.success(list);
        }
        List<SceneStartCheckResp.FileReadInfo> infos = resultData.getFileReadInfos();
        if (Objects.nonNull(infos)) {
            infos.forEach(t -> {
                ScenePositionPointResponse response = new ScenePositionPointResponse();
                response.setScriptName(t.getFileName());
                response.setScriptSize(t.getFileSize());
                response.setPressedSize(t.getReadSize());
                list.add(response);
            });
        }
        return ResponseResult.success(list);
    }

    /**
     * 创建场对应的排除应用
     *
     * @param excludedApplicationIds 排除的应用ids
     * @param sceneId                场景id
     */
    @Override
    public void createSceneExcludedApplication(Long sceneId, List<Long> excludedApplicationIds) {
        if (CollectionUtil.isEmpty(excludedApplicationIds)) {
            return;
        }

        List<CreateSceneExcludedApplicationParam> createSceneExcludedApplicationParams =
                excludedApplicationIds.stream().map(excludedApplicationId -> {
                    CreateSceneExcludedApplicationParam createSceneExcludedApplicationParam
                            = new CreateSceneExcludedApplicationParam();
                    createSceneExcludedApplicationParam.setSceneId(sceneId);
                    createSceneExcludedApplicationParam.setApplicationId(excludedApplicationId);
                    createSceneExcludedApplicationParam.setTenantId(WebPluginUtils.traceTenantId());
                    createSceneExcludedApplicationParam.setEnvCode(WebPluginUtils.traceEnvCode());
                    return createSceneExcludedApplicationParam;
                }).collect(Collectors.toList());

        // 再插入
        if (!sceneExcludedApplicationDAO.saveBatch(createSceneExcludedApplicationParams)) {
            throw ApiException.create(AppConstants.RESPONSE_CODE_FAIL, "排除的应用保存失败!");
        }
    }


    @Override
    public String recoveryScene(SceneManageDeleteReq vo) {
        return cloudSceneManageApi.recovery(vo);
    }

    @Override
    public String archive(SceneManageDeleteReq vo) {
        return cloudSceneManageApi.archive(vo);
    }

    @Override
    public List<SceneListForSelectOutput> listForSelect(ListSceneForSelectRequest request) {
        // 查询cloud, 场景列表, 状态是压测中
        SceneManageQueryReq sceneManageQueryReq = new SceneManageQueryReq();
        sceneManageQueryReq.setStatus(request.getStatus());
        ResponseResult<List<SceneManageListResp>> cloudResult = sceneManageApi.querySceneByStatus(sceneManageQueryReq);
        if (cloudResult == null || !cloudResult.getSuccess() || CollectionUtil.isEmpty(cloudResult.getData())) {
            return Collections.emptyList();
        }

        return cloudResult.getData().stream().map(result -> {
            SceneListForSelectOutput sceneListForSelectOutput = new SceneListForSelectOutput();
            sceneListForSelectOutput.setId(result.getId());
            sceneListForSelectOutput.setName(result.getSceneName());
            return sceneListForSelectOutput;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SceneReportListOutput> listReportBySceneIds(ListSceneReportRequest request) {
        // 查询cloud, 获得查询tps所需要的条件
        List<ReportActivityResp> queryTpsParamList = this.listQueryTpsParam(request.getSceneIds());
        if (queryTpsParamList.isEmpty()) {
            return Collections.emptyList();
        }

        return queryTpsParamList.stream().map(queryTpsParam -> {
                    // 通过条件, 查询influxdb, 获取到tps, sql语句
                    String influxDbSql = String.format("SELECT time as datetime, ((count - fail_count) / 5) AS tps "
                                    + "FROM %s WHERE time <= NOW() AND time >= (NOW() - %dm) AND transaction = '%s' ORDER BY time",
                            this.getTableName(queryTpsParam), request.getInterval(),
                            queryTpsParam.getBusinessActivityList().get(0).getBindRef());

                    List<SceneReportListOutput> reportList = influxDatabaseManager.query(influxDbSql, SceneReportListOutput.class, "jmeter");

                    if (CollectionUtil.isEmpty(reportList)) {
                        return null;
                    }

                    // 时间戳处理
                    for (SceneReportListOutput report : reportList) {
                        report.setSceneId(queryTpsParam.getSceneId());
                        report.setSceneName(queryTpsParam.getSceneName());
                        report.setTime(LocalDateTimeUtil.format(report.getDatetime(), "HH:mm:ss"));
                    }

                    return reportList;
                }).filter(Objects::nonNull).flatMap(Collection::stream)
                .sorted(Comparator.comparing(SceneReportListOutput::getTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<SceneReportListOutput> rankReport(ListSceneReportRequest request) {
        // 查询cloud, 获得查询tps所需要的条件
        List<ReportActivityResp> queryTpsParamList = this.listQueryTpsParam(request.getSceneIds());
        if (queryTpsParamList.isEmpty()) {
            return Collections.emptyList();
        }

        return queryTpsParamList.stream().map(queryTpsParam -> {
                    List<SceneReportListOutput> reportList = this.listReportFromInfluxDb(queryTpsParam);
                    if (CollectionUtil.isEmpty(reportList) || reportList.get(0) == null) {
                        return null;
                    }

                    SceneReportListOutput sceneReportListOutput = reportList.get(0);
                    sceneReportListOutput.setSceneId(queryTpsParam.getSceneId());
                    sceneReportListOutput.setSceneName(queryTpsParam.getSceneName());
                    return sceneReportListOutput;
                }).filter(Objects::nonNull)
                .sorted(Comparator.comparing(SceneReportListOutput::getTps).reversed())
                .limit(10).collect(Collectors.toList());
    }

    // 获取集群信息
    @Override
    public SceneMachineResponse machineClusters(String id, Integer type) {
        List<WatchmanClusterResponse> clusters = engineClusterService.clusters();
        List<SceneMachineCluster> machines = clusters.stream().map(cluster -> {
            WatchmanNode node = cluster.getResource();
            return SceneMachineCluster.builder()
                    .id(cluster.getId()).name(node.getName())
                    .type(String.valueOf(cluster.getType().getType())).disabled(cluster.isDisable())
                    .cpu(node.getCpu()).memory(node.getMemory()).build();
        }).collect(Collectors.toList());

        WatchmanClusterResponse lastExecExtract = engineClusterService.extractLastExecExtract(Long.valueOf(id), type);
        EngineType engineType = lastExecExtract.getType();
        return SceneMachineResponse.builder()
                .lastStartMachineId(lastExecExtract.getId())
                .lastStartMachineType(Objects.isNull(engineType) ? null : engineType.getType())
                .list(machines).build();
    }

    /**
     * 根据场景ids获取查询tps的参数
     *
     * @param sceneIds 场景ids
     * @return 查询参数
     */
    private List<ReportActivityResp> listQueryTpsParam(List<Long> sceneIds) {
        ReportDetailByIdsReq reportDetailByIdsReq = new ReportDetailByIdsReq();
        reportDetailByIdsReq.setSceneIds(sceneIds);
        ResponseResult<List<ReportActivityResp>> cloudResult = sceneTaskApi.listQueryTpsParam(reportDetailByIdsReq);
        if (cloudResult == null || !cloudResult.getSuccess() || CollectionUtil.isEmpty(cloudResult.getData())) {
            return Collections.emptyList();
        }

        return cloudResult.getData();
    }

    /**
     * 压测报告的influxdb tableName
     *
     * @param queryTpsParam 参数
     * @return 表名
     */
    private String getTableName(ReportActivityResp queryTpsParam) {
        if (queryTpsParam.getJobId() != null) {
            return InfluxUtil.getMeasurement("pressure", queryTpsParam.getJobId());
        }
        return String.format("pressure_%d_%d_%d", queryTpsParam.getSceneId(), queryTpsParam.getReportId(),
                WebPluginUtils.getCustomerId());
    }

    /**
     * influxdb查询报告tps
     *
     * @param queryTpsParam 请求参数
     * @return 报告列表
     */
    private List<SceneReportListOutput> listReportFromInfluxDb(ReportActivityResp queryTpsParam) {
        // 通过条件, 查询influxdb, 获取到tps, sql语句
        String influxDbSql = String.format("SELECT time AS datetime, max(tps) AS tps FROM "
                        + "(SELECT ((count - fail_count) / 5) AS tps FROM %s WHERE transaction = '%s' ORDER BY time)",
                this.getTableName(queryTpsParam), queryTpsParam.getBusinessActivityList().get(0).getBindRef());
        List<SceneReportListOutput> reportList = influxDatabaseManager.query(influxDbSql, SceneReportListOutput.class, "jmeter");
        if (CollectionUtil.isEmpty(reportList)) {
            return Collections.emptyList();
        }

        return reportList;
    }

    private ScriptCheckAndUpdateReq buildVerifyRequest(ScriptAndActivityVerifyRequest verifyParam) {
        ScriptCheckAndUpdateReq checkReq = new ScriptCheckAndUpdateReq();
        checkReq.setPressure(verifyParam.isPressure());
        checkReq.setAbsolutePath(verifyParam.isAbsolutePath());
        checkReq.setUploadPath(verifyParam.getScriptList().get(0).getUploadPath());
        checkReq.setSceneId(verifyParam.getSceneId());
        checkReq.setSceneName(verifyParam.getSceneName());
        checkReq.setRequest(new ArrayList<>());
        parseDeployIfNecessary(verifyParam, checkReq);
        Integer version = verifyParam.getVersion();
        if (version != null) {
            checkReq.setVersion(version);
        }
        return checkReq;
    }

    private void parseDeployIfNecessary(ScriptAndActivityVerifyRequest verifyParam, ScriptCheckAndUpdateReq req) {
        if (req.isPressure()) {
            req.getWatchmanIdList().addAll(verifyParam.getWatchmanIdList()); // 设置集群
            ScriptManageDeployDetailResponse deployDetail = verifyParam.getDeployDetail();
            if (Objects.nonNull(deployDetail)) {
                List<PluginConfigDetailResponse> pluginFiles = deployDetail.getPluginConfigDetailResponseList();
                if (CollectionUtils.isNotEmpty(pluginFiles)) {
                    List<EnginePlugin> refOutputList = pluginFiles.stream()
                            .map(plugin -> EnginePlugin.of(Long.valueOf(plugin.getId()), plugin.getVersion()))
                            .collect(Collectors.toList());
                    req.setPlugins(refOutputList);
                }
                if (Objects.isNull(verifyParam.getSceneId())) {
                    List<FileManageResponse> dataFiles = deployDetail.getFileManageResponseList();
                    if (CollectionUtils.isNotEmpty(dataFiles)) {
                        Map<Integer, List<FileManageResponse>> dataFilesMap = dataFiles.stream()
                                .collect(Collectors.groupingBy(FileManageResponse::getFileType, Collectors.toList()));
                        List<FileManageResponse> scriptFile = dataFilesMap.get(FileTypeEnum.SCRIPT.getCode());
                        if (CollectionUtils.isNotEmpty(scriptFile)) {
                            FileManageResponse script = scriptFile.get(0);
                            req.setScriptPath(new FileVerifyItem(script.getUploadPath(), script.getMd5()));
                        }
                        List<FileManageResponse> csvFiles = dataFilesMap.get(FileTypeEnum.DATA.getCode());
                        if (CollectionUtils.isNotEmpty(csvFiles)) {
                            req.setCsvPaths(csvFiles.stream()
                                    .map(csv -> new FileVerifyItem(csv.getUploadPath(), csv.getMd5(), Objects.equals(csv.getIsBigFile(), 1)))
                                    .collect(Collectors.toList()));
                        }
                    }
                    List<FileManageResponse> attachmentFiles = deployDetail.getAttachmentManageResponseList();
                    if (CollectionUtils.isNotEmpty(attachmentFiles)) {
                        req.setAttachments(attachmentFiles.stream()
                                .map(attachment -> new FileVerifyItem(attachment.getUploadPath(), attachment.getMd5()))
                                .collect(Collectors.toList()));
                    }
                }
            }
        }
    }

    /**
     * 获取性能基线指标列表
     *
     * @param sceneId
     * @return
     */
    @Override
    public List<SceneBaseLineOutput> getPerformanceLineResultList(long sceneId) {
        try {
            BaseLineQueryReq baseLineQueryReq = getBaseLineQueryReq(sceneId);
            log.info("getPerformanceLineResultList param={}", JSON.toJSONString(baseLineQueryReq));
            if (Objects.isNull(baseLineQueryReq)) {
                return Collections.emptyList();
            }
            LambdaQueryWrapper<TSceneBaseLine> baseLineLambdaQueryWrapper = new LambdaQueryWrapper<>();
            baseLineLambdaQueryWrapper.eq(TSceneBaseLine::getSceneId, baseLineQueryReq.getSceneId());
            if (baseLineQueryReq.getLineTypeEnum() == SceneBaseLineTypeEnum.REPORT.getType()) {
                baseLineLambdaQueryWrapper.eq(TSceneBaseLine::getReportId, baseLineQueryReq.getReportId());
            }

            Timestamp start = new Timestamp(baseLineQueryReq.getBaseLineStartTime());
            Timestamp end = new Timestamp(baseLineQueryReq.getBaseLineEndTime());

            baseLineLambdaQueryWrapper.eq(TSceneBaseLine::getStartTime, start);
            baseLineLambdaQueryWrapper.eq(TSceneBaseLine::getEndTime, end);
            baseLineLambdaQueryWrapper.eq(TSceneBaseLine::getLineType, baseLineQueryReq.getLineTypeEnum());
            List<TSceneBaseLine> sceneBaseLineList = sceneBaseLineMapper.selectList(baseLineLambdaQueryWrapper);

            if (CollectionUtils.isEmpty(sceneBaseLineList)) {
                return Collections.emptyList();
            }

            Map<Long, List<TSceneBaseLine>> baseActivityMap = sceneBaseLineList.stream().collect(Collectors.groupingBy(TSceneBaseLine::getActivityId));

            List<SceneBaseLineOutput> baseLineOutputs = new ArrayList<>();

            baseActivityMap.forEach((k, v) -> {
                if (CollectionUtils.isEmpty(v)) {
                    return;
                }
                SceneBaseLineOutput baseLineOutput = new SceneBaseLineOutput();
                baseLineOutput.setActivityId(k);
                ActivityResult result = activityDAO.getActivityById(k);
                baseLineOutput.setActivityName(result.getActivityName());
                List<TSceneBaseLine> tmpList = v.stream().sorted(Comparator.comparing(TSceneBaseLine::getRpcId)).collect(Collectors.toList());
                List<SceneBaseLineOutput.SceneBaseLineNode> nodeList = BeanCopyUtils.copyList(tmpList, SceneBaseLineOutput.SceneBaseLineNode.class);
                List<SceneBaseLineOutput.SceneBaseLineNode> sortNodeList = nodeList.stream().sorted(Comparator.comparing(SceneBaseLineOutput.SceneBaseLineNode::getRpcId)).collect(Collectors.toList());
                baseLineOutput.setNodeList(sortNodeList);
                baseLineOutputs.add(baseLineOutput);
            });
            return baseLineOutputs;
        } catch (Exception e) {
            log.error("getPerformanceLineResultList error", e);
        }
        return Collections.emptyList();
    }

    private BaseLineQueryReq getBaseLineQueryReq(long sceneId) {
        try {
            LambdaQueryWrapper<SceneManageEntity> entityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            entityLambdaQueryWrapper.eq(SceneManageEntity::getId, sceneId);
            entityLambdaQueryWrapper.select(SceneManageEntity::getBaseLineReportId, SceneManageEntity::getBaseLineStartTime, SceneManageEntity::getBaseLineEndTime, SceneManageEntity::getLineTypeEnum, SceneManageEntity::getId);
            SceneManageEntity sceneManageEntity = this.sceneManageMapper.selectOne(entityLambdaQueryWrapper);
            if (Objects.isNull(sceneManageEntity)) {
                return null;
            }
            return BaseLineQueryReq.genReqBySceneManageEntity(sceneManageEntity);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据时间段获取基线数据并入库
     *
     * @param baseLineQueryReq
     * @return
     */
    @Override
    public boolean getBaseLineByTimeAndInsert(BaseLineQueryReq baseLineQueryReq) {
        try {
            //这边只能根据sceneId获取业务活动的列表
            Map<String, SceneRequest.Content> contentMap = cloudSceneService.getContent(baseLineQueryReq.getSceneId());
            if (MapUtils.isEmpty(contentMap)) {
                return false;
            }
            List<SceneRequest.Content> contentList = new ArrayList<>(contentMap.values());
            List<Long> activityIds = contentList.stream().filter(a -> a.getBusinessActivityId() > 0)
                    .map(SceneRequest.Content::getBusinessActivityId).collect(Collectors.toList());
            return getBaseLineAndInsert(activityIds, baseLineQueryReq);
        } catch (Exception e) {
            log.error("getBaseLineByTimeAndInsert error", e);
        }
        return false;
    }

    private boolean getBaseLineAndInsert(List<Long> activityIds, BaseLineQueryReq baseLineQueryReq) {
        try {
            if (CollectionUtils.isEmpty(activityIds)) {
                throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "业务活动id不能为空");
            }
            if (baseLineQueryReq.getLineTypeEnum() == SceneBaseLineTypeEnum.TIME.getType()) {
                LambdaQueryWrapper<TSceneBaseLine> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(TSceneBaseLine::getSceneId, baseLineQueryReq.getSceneId());
                lambdaQueryWrapper.eq(TSceneBaseLine::getLineType, baseLineQueryReq.getLineTypeEnum());
                lambdaQueryWrapper.eq(TSceneBaseLine::getStartTime, new Timestamp(baseLineQueryReq.getBaseLineStartTime()));
                lambdaQueryWrapper.eq(TSceneBaseLine::getEndTime, new Timestamp(baseLineQueryReq.getBaseLineEndTime()));
                this.sceneBaseLineMapper.delete(lambdaQueryWrapper);
            }
            List<ActivityResponse> responses = new ArrayList<>();
            for (Long activityId : activityIds) {
                ActivityResponse response = activityService.getActivityByIdWithoutTopology(activityId);
                responses.add(response);
            }
            //去ck库获取基线数据
            List<EntryTraceAvgCostOutput> avgCostDTOList = getStatisticsTraceList(responses, baseLineQueryReq.getBaseLineStartTime(), baseLineQueryReq.getBaseLineEndTime());
            if (CollectionUtils.isEmpty(avgCostDTOList)) {
                return false;
            }

            List<SceneBaseLineInsertDto> baseLineList = avgCostDTOList.stream().filter(a -> Objects.nonNull(a)).map(entryTraceAvgCostRes -> {
                SceneBaseLineInsertDto dto = SceneBaseLineInsertDto.genOb(entryTraceAvgCostRes, baseLineQueryReq);
                if (StringUtils.isNotBlank(entryTraceAvgCostRes.getTraceId())) {
                    List<ReportTraceDetailDTO> traceDetailDTOS = getTraceSnapShotList(entryTraceAvgCostRes.getTraceId());
                    dto.setTraceSnapshot(JSON.toJSONString(traceDetailDTOS));
                }
                return dto;
            }).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(baseLineList)) {
                return false;
            }
            List<TSceneBaseLine> baseLines = BeanCopyUtils.copyList(baseLineList, TSceneBaseLine.class);
            return baseLineService.saveBatch(baseLines, 100);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 查询ck库获取基线数据
     *
     * @param responses
     * @param startTime
     * @param endTime
     * @return
     */
    private List<EntryTraceAvgCostOutput> getStatisticsTraceList(List<ActivityResponse> responses, long startTime, long endTime) {
        try {
            List<EntryTraceAvgCostOutput> traceStatisticsQueryReqList = new ArrayList<>();
            for (ActivityResponse response : responses) {
                TraceStatisticsQueryReq req = new TraceStatisticsQueryReq();
                req.setServiceName(response.getServiceName());
                req.setMethodName(response.getMethod());
                req.setAppName(response.getApplicationName());
                req.setStartTime(getTimeStr(startTime));
                req.setEndTime(getTimeStr(endTime));
                req.setTenantAppKey(WebPluginUtils.traceTenantCommonExt().getTenantAppKey());
                req.setEnvCode(WebPluginUtils.traceTenantCommonExt().getEnvCode());
                List<EntryTraceAvgCostDTO> tempList = traceClient.getStatisticsTraceList(Arrays.asList(req));
                if (CollectionUtils.isEmpty(tempList)) {
                    continue;
                }
                List<EntryTraceAvgCostOutput> list = BeanCopyUtils.copyList(tempList, EntryTraceAvgCostOutput.class);
                for (EntryTraceAvgCostOutput avgCostRes : list) {
                    avgCostRes.setActivityId(response.getActivityId());
                }
                traceStatisticsQueryReqList.addAll(list);
            }
            return traceStatisticsQueryReqList;
        } catch (Exception e) {
            throw e;
        }
    }

    private static String getTimeStr(long time) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    /**
     * 根据报告id获取基线数据并入库
     *
     * @param reportId
     * @return
     */
    @Override
    public boolean getBaseLineByReportIdAndInsert(long reportId) {
        try {
            //获取所有的业务活动和对应的指标
            LambdaQueryWrapper<ReportBusinessActivityDetailEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ReportBusinessActivityDetailEntity::getReportId, reportId);
            lambdaQueryWrapper.eq(ReportBusinessActivityDetailEntity::getIsDeleted, 0);
            List<ReportBusinessActivityDetailEntity> reportBusinessActivityDetailList = detailMapper.selectList(lambdaQueryWrapper);
            if (CollectionUtils.isEmpty(reportBusinessActivityDetailList)) {
                return false;
            }
            List<Long> activityIdList = reportBusinessActivityDetailList.stream()
                    .filter(a -> a.getBusinessActivityId() > 0)
                    .map(ReportBusinessActivityDetailEntity::getBusinessActivityId).collect(Collectors.toList());

            LambdaQueryWrapper<ReportEntity> reportEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            reportEntityLambdaQueryWrapper.eq(ReportEntity::getId, reportId);
            reportEntityLambdaQueryWrapper.eq(ReportEntity::getIsDeleted, 0);
            reportEntityLambdaQueryWrapper.select(ReportEntity::getStartTime, ReportEntity::getEndTime, ReportEntity::getSceneId);
            ReportEntity reportEntity = tReportMapper.selectOne(reportEntityLambdaQueryWrapper);

            if (Objects.isNull(reportEntity)) {
                log.error("getBaseLineByReportIdAndInsert get reportEntity error params:{}", reportId);
                return false;
            }
            //查询基线数据并入库
            BaseLineQueryReq req = new BaseLineQueryReq();
            req.setReportId(reportId);
            req.setSceneId(reportEntity.getSceneId());
            req.setBaseLineStartTime(reportEntity.getStartTime().getTime());
            req.setBaseLineEndTime(reportEntity.getEndTime().getTime());
            req.setLineTypeEnum(SceneBaseLineTypeEnum.REPORT.getType());
            return getBaseLineAndInsert(activityIdList, req);
        } catch (Exception e) {
            log.error("getBaseLineByReportIdAndInsert error", e);
        }
        return false;
    }

    /**
     * 只用来设置自定义时间区间的性能基线,选择过去报告的性能基线的时候才去查询ck，报告基线压测结束自动生成。
     *
     * @param baseLineQueryReq
     * @return
     */
    @Override
    public boolean performanceLineCreate(BaseLineQueryReq baseLineQueryReq) {
        //先设置压测场景
        updateSceneManageBaseLineSet(baseLineQueryReq);
        if (baseLineQueryReq.getLineTypeEnum() == SceneBaseLineTypeEnum.TIME.getType()) {
            return getBaseLineByTimeAndInsert(baseLineQueryReq);
        }
        return true;
    }

    private int updateSceneManageBaseLineSet(BaseLineQueryReq baseLineQueryReq) {
        Date start;
        Date end;
        try {
            if (baseLineQueryReq.getLineTypeEnum() == 2) {
                LambdaQueryWrapper<ReportEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(ReportEntity::getId, baseLineQueryReq.getReportId());
                lambdaQueryWrapper.select(ReportEntity::getStartTime, ReportEntity::getEndTime);
                ReportEntity reportEntity = this.tReportMapper.selectOne(lambdaQueryWrapper);
                start = reportEntity.getStartTime();
                end = reportEntity.getEndTime();
            } else {
                start = new Date(baseLineQueryReq.getBaseLineStartTime());
                end = new Date(baseLineQueryReq.getBaseLineEndTime());
            }
            LambdaUpdateWrapper<SceneManageEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(SceneManageEntity::getId, baseLineQueryReq.getSceneId());
            lambdaUpdateWrapper.set(SceneManageEntity::getBaseLineReportId, baseLineQueryReq.getReportId());
            lambdaUpdateWrapper.set(SceneManageEntity::getBaseLineStartTime, start);
            lambdaUpdateWrapper.set(SceneManageEntity::getBaseLineEndTime, end);
            lambdaUpdateWrapper.set(SceneManageEntity::getLineTypeEnum, baseLineQueryReq.getLineTypeEnum());
            return this.sceneManageMapper.update(null, lambdaUpdateWrapper);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Long> getReportListById(Long sceneId) {
        try {
            LambdaQueryWrapper<ReportEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ReportEntity::getSceneId, sceneId);
            lambdaQueryWrapper.eq(ReportEntity::getIsDeleted, 0);
            lambdaQueryWrapper.select(ReportEntity::getId);
            List<ReportEntity> reportEntityList = tReportMapper.selectList(lambdaQueryWrapper);
            if (CollectionUtils.isEmpty(reportEntityList)) {
                return Collections.emptyList();
            }
            return reportEntityList.stream().map(ReportEntity::getId).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("getReportListById error", e);
        }
        return Collections.emptyList();
    }

    private List<ReportTraceDetailDTO> getTraceSnapShotList(String traceId) {
        ReportLinkDetailResponse response = reportRealTimeService.getLinkDetail(traceId, 0);
        return response.getTraces();
    }


    @Override
    public List<ReportTraceDetailDTO> getTraceSnapShot(long reportId) {
        try {
            LambdaQueryWrapper<TReportBaseLinkProblem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(TReportBaseLinkProblem::getReportId, reportId);
            lambdaQueryWrapper.select(TReportBaseLinkProblem::getTraceSnapshot);
            TReportBaseLinkProblem reportBaseLinkProblem = this.reportBaseLinkProblemMapper.selectOne(lambdaQueryWrapper);
            if (Objects.isNull(reportBaseLinkProblem) || StringUtils.isBlank(reportBaseLinkProblem.getTraceSnapshot())) {
                return Collections.emptyList();
            }
            return JSON.parseArray(reportBaseLinkProblem.getTraceSnapshot(), ReportTraceDetailDTO.class);
        } catch (Exception e) {
            log.error("getTraceSnapShot error", e);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean getBaseLineProblemAndInsert(long reportId) {
        try {
            LambdaQueryWrapper<ReportEntity> reportEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            reportEntityLambdaQueryWrapper.eq(ReportEntity::getId, reportId);
            reportEntityLambdaQueryWrapper.select(ReportEntity::getSceneId, ReportEntity::getStartTime, ReportEntity::getEndTime, ReportEntity::getId);
            ReportEntity report = this.tReportMapper.selectOne(reportEntityLambdaQueryWrapper);
            if (Objects.isNull(report)) {
                log.info("getBaseLineProblemAndInsert report is null,params={}", reportId);
                return false;
            }

            LambdaQueryWrapper<SceneManageEntity> sceneEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            sceneEntityLambdaQueryWrapper.eq(SceneManageEntity::getId, report.getSceneId());
            sceneEntityLambdaQueryWrapper.select(SceneManageEntity::getLineTypeEnum, SceneManageEntity::getBaseLineReportId, SceneManageEntity::getBaseLineStartTime, SceneManageEntity::getBaseLineEndTime, SceneManageEntity::getId);
            SceneManageEntity sceneManageEntity = this.sceneManageMapper.selectOne(sceneEntityLambdaQueryWrapper);
            if (sceneManageEntity.getLineTypeEnum() == SceneBaseLineTypeEnum.NONE.getType()) {
                log.info("getBaseLineProblemAndInsert scene ={}", JSON.toJSONString(sceneManageEntity));
                return false;
            }
            //基线性能数据查询
            BaseLineQueryReq baseLineQueryReq = BaseLineQueryReq.getBaseLineReq(sceneManageEntity);
            List<SceneBaseLineOutput> baseLineList = getPerformanceLineResultList(baseLineQueryReq);
            Map<Long, SceneBaseLineOutput> baseLineOutputMap = baseLineList.stream().collect(Collectors.toMap(SceneBaseLineOutput::getActivityId, value -> value, (k1, k2) -> k1));
            //当前报告性能查询
            BaseLineQueryReq currentLineQueryReq = BaseLineQueryReq.getCurrentLineReq(sceneManageEntity, report);

            Retryer<List<SceneBaseLineOutput>> retryer = RetryerBuilder.<List<SceneBaseLineOutput>>newBuilder()
                    .retryIfResult(coll -> CollectionUtils.isEmpty(coll))
                    .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                    .withWaitStrategy(WaitStrategies.fixedWait(2000, TimeUnit.MILLISECONDS))
                    .build();

            List<SceneBaseLineOutput> currentLineList = retryer.call(() -> getPerformanceLineResultList(currentLineQueryReq));

            if (CollectionUtils.isEmpty(baseLineList)) {
                log.info("基线数据为空,params={}", JSON.toJSONString(baseLineQueryReq));
                return false;
            }

            //比较数据放在这里
            List<TReportBaseLinkProblem> list = new ArrayList<>();

            for (SceneBaseLineOutput currentLine : currentLineList) {
                SceneBaseLineOutput baseline = baseLineOutputMap.get(currentLine.getActivityId());
                if (Objects.isNull(baseline)) {
                    continue;
                }
                List<SceneBaseLineOutput.SceneBaseLineNode> baseNodeList = baseline.getNodeList();

                Map<String, SceneBaseLineOutput.SceneBaseLineNode> baseMap = baseNodeList.stream().collect(Collectors.toMap(a -> a.getAppName() + a.getServiceName() + a.getMethodName(), value -> value, (k1, k2) -> k1));
                List<SceneBaseLineOutput.SceneBaseLineNode> currentNodeList = currentLine.getNodeList();
                for (SceneBaseLineOutput.SceneBaseLineNode currentSceneBaseLineNode : currentNodeList) {
                    if (Objects.isNull(currentSceneBaseLineNode)) {
                        continue;
                    }
                    String key = currentSceneBaseLineNode.getAppName() + currentSceneBaseLineNode.getServiceName() + currentSceneBaseLineNode.getMethodName();
                    SceneBaseLineOutput.SceneBaseLineNode baseNode = baseMap.get(key);
                    String reason = getProblemReason(baseNode, currentSceneBaseLineNode);
                    if (StringUtils.isBlank(reason)) {
                        continue;
                    }
                    TReportBaseLinkProblem problem = BeanCopyUtils.copyObject(currentSceneBaseLineNode, TReportBaseLinkProblem.class);
                    problem.setReason(reason);
                    problem.setActivityId(currentLine.getActivityId());
                    problem.setActivityName(currentLine.getActivityName());
                    problem.setReportId(currentLineQueryReq.getReportId());
                    problem.setSceneId(currentLineQueryReq.getSceneId());
                    BigDecimal baseRt = Objects.isNull(baseNode) ? new BigDecimal(0) : baseNode.getRt();
                    BigDecimal baseSuccessRate = Objects.isNull(baseNode) ? new BigDecimal(0) : baseNode.getSuccessRate();
                    problem.setBaseRt(baseRt);
                    problem.setBaseSuccessRate(baseSuccessRate);
                    problem.setRt(currentSceneBaseLineNode.getRt());
                    problem.setSuccessRate(currentSceneBaseLineNode.getSuccessRate());
                    problem.setServiceName(currentSceneBaseLineNode.getServiceName());
                    problem.setMethodName(currentSceneBaseLineNode.getMethodName());
                    problem.setAppName(currentSceneBaseLineNode.getAppName());
                    problem.setRpcId(currentSceneBaseLineNode.getRpcId());
                    problem.setRpcType(currentSceneBaseLineNode.getRpcType());
                    problem.setLogType(currentSceneBaseLineNode.getLogType());
                    problem.setLineType(currentLineQueryReq.getLineTypeEnum());
                    problem.setTraceSnapshot(currentSceneBaseLineNode.getTraceSnapshot());
                    problem.setTotalRequest(currentSceneBaseLineNode.getTotalRequest());
                    list.add(problem);
                }
            }

            if (CollectionUtils.isEmpty(list)) {
                log.info("没有问题节点,params={}", JSON.toJSONString(currentLineQueryReq));
                return false;
            }
            return this.reportBaseLinkProblemService.saveBatch(list, 100);
        } catch (Exception e) {
            log.error("getBaseLineProblemAndInsert error", e);
        }
        return false;
    }


    private List<SceneBaseLineOutput> getPerformanceLineResultList(BaseLineQueryReq baseLineQueryReq) {
        try {
            LambdaQueryWrapper<TSceneBaseLine> baseLineLambdaQueryWrapper = new LambdaQueryWrapper<>();
            baseLineLambdaQueryWrapper.eq(TSceneBaseLine::getSceneId, baseLineQueryReq.getSceneId());
            baseLineLambdaQueryWrapper.eq(TSceneBaseLine::getIsDelete, 0);
            baseLineLambdaQueryWrapper.eq(TSceneBaseLine::getStartTime, new Timestamp(baseLineQueryReq.getBaseLineStartTime()));
            baseLineLambdaQueryWrapper.eq(TSceneBaseLine::getEndTime, new Timestamp(baseLineQueryReq.getBaseLineEndTime()));
            if (Objects.nonNull(baseLineQueryReq.getReportId())) {
                baseLineLambdaQueryWrapper.eq(TSceneBaseLine::getReportId, baseLineQueryReq.getReportId());
            }
            List<TSceneBaseLine> sceneBaseLineList = sceneBaseLineMapper.selectList(baseLineLambdaQueryWrapper);

            Map<Long, List<TSceneBaseLine>> baseActivityMap = sceneBaseLineList.stream().collect(Collectors.groupingBy(TSceneBaseLine::getActivityId));

            List<SceneBaseLineOutput> baseLineOutputs = new ArrayList<>();

            baseActivityMap.forEach((k, v) -> {
                if (CollectionUtils.isEmpty(v)) {
                    return;
                }
                SceneBaseLineOutput baseLineOutput = new SceneBaseLineOutput();
                baseLineOutput.setActivityId(k);
                ActivityResult result = activityDAO.getActivityById(k);
                baseLineOutput.setActivityName(result.getActivityName());
                List<TSceneBaseLine> tmpList = v.stream().peek(a -> {
                    BigDecimal successRate = Optional.ofNullable(a.getSuccessRate()).orElse(new BigDecimal(0));
                    BigDecimal rt = Optional.ofNullable(a.getRt()).orElse(new BigDecimal(0));
                    a.setSuccessRate(successRate);
                    a.setRt(rt);
                }).sorted(Comparator.comparing(TSceneBaseLine::getRpcId)).collect(Collectors.toList());
                List<SceneBaseLineOutput.SceneBaseLineNode> nodeList = BeanCopyUtils.copyList(tmpList, SceneBaseLineOutput.SceneBaseLineNode.class);
                baseLineOutput.setNodeList(nodeList);
                baseLineOutputs.add(baseLineOutput);
            });
            return baseLineOutputs;
        } catch (Exception e) {
            throw e;
        }
    }


    private static String getProblemReason(SceneBaseLineOutput.SceneBaseLineNode baseNode, SceneBaseLineOutput.SceneBaseLineNode currentSceneBaseLineNode) {
        List<String> list = new ArrayList<>();
        if (Objects.isNull(baseNode)) {
            list.add(BaseLinkProblemReasonEnum.NONE_NODE.getReason());
            return CollectionUtils.isEmpty(list) ? null : JSON.toJSONString(list);
        }
        if (baseNode.getSuccessRate().compareTo(currentSceneBaseLineNode.getSuccessRate()) > 0) {
            list.add(BaseLinkProblemReasonEnum.NODE_SUCCESS_RATE_LOW.getReason());
        }
        if (baseNode.getRt().compareTo(currentSceneBaseLineNode.getRt()) < 0) {
            list.add(BaseLinkProblemReasonEnum.NODE_RT_HIGH.getReason());
        }
        return CollectionUtils.isEmpty(list) ? null : JSON.toJSONString(list);
    }

    @Override
    public List<TReportBaseLinkProblemOutput> getReportProblemList(long reportId) {
        try {
            //如果查询的时候发现没有数据，先去比较一下入库再去查询。
            if (countProblem(reportId) == 0) {
                getBaseLineProblemAndInsert(reportId);
            }

            LambdaQueryWrapper<TReportBaseLinkProblem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(TReportBaseLinkProblem::getReportId, reportId);
            lambdaQueryWrapper.eq(TReportBaseLinkProblem::getIsDelete, 0);
            List<TReportBaseLinkProblem> list = reportBaseLinkProblemService.list(lambdaQueryWrapper);

            Map<Long, List<TReportBaseLinkProblem>> activityMap = list.stream().collect(Collectors.groupingBy(TReportBaseLinkProblem::getActivityId));

            List<TReportBaseLinkProblemOutput> outputList = new ArrayList<>();

            activityMap.forEach((k, v) -> {
                if (CollectionUtils.isEmpty(v)) {
                    return;
                }
                List<TReportBaseLinkProblemOutput.BaseLineProblemNode> nodeList = BeanCopyUtils.copyList(v, TReportBaseLinkProblemOutput.BaseLineProblemNode.class);
                TReportBaseLinkProblemOutput.BaseLineProblemNode root = nodeList.stream()
                        .filter(a -> Objects.nonNull(a)).findFirst().orElse(null);

                TReportBaseLinkProblemOutput output = new TReportBaseLinkProblemOutput();
                if (root != null) {
                    output.setTraceSnapshot(root.getTraceSnapshot());
                    output.setActivityName(root.getActivityName());
                    output.setActivityId(root.getActivityId());
                }
                for (TReportBaseLinkProblemOutput.BaseLineProblemNode node : nodeList) {
                    if (node.getRpcId().equals("0")) {
                        node.setTraceSnapshot(null);
                    }
                    BigDecimal num = Optional.ofNullable(node.getTotalRequest()).orElse(new BigDecimal(0));
                    BigDecimal total = node.getRt().subtract(node.getBaseRt()).multiply(num);
                    node.setTotalOptimizableRt(total);
                }
                List<TReportBaseLinkProblemOutput.BaseLineProblemNode> sortNodeList = nodeList.stream()
                        .sorted(Comparator.comparing(TReportBaseLinkProblemOutput.BaseLineProblemNode::getTotalOptimizableRt).reversed())
                        .collect(Collectors.toList());
                BigDecimal maxRt = sortNodeList.stream().findFirst().map(node -> node.getTotalOptimizableRt())
                        .orElse(BigDecimal.ZERO);
                output.setBaseLineProblemNodes(sortNodeList);
                output.setMaxOptimizableRt(maxRt);
                outputList.add(output);
            });

            return outputList.stream().sorted(Comparator.comparing(TReportBaseLinkProblemOutput::getMaxOptimizableRt).reversed()).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("getReportProblemList,error", e);
        }
        return Collections.emptyList();
    }


    @Override
    public long countProblem(long reportId) {
        LambdaQueryWrapper<TReportBaseLinkProblem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TReportBaseLinkProblem::getReportId, reportId);
        lambdaQueryWrapper.eq(TReportBaseLinkProblem::getIsDelete, 0);
        return reportBaseLinkProblemService.count(lambdaQueryWrapper);
    }
}
