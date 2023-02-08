package io.shulie.takin.cloud.biz.service.schedule.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import com.pamirs.takin.cloud.entity.dao.schedule.TScheduleRecordMapper;
import com.pamirs.takin.cloud.entity.domain.entity.schedule.ScheduleRecord;
import com.pamirs.takin.cloud.entity.domain.vo.report.SceneTaskNotifyParam;
import com.pamirs.takin.cloud.entity.domain.vo.scenemanage.SceneManageStartRecordVO;
import com.pamirs.takin.entity.domain.dto.report.BusinessActivitySummaryDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportApplicationDTO;
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
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.FileTypeEnum;
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
import io.shulie.takin.cloud.model.request.job.pressure.StartRequest;
import io.shulie.takin.cloud.model.request.job.pressure.StartRequest.FileInfo;
import io.shulie.takin.cloud.model.request.job.pressure.StartRequest.FileInfo.SplitInfo;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.service.report.impl.ReportApplicationService;
import io.shulie.takin.web.common.util.BeanCopyUtils;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author 莫问
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
    @Resource
    private ReportApplicationService reportApplicationService;
    @Resource
    private ApplicationDAO applicationDAO;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void startSchedule(ScheduleStartRequestExt request) {
        log.info("启动调度, 请求数据：{}", request);
        //任务只处理一次
        ScheduleRecord schedule = tScheduleRecordMapper.getScheduleByTaskId(request.getTaskId());
        if (schedule != null) {
            log.error("异常代码【{}】,异常内容：启动调度失败 --> 调度任务[{}]已经启动",
                    TakinCloudExceptionEnum.SCHEDULE_START_ERROR, request.getTaskId());
            return;
        }
        //获取策略
        StrategyConfigExt config = strategyConfigService.getCurrentStrategyConfig();
        if (config == null) {
            log.error("异常代码【{}】,异常内容：启动调度失败 --> 调度策略未配置",
                    TakinCloudExceptionEnum.SCHEDULE_START_ERROR);
            return;
        }

        String scheduleName = ScheduleConstants.getScheduleName(request.getSceneId(), request.getTaskId(), request.getTenantId());

        //保存调度记录
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

        //add by 李鹏 保存调度对应压测引擎插件记录信息
        scheduleRecordEnginePluginService.saveScheduleRecordEnginePlugins(
                scheduleRecord.getId(), request.getEnginePluginsFilePath());
        //add end

        //发布事件
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
        //把数据放入缓存，初始化回调调度需要
        stringRedisTemplate.opsForValue().set(scheduleName, JSON.toJSONString(eventRequest));
        // 需要将 本次调度 pod数量存入redis,报告中用到
        // 总计 报告生成用到 调度期间出现错误，这份数据只存24小时
        stringRedisTemplate.opsForValue().set(
                ScheduleConstants.getPressureNodeTotalKey(request.getSceneId(), request.getTaskId(), request.getTenantId()),
                String.valueOf(request.getTotalIp()), 1, TimeUnit.DAYS);
        //调度初始化
        scheduleEvent.initSchedule(eventRequest);
    }

    @Override
    public void stopSchedule(ScheduleStopRequestExt request) {
        log.info("停止调度, 请求数据：{}", request);
        Long sceneId = request.getSceneId();
        String resourceId = request.getResourceId();
        if (StringUtils.isNotBlank(resourceId)) {
            String stopTaskMessageKey = PressureStartCache.getStopTaskMessageKey(sceneId);
            String stopTaskMessage = redisClientUtil.getString(stopTaskMessageKey);
            if (Objects.isNull(stopTaskMessage)) {
                stopTaskMessage = "停止调度";
            }
            if (redisClientUtil.hasLockKey(PressureStartCache.getJmeterStartFirstKey(resourceId))) {
                callRunningFailedEvent(resourceId, stopTaskMessage);
            } else {
                callStartFailedEvent(resourceId, stopTaskMessage);
            }
        } else {
            // 直接标记场景为停止状态,报告为完成状态
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
        // 压力机数目记录
        push(startRequest);

        Long sceneId = startRequest.getSceneId();
        Long taskId = startRequest.getTaskId();
        Long customerId = startRequest.getTenantId();

        // 场景生命周期更新 启动中(文件拆分完成) ---> 创建Job中
        cloudSceneManageService.updateSceneLifeCycle(
                UpdateStatusBean.build(sceneId, taskId, customerId)
                        .checkEnum(SceneManageStatusEnum.STARTING, SceneManageStatusEnum.FILE_SPLIT_END)
                        .updateEnum(SceneManageStatusEnum.JOB_CREATING)
                        .build());

        request.setCallbackUrl(appConfig.getCallbackUrl());
        ResourceContext context = getResourceContext(request.getRequest().getResourceId());
        String attachId = context.getAttachId();
        request.getRequest().setAttachId(attachId);
        request.getRequest().setFileMapping(redisClientUtil.hasKey(PressureStartCache.getScriptMappingKey(attachId)));

        try {
            PressureTaskStartReq req = buildStartReq(request, context);
            notifyTaskResult(request);
            String resourceId = String.valueOf(req.getResourceId());
            if (!redisClientUtil.hasLockKey(PressureStartCache.getStopFlag(resourceId))) {
                Long jobId = pressureTaskApi.start(req);
                redisClientUtil.lockNoExpire(PressureStartCache.getStartFlag(resourceId), String.valueOf(jobId));
                log.info("场景{},任务{},顾客{}开始启动压测， 压测启动成功", sceneId, taskId, customerId);
                updateReportAssociation(startRequest, jobId);
                ResourceContext resourceContext = getResourceContext(resourceId);
                updateReportPtlLocation(resourceContext);
                cloudAsyncService.checkJmeterStartedTask(resourceContext);
            }
        } catch (Exception e) {
            // 创建失败
            log.info("场景{},任务{},顾客{}开始启动压测，压测启动失败", sceneId, taskId, customerId);
            cloudSceneManageService.reportRecord(SceneManageStartRecordVO.build(sceneId, taskId, customerId).success(false)
                    .errorMsg("压测启动创建失败，失败原因：" + e.getMessage()).build());
        } finally {
            redisClientUtil.del(PressureStartCache.getScriptMappingKey(startRequest.getAttachId()));
        }
    }

    @Override
    public void initScheduleCallback(ScheduleInitParamExt param) {

    }

    /**
     * 临时方案：
     * 拆分文件的索引都存入到redis队列, 避免控制台集群环境下索引获取不正确
     */
    private void push(ScheduleStartRequestExt request) {
        //把数据放入队列
        String key = ScheduleConstants.getFileSplitQueue(request.getSceneId(), request.getTaskId(), request.getTenantId());
        // 生成集合
        List<String> numList = IntStream.rangeClosed(1, request.getTotalIp())
                .boxed().map(String::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));
        // 集合放入Redis
        stringRedisTemplate.opsForList().leftPushAll(key, numList);
    }

    public PressureTaskStartReq buildStartReq(ScheduleRunRequest runRequest, ResourceContext context) {
        ScheduleStartRequestExt request = runRequest.getRequest();
        PressureTaskStartReq req = new PressureTaskStartReq();
        req.setCallbackUrl(runRequest.getCallbackUrl());
        req.setResourceId(Long.valueOf(request.getResourceId()));
        req.setJvmOptions(runRequest.getMemSetting());
        req.setType(JobType.of(request.getPressureScene()));
        req.setName(String.format("%s-%s-%s-%s", request.getSceneId(), context.getReportId(), context.getTenantId(), context.getEnvCode()));
        req.setSampling(runRequest.getTraceSampling());
        req.setBindByXpathMd5(request.getBindByXpathMd5());
        req.setThreadConfig(buildThreadGroup(request));

        ReportApplicationDTO reportApplication = reportApplicationService.getReportApplication(context.getReportId());
        completedSla(req, request, reportApplication);
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

    private void completedSla(PressureTaskStartReq req, ScheduleStartRequestExt request, ReportApplicationDTO reportApplication) {
        List<SlaConfig> conditionList = new ArrayList<>();
        List<SlaConfig> stopCondition = request.getStopCondition();
        if (!CollectionUtils.isEmpty(stopCondition)) {
            conditionList.addAll(stopCondition);
        }
        List<SlaConfig> waringCondition = request.getWarningCondition();
        if (!CollectionUtils.isEmpty(waringCondition)) {
            conditionList.addAll(waringCondition);
        }
        List<SlaInfo> slaConfigs = new ArrayList<>(conditionList.size());
        req.setSlaConfig(slaConfigs);
        if (!CollectionUtils.isEmpty(conditionList)) {
            conditionList.forEach(condition -> {
                slaConfigs.addAll(convertSlaConfig(condition, reportApplication));
            });
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

    private void completedFile(PressureTaskStartReq req, ScheduleStartRequestExt requestExt) {
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
            fileMappingIfNecessary(requestExt, req.getDataFile(), FileTypeEnum.DATA);
            fileMappingIfNecessary(requestExt, req.getDependencyFile(), FileTypeEnum.ATTACHMENT);
        }
        fileMappingIfNecessary(requestExt, Collections.singletonList(script), FileTypeEnum.SCRIPT);
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

    private List<SlaInfo> convertSlaConfig(SlaConfig config, ReportApplicationDTO reportApplication) {
        List<SlaInfo> all = new ArrayList<>();
        List<BusinessActivitySummaryDTO> businessActivitySummaryDTOS = reportApplication.getReportDetail().getBusinessActivity();
        //如果报告没有关联的业务活动，不会有sla
        if (CollectionUtil.isEmpty(businessActivitySummaryDTOS)){
            return all;
        }
        SlaInfo info = new SlaInfo();
        info.setRef(config.getActivity());
        info.setAttach(String.valueOf(config.getId()));
        info.setFormulaTarget(FormulaTarget.of(config.getIndexInfo()));
        info.setFormulaSymbol(FormulaSymbol.ofValue(config.getCondition()));
        info.setFormulaNumber(config.getDuring().doubleValue());
        info.setTimes(config.getTimes());
        info.setEvent(config.getEvent());

        if (StringUtils.isBlank(config.getActivity())) {
            businessActivitySummaryDTOS.forEach(businessActivitySummaryDTO -> {
                SlaInfo businessSla = new SlaInfo();
                BeanUtils.copyProperties(info, businessSla);
                businessSla.setRef(businessActivitySummaryDTO.getBindRef());
                all.add(businessSla);
            });
        } else {
            all.add(info);
        }

        all.forEach(sla -> {
            if (!CollectionUtils.isEmpty(reportApplication.getApplicationNames()) && (FormulaTarget.CPU_USAGE.getCode().equals(config.getIndexInfo())
                    || FormulaTarget.MEMORY_USAGE.getCode().equals(config.getIndexInfo()))) {
                //匹配请求和对应的应用
                if (!CollectionUtils.isEmpty(businessActivitySummaryDTOS)) {
                    List<BusinessActivitySummaryDTO> activitySummaryDTOS = businessActivitySummaryDTOS.stream().filter(o ->
                            o.getBindRef().equals(config.getActivity())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(activitySummaryDTOS)) {
                        String applicationIds = activitySummaryDTOS.get(0).getApplicationIds();
                        List<Long> splitApplicationIds = splitApplicationIds(applicationIds);
                        if (!CollectionUtils.isEmpty(splitApplicationIds)) {
                            List<ApplicationDetailResult> appsList = applicationDAO.getApplicationByIds(splitApplicationIds);
                            if (!CollectionUtils.isEmpty(appsList)) {
                                List<String> stringList = appsList.stream().map(ApplicationDetailResult::getApplicationName)
                                        .filter(StringUtils::isNoneBlank).distinct().collect(Collectors.toList());
                                sla.setAppNames(JsonHelper.bean2Json(stringList));
                            }
                        }
                    }
                }
            }
        });

        return all;
    }

    private static List<Long> splitApplicationIds(String applicationIds) {
        if (StringUtils.isBlank(applicationIds)) {
            return Collections.EMPTY_LIST;
        }
        String[] args = applicationIds.split(",");
        List<Long> dataList = Lists.newArrayList();
        for (String arg : args) {
            dataList.add(Long.parseLong(arg));
        }
        return dataList;
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

    // 处理文件路径映射
    private void fileMappingIfNecessary(ScheduleStartRequestExt requestExt, List<FileInfo> dataFile, FileTypeEnum data) {
        if (requestExt.isFileMapping() && !CollectionUtils.isEmpty(dataFile)) {
            String mappingKey = PressureStartCache.getScriptMappingKey(requestExt.getAttachId());
            List<String> filePath = (List<String>) redisClientUtil.hmget(mappingKey, String.valueOf(data.getCode()));
            Map<String, String> fileMap = filePath.stream().collect(
                    Collectors.toMap(path -> path.substring(path.lastIndexOf(SceneManageConstant.FILE_SPLIT)), Function.identity()));
            dataFile.forEach(file -> {
                String uri = file.getUri();
                // 文件名称
                String fileName = uri.substring(uri.lastIndexOf(SceneManageConstant.FILE_SPLIT));
                // 映射的路径
                String mapPath = fileMap.get(fileName);
                // 如果没有映射就不做变更
                if (StringUtils.isNotBlank(mapPath)) {
                    file.setUri(mapPath);
                }
            });
        }
    }
}
