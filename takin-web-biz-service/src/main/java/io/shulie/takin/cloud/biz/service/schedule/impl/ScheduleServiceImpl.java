package io.shulie.takin.cloud.biz.service.schedule.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import com.pamirs.takin.cloud.entity.dao.schedule.TScheduleRecordMapper;
import com.pamirs.takin.cloud.entity.domain.entity.schedule.ScheduleRecord;
import com.pamirs.takin.cloud.entity.domain.vo.report.SceneTaskNotifyParam;
import com.pamirs.takin.cloud.entity.domain.vo.scenemanage.SceneManageStartRecordVO;
import io.shulie.takin.adapter.api.constant.FormulaSymbol;
import io.shulie.takin.adapter.api.constant.FormulaTarget;
import io.shulie.takin.adapter.api.constant.JobType;
import io.shulie.takin.adapter.api.constant.ThreadGroupType;
import io.shulie.takin.adapter.api.entrypoint.pressure.PressureTaskApi;
import io.shulie.takin.adapter.api.model.common.TimeBean;
import io.shulie.takin.adapter.api.model.request.pressure.PressureTaskStartReq;
import io.shulie.takin.adapter.api.model.request.pressure.PressureTaskStartReq.SlaInfo;
import io.shulie.takin.adapter.api.model.request.pressure.PressureTaskStartReq.ThreadConfigInfo;
import io.shulie.takin.cloud.biz.cache.SceneTaskStatusCache;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.biz.service.async.CloudAsyncService;
import io.shulie.takin.cloud.biz.service.engine.EngineConfigService;
import io.shulie.takin.cloud.biz.service.record.ScheduleRecordEnginePluginService;
import io.shulie.takin.cloud.biz.service.report.CloudReportService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneManageService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneTaskService;
import io.shulie.takin.cloud.biz.service.schedule.ScheduleEventService;
import io.shulie.takin.cloud.biz.service.schedule.ScheduleService;
import io.shulie.takin.cloud.biz.service.strategy.StrategyConfigService;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.constants.FileSplitConstants;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.data.dao.scene.task.PressureTaskDAO;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.ext.content.enginecall.BusinessActivityExt;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleInitParamExt;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleRunRequest;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStartRequestExt;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStartRequestExt.DataFile;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStartRequestExt.SlaConfig;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStartRequestExt.StartEndPosition;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStopRequestExt;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyConfigExt;
import io.shulie.takin.cloud.ext.content.enginecall.ThreadGroupConfigExt;
import io.shulie.takin.cloud.model.request.StartRequest;
import io.shulie.takin.cloud.model.request.StartRequest.FileInfo;
import io.shulie.takin.cloud.model.request.StartRequest.FileInfo.SplitInfo;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.common.util.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author ??????
 */
@Service
@Slf4j
public class ScheduleServiceImpl extends AbstractIndicators implements ScheduleService {
    @Resource
    private StrategyConfigService strategyConfigService;
    @Resource
    private TScheduleRecordMapper tScheduleRecordMapper;
    @Resource
    private ScheduleEventService scheduleEvent;
    @Resource
    private CloudSceneManageService cloudSceneManageService;
    @Resource
    private ScheduleRecordEnginePluginService scheduleRecordEnginePluginService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CloudReportService cloudReportService;
    @Resource
    private AppConfig appConfig;
    @Resource
    private PressureTaskApi pressureTaskApi;
    @Resource
    private PressureTaskDAO pressureTaskDAO;
    @Resource
    private EngineConfigService engineConfigService;
    @Resource
    private CloudAsyncService cloudAsyncService;
    @Resource
    private RedisClientUtil redisClientUtil;
    @Resource
    private CloudSceneTaskService cloudSceneTaskService;
    @Resource
    private SceneTaskStatusCache taskStatusCache;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void startSchedule(ScheduleStartRequestExt request) {
        log.info("????????????, ???????????????{}", request);
        //?????????????????????
        ScheduleRecord schedule = tScheduleRecordMapper.getScheduleByTaskId(request.getTaskId());
        if (schedule != null) {
            log.error("???????????????{}???,????????????????????????????????? --> ????????????[{}]????????????",
                TakinCloudExceptionEnum.SCHEDULE_START_ERROR, request.getTaskId());
            return;
        }
        //????????????
        StrategyConfigExt config = strategyConfigService.getCurrentStrategyConfig();
        if (config == null) {
            log.error("???????????????{}???,????????????????????????????????? --> ?????????????????????",
                TakinCloudExceptionEnum.SCHEDULE_START_ERROR);
            return;
        }

        String scheduleName = ScheduleConstants.getScheduleName(request.getSceneId(), request.getTaskId(), request.getTenantId());

        //??????????????????
        ScheduleRecord scheduleRecord = new ScheduleRecord();
        scheduleRecord.setCpuCoreNum(config.getCpuNum());
        scheduleRecord.setPodNum(request.getTotalIp());
        scheduleRecord.setMemorySize(config.getMemorySize());
        scheduleRecord.setSceneId(request.getSceneId());
        scheduleRecord.setTaskId(request.getTaskId());
        scheduleRecord.setStatus(ScheduleConstants.SCHEDULE_STATUS_1);

        scheduleRecord.setTenantId(request.getTenantId());
        scheduleRecord.setPodClass(scheduleName);
        tScheduleRecordMapper.insertSelective(scheduleRecord);

        //add by ?????? ????????????????????????????????????????????????
        scheduleRecordEnginePluginService.saveScheduleRecordEnginePlugins(
            scheduleRecord.getId(), request.getEnginePluginsFilePath());
        //add end

        //????????????
        ScheduleRunRequest eventRequest = new ScheduleRunRequest();
        eventRequest.setScheduleId(scheduleRecord.getId());
        eventRequest.setRequest(request);
        eventRequest.setStrategyConfig(config);
        String memSetting;
        if (PressureSceneEnum.INSPECTION_MODE.getCode().equals(request.getPressureScene())) {
            memSetting = "-XX:MaxRAMPercentage=90.0";
        } else {
            memSetting = CommonUtil.getValue(appConfig.getK8sJvmSettings(), config, StrategyConfigExt::getK8sJvmSettings);
        }
        eventRequest.setMemSetting(memSetting);
        Integer traceSampling = 1;
        if (!request.isTryRun() && !request.isInspect()) {
            traceSampling = CommonUtil.getValue(traceSampling, engineConfigService, EngineConfigService::getLogSimpling);
        }
        eventRequest.setTraceSampling(traceSampling);
        //???????????????????????????????????????????????????
        stringRedisTemplate.opsForValue().set(scheduleName, JSON.toJSONString(eventRequest));
        // ????????? ???????????? pod????????????redis,???????????????
        // ?????? ?????????????????? ?????????????????????????????????????????????24??????
        stringRedisTemplate.opsForValue().set(
            ScheduleConstants.getPressureNodeTotalKey(request.getSceneId(), request.getTaskId(), request.getTenantId()),
            String.valueOf(request.getTotalIp()), 1, TimeUnit.DAYS);
        //???????????????
        scheduleEvent.initSchedule(eventRequest);
    }

    @Override
    public void stopSchedule(ScheduleStopRequestExt request) {
        log.info("????????????, ???????????????{}", request);
        Long sceneId = request.getSceneId();
        String resourceId = request.getResourceId();
        if (StringUtils.isNotBlank(resourceId)) {
            String stopTaskMessageKey = PressureStartCache.getStopTaskMessageKey(sceneId);
            String stopTaskMessage = redisClientUtil.getString(stopTaskMessageKey);
            if (Objects.isNull(stopTaskMessage)) {
                stopTaskMessage = "????????????";
            }
            if (redisClientUtil.hasLockKey(PressureStartCache.getJmeterStartFirstKey(resourceId))) {
                callRunningFailedEvent(resourceId, stopTaskMessage);
            } else {
                callStartFailedEvent(resourceId, stopTaskMessage);
            }
        } else {
            // ?????????????????????????????????,?????????????????????
            Long reportId = request.getTaskId();
            cloudReportService.updateReportFeatures(reportId, ReportConstants.FINISH_STATUS, null, null);
            cloudSceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId, request.getTenantId())
                .checkEnum(SceneManageStatusEnum.getAll()).updateEnum(SceneManageStatusEnum.WAIT).build());
            taskStatusCache.cacheStatus(sceneId, reportId, SceneRunTaskStatusEnum.ENDED);
            String reportKey = WebRedisKeyConstant.getReportKey(reportId);
            redisTemplate.opsForList().remove(WebRedisKeyConstant.getTaskList(), 0, reportKey);
            redisTemplate.delete(reportKey);
        }
    }

    @Override
    public void runSchedule(ScheduleRunRequest request) {
        ScheduleStartRequestExt startRequest = request.getRequest();
        // ?????????????????????
        push(startRequest);

        Long sceneId = startRequest.getSceneId();
        Long taskId = startRequest.getTaskId();
        Long customerId = startRequest.getTenantId();

        // ???????????????????????? ?????????(??????????????????) ---> ??????Job???
        cloudSceneManageService.updateSceneLifeCycle(
            UpdateStatusBean.build(sceneId, taskId, customerId)
                .checkEnum(SceneManageStatusEnum.STARTING, SceneManageStatusEnum.FILE_SPLIT_END)
                .updateEnum(SceneManageStatusEnum.JOB_CREATING)
                .build());

        request.setCallbackUrl(appConfig.getCallbackUrl());
        try {
            PressureTaskStartReq req = buildStartReq(request);
            notifyTaskResult(request);
            String resourceId = String.valueOf(req.getResourceId());
            if (!redisClientUtil.hasLockKey(PressureStartCache.getStopFlag(resourceId))) {
                Long jobId = pressureTaskApi.start(req);
                redisClientUtil.lockNoExpire(PressureStartCache.getStartFlag(resourceId), String.valueOf(jobId));
                log.info("??????{},??????{},??????{}????????????????????? ??????????????????", sceneId, taskId, customerId);
                updateReportAssociation(startRequest, jobId);
                cloudAsyncService.checkJmeterStartedTask(getResourceContext(resourceId));
            }
        } catch (Exception e) {
            // ????????????
            log.info("??????{},??????{},??????{}???????????????????????????????????????", sceneId, taskId, customerId);
            cloudSceneManageService.reportRecord(SceneManageStartRecordVO.build(sceneId, taskId, customerId).success(false)
                .errorMsg("??????????????????????????????????????????" + e.getMessage()).build());

        }
    }

    @Override
    public void initScheduleCallback(ScheduleInitParamExt param) {

    }

    /**
     * ???????????????
     * ?????????????????????????????????redis??????, ???????????????????????????????????????????????????
     */
    private void push(ScheduleStartRequestExt request) {
        //?????????????????????
        String key = ScheduleConstants.getFileSplitQueue(request.getSceneId(), request.getTaskId(), request.getTenantId());
        // ????????????
        List<String> numList = IntStream.rangeClosed(1, request.getTotalIp())
            .boxed().map(String::valueOf)
            .collect(Collectors.toCollection(ArrayList::new));
        // ????????????Redis
        stringRedisTemplate.opsForList().leftPushAll(key, numList);
    }

    public static PressureTaskStartReq buildStartReq(ScheduleRunRequest runRequest) {
        ScheduleStartRequestExt request = runRequest.getRequest();
        PressureTaskStartReq req = new PressureTaskStartReq();
        req.setCallbackUrl(runRequest.getCallbackUrl());
        req.setResourceId(Long.valueOf(request.getResourceId()));
        req.setJvmOptions(runRequest.getMemSetting());
        req.setType(JobType.of(request.getPressureScene()));
        req.setName(String.valueOf(request.getSceneId()));
        req.setSampling(runRequest.getTraceSampling());
        req.setBindByXpathMd5(request.getBindByXpathMd5());
        req.setThreadConfig(buildThreadGroup(request));
        completedSla(req, request);
        completedMetrics(req, request);
        completedFile(req, request);
        completedExt(req, request);
        return req;
    }


    private static List<ThreadConfigInfo> buildThreadGroup(ScheduleStartRequestExt requestExt) {
        Long continuedTime = requestExt.getContinuedTime();
        Double tps = requestExt.getTps();
        Integer intTps = Objects.nonNull(tps) ? tps.intValue() : null;
        return requestExt.getThreadGroupConfigMap().entrySet().stream().map(entry -> {
            ThreadConfigInfo info = new ThreadConfigInfo();
            String key = entry.getKey();
            info.setRef(key);
            ThreadGroupConfigExt ext = entry.getValue();
            info.setType(ThreadGroupType.of(ext.getType(), ext.getMode()));
            info.setDuration(continuedTime.intValue());
            info.setNumber(ext.getThreadNum());
            Integer rampUp = ext.getRampUp();
            if (Objects.nonNull(rampUp)) {
                info.setGrowthTime(Long.valueOf(new TimeBean(rampUp.longValue(),
                    ext.getRampUpUnit()).getSecondTime()).intValue());
            }
            info.setGrowthStep(ext.getSteps());
            info.setTps(intTps);
            return info;
        }).collect(Collectors.toList());
    }

    private static void completedSla(PressureTaskStartReq req, ScheduleStartRequestExt request) {
        List<SlaConfig> conditionList = new ArrayList<>();
        List<SlaConfig> stopCondition = request.getStopCondition();
        if (!CollectionUtils.isEmpty(stopCondition)) {
            conditionList.addAll(stopCondition);
        }
        List<SlaConfig> waringCondition = request.getWarningCondition();
        if (!CollectionUtils.isEmpty(waringCondition)) {
            conditionList.addAll(waringCondition);
        }
        List<SlaConfig> finalCondition = conditionList.stream().filter(
            condition -> Objects.nonNull(FormulaTarget.of(condition.getIndexInfo()))).collect(
            Collectors.toList());
        List<SlaInfo> slaConfigs = new ArrayList<>(conditionList.size());
        req.setSlaConfig(slaConfigs);
        if (!CollectionUtils.isEmpty(finalCondition)) {
            slaConfigs.addAll(
                finalCondition.stream().map(ScheduleServiceImpl::convertSlaConfig).collect(Collectors.toList()));
        }
    }

    private static void completedMetrics(PressureTaskStartReq req, ScheduleStartRequestExt requestExt) {
        Map<String, BusinessActivityExt> businessData = requestExt.getBusinessData();
        req.setMetricsConfig(businessData.values().stream().map(value -> {
            StartRequest.MetricsInfo metrics = new StartRequest.MetricsInfo();
            metrics.setRef(value.getBindRef());
            metrics.setTps(value.getTps().doubleValue());
            metrics.setRt(value.getRt().doubleValue());
            metrics.setSuccessRate(value.getSa().doubleValue());
            metrics.setSa(value.getSa().doubleValue());
            return metrics;
        }).collect(Collectors.toList()));
    }

    private static void completedFile(PressureTaskStartReq req, ScheduleStartRequestExt requestExt) {
        FileInfo script = new FileInfo();
        script.setUri(requestExt.getScriptPath());
        req.setScriptFile(script);
        List<DataFile> dataFile = requestExt.getDataFile();
        if (!CollectionUtils.isEmpty(dataFile)) {
            Map<Integer, List<DataFile>> fileTypeMap = dataFile.stream().collect(groupingBy(DataFile::getFileType));
            if (!CollectionUtils.isEmpty(fileTypeMap)) {
                List<DataFile> dataFiles = fileTypeMap.get(FileSplitConstants.FILE_TYPE_DATA_FILE);
                if (!CollectionUtils.isEmpty(dataFiles)) {
                    req.getDataFile().addAll(dataFiles.stream()
                        .map(ScheduleServiceImpl::convertFile).collect(Collectors.toList()));
                }
                List<DataFile> attachmentFiles = fileTypeMap.get(FileSplitConstants.FILE_TYPE_EXTRA_FILE);
                if (!CollectionUtils.isEmpty(attachmentFiles)) {
                    List<FileInfo> dependencies = attachmentFiles.stream()
                        .map(ScheduleServiceImpl::convertFile).collect(Collectors.toList());
                    req.getDependencyFile().addAll(dependencies);
                }
            }
        }
        List<String> enginePluginsFilePath = requestExt.getEnginePluginsFilePath();
        if (!CollectionUtils.isEmpty(enginePluginsFilePath)) {
            List<FileInfo> dependencies = enginePluginsFilePath.stream().map(path -> {
                FileInfo info = new FileInfo();
                info.setUri(path);
                return info;
            }).collect(Collectors.toList());
            req.getDependencyFile().addAll(dependencies);
        }
    }

    private static FileInfo convertFile(DataFile file) {
        FileInfo info = new FileInfo();
        info.setUri(file.getPath());
        Map<Integer, List<StartEndPosition>> positions = file.getStartEndPositions();
        if (!CollectionUtils.isEmpty(positions)) {
            info.setSplitList(positions.values().stream().flatMap(Collection::stream).map(position -> {
                SplitInfo splitInfo = new SplitInfo();
                splitInfo.setStart(Long.parseLong(position.getStart()));
                splitInfo.setEnd(Long.parseLong(position.getEnd()));
                return splitInfo;
            }).collect(Collectors.toList()));
        }
        return info;
    }

    private static void completedExt(PressureTaskStartReq req, ScheduleStartRequestExt request) {
        if (request.isTryRun()) {
            Map<String, String> ext = new HashMap<>(4);
            ext.put("loopsNum", String.valueOf(request.getLoopsNum()));
            ext.put("expectThroughput", String.valueOf(request.getConcurrenceNum()));
            req.setExt(ext);
        }
    }

    private static SlaInfo convertSlaConfig(SlaConfig config) {
        SlaInfo info = new SlaInfo();
        info.setRef(config.getActivity());
        info.setAttach(String.valueOf(config.getId()));
        info.setFormulaTarget(FormulaTarget.of(config.getIndexInfo()));
        info.setFormulaSymbol(FormulaSymbol.ofValue(config.getCondition()));
        info.setFormulaNumber(config.getDuring().doubleValue());
        return info;
    }

    private void updateReportAssociation(ScheduleStartRequestExt startRequest, Long jobId) {
        ReportUpdateParam report = new ReportUpdateParam();
        report.setId(startRequest.getTaskId());
        report.setJobId(jobId);
        cloudReportService.updateReportById(report);
        String resourceId = startRequest.getResourceId();
        pressureTaskDAO.updateResourceAssociation(resourceId, jobId);
        redisClientUtil.hmset(PressureStartCache.getResourceKey(resourceId), PressureStartCache.JOB_ID, jobId);
        redisClientUtil.hmset(PressureStartCache.getSceneResourceKey(startRequest.getSceneId()),
            PressureStartCache.JOB_ID, jobId);
    }
    private void notifyTaskResult(ScheduleRunRequest request) {
        SceneTaskNotifyParam notify = new SceneTaskNotifyParam();
        notify.setSceneId(request.getRequest().getSceneId());
        notify.setTaskId(request.getRequest().getTaskId());
        notify.setTenantId(request.getRequest().getTenantId());
        notify.setStatus("started");
        cloudSceneTaskService.taskResultNotify(notify);
    }
}
