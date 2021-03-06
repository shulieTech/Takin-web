package io.shulie.takin.cloud.biz.service.scene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.cloud.entity.domain.vo.report.SceneTaskNotifyParam;
import io.shulie.takin.adapter.api.model.common.RuleBean;
import io.shulie.takin.cloud.biz.cloudserver.SceneManageDTOConvert;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneSlaRefOutput;
import io.shulie.takin.cloud.biz.service.engine.EnginePluginFilesService;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOptions;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.constants.ScheduleEventConstant;
import io.shulie.takin.cloud.common.enums.scenemanage.TaskStatusEnum;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.common.utils.NumberUtil;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.ext.content.enginecall.BusinessActivityExt;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStartRequestExt;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStartRequestExt.SlaConfig;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStopRequestExt;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import io.shulie.takin.eventcenter.annotation.IntrestFor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author ??????
 * @date 2020-04-22
 */
@Component
@Slf4j
public class SceneTaskEventService {
    @Resource
    private CloudSceneTaskService cloudSceneTaskService;
    @Resource
    private CloudSceneManageService cloudSceneManageService;
    @Resource
    private EventCenterTemplate eventCenterTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private EnginePluginFilesService enginePluginFilesService;
    @Resource
    private AppConfig appConfig;

    @IntrestFor(event = "failed")
    public void failed(Event event) {
        log.info("???????????????????????????.....");
        Object object = event.getExt();
        TaskResult taskBean = (TaskResult)object;
        if (taskBean != null) {
            cloudSceneTaskService.handleSceneTaskEvent(taskBean);
        }
    }

    @IntrestFor(event = "started")
    public void started(Event event) {
        log.info("???????????????????????????.....");
        Object object = event.getExt();
        TaskResult taskBean = (TaskResult)object;
        if (taskBean != null) {
            cloudSceneTaskService.handleSceneTaskEvent(taskBean);
        }
    }

    /**
     * ????????????????????????
     *
     * @param scene    ??????
     * @param reportId ????????????
     */
    public void callStartEvent(SceneManageWrapperOutput scene, Long reportId) {
        Long sceneId = scene.getId();
        Long customerId = scene.getTenantId();
        ScheduleStartRequestExt scheduleStartRequest = new ScheduleStartRequestExt();
        scheduleStartRequest.setContinuedTime(scene.getTotalTestTime());
        scheduleStartRequest.setSceneId(sceneId);
        scheduleStartRequest.setTaskId(reportId);
        // ??????id
        scheduleStartRequest.setTenantId(customerId);

        scheduleStartRequest.setPressureScene(scene.getPressureType());
        scheduleStartRequest.setTotalIp(scene.getIpNum());
        scheduleStartRequest.setExpectThroughput(scene.getConcurrenceNum());
        scheduleStartRequest.setThreadGroupConfigMap(scene.getThreadGroupConfigMap());

        List<SceneSlaRefOutput> warningCondition = scene.getWarningCondition();
        if (CollectionUtils.isNotEmpty(warningCondition)) {
            scheduleStartRequest.setWarningCondition(
                warningCondition.stream().map(this::convertSla).flatMap(Collection::stream).collect(Collectors.toList()));
        }
        List<SceneSlaRefOutput> stopCondition = scene.getStopCondition();
        if (CollectionUtils.isNotEmpty(stopCondition)) {
            scheduleStartRequest.setStopCondition(
                stopCondition.stream().map(this::convertSla).flatMap(Collection::stream).collect(Collectors.toList()));
        }
        Map<String, BusinessActivityExt> businessData = Maps.newHashMap();
        Integer tps = CommonUtil.sum(scene.getBusinessActivityConfig(), SceneManageWrapperOutput.SceneBusinessActivityRefOutput::getTargetTPS);
        List<BusinessActivityExt> activities = CommonUtil.getList(scene.getBusinessActivityConfig(), SceneManageDTOConvert.INSTANCE::of);
        if (CollectionUtils.isNotEmpty(activities)) {
            for (BusinessActivityExt d : activities) {
                if (null != d.getTps()) {
                    d.setRate(NumberUtil.getRate(d.getTps(), tps));
                }
                businessData.put(d.getBindRef(), d);
            }
        }
        scheduleStartRequest.setBusinessData(businessData);
        scheduleStartRequest.setBindByXpathMd5(StringUtils.isNoneBlank(scene.getScriptAnalysisResult()));

        //?????????????????????????????????????????????????????????????????????????????????????????? modified by xr.l 20210712
        if (CollectionUtils.isNotEmpty(scene.getEnginePlugins())) {
            scheduleStartRequest.setEnginePluginsFilePath(enginePluginFilesService.findPluginFilesPathByPluginIdAndVersion(scene.getEnginePlugins()));
        } else {
            scheduleStartRequest.setEnginePluginsFilePath(Lists.newArrayList());
        }

        //??????????????????
        scheduleStartRequest.setLoopsNum(scene.getLoopsNum());
        scheduleStartRequest.setConcurrenceNum(scene.getConcurrenceNum());
        scheduleStartRequest.setFixedTimer(scene.getFixedTimer());
        scheduleStartRequest.setInspect(scene.isInspect());
        scheduleStartRequest.setTryRun(scene.isTryRun());

        List<ScheduleStartRequestExt.DataFile> dataFileList = new ArrayList<>();
        scene.getUploadFile().forEach(file -> {
            Integer fileType = file.getFileType();
            String path = file.getUploadPath();
            if (fileType == 0) {
                scheduleStartRequest.setScriptPath(appConfig.reWritePathByNfsRelative(path, fileType, false));
            } else {
                ScheduleStartRequestExt.DataFile dataFile = new ScheduleStartRequestExt.DataFile();
                dataFile.setName(file.getFileName());
                dataFile.setPath(appConfig.reWritePathByNfsRelative(path, fileType, false));
                dataFile.setSplit(file.getIsSplit() != null && file.getIsSplit() == 1);
                dataFile.setOrdered(file.getIsOrderSplit() != null && file.getIsOrderSplit() == 1);
                dataFile.setRefId(file.getId());
                dataFile.setFileType(fileType);
                dataFile.setBigFile(file.getIsBigFile() != null && file.getIsBigFile() == 1);
                dataFile.setFileMd5(file.getFileMd5());
                dataFileList.add(dataFile);
            }
        });
        scheduleStartRequest.setDataFile(dataFileList);
        scheduleStartRequest.setFileContinueRead(scene.isContinueRead());
        scheduleStartRequest.setResourceId(scene.getResourceId());
        Event event = new Event();
        event.setEventName(ScheduleEventConstant.START_SCHEDULE_EVENT);
        event.setExt(scheduleStartRequest);
        eventCenterTemplate.doEvents(event);
        log.info("??????[{}]??????????????????.....{}", scene, reportId);
    }

    /**
     * ????????????????????????
     *
     * @param reportResult ????????????
     */
    public void callStopEvent(ReportResult reportResult) {
        ScheduleStopRequestExt scheduleStopRequest = new ScheduleStopRequestExt();
        scheduleStopRequest.setSceneId(reportResult.getSceneId());
        scheduleStopRequest.setTaskId(reportResult.getId());
        scheduleStopRequest.setTenantId(reportResult.getTenantId());
        scheduleStopRequest.setPressureTaskId(reportResult.getTaskId());
        scheduleStopRequest.setJobId(reportResult.getJobId());
        scheduleStopRequest.setResourceId(reportResult.getResourceId());
        Event event = new Event();
        event.setEventName(ScheduleEventConstant.STOP_SCHEDULE_EVENT);
        event.setExt(scheduleStopRequest);
        eventCenterTemplate.doEvents(event);
        log.info("??????????????????[{}]????????????.....{}", reportResult.getSceneId(), reportResult.getId());
    }

    /**
     * ????????????
     *
     * @param param ??????
     */
    public String callStartResultEvent(SceneTaskNotifyParam param) {
        String index = "";
        if (param != null) {
            log.info("??????pod????????????:{}", param);
            Event event = new Event();
            TaskResult result = new TaskResult();
            result.setSceneId(param.getSceneId());
            result.setTaskId(param.getTaskId());
            result.setTenantId(param.getTenantId());
            result.setMsg(param.getMsg());

            boolean isNotify = true;
            if ("started".equals(param.getStatus())) {
                // ???????????? ????????????
                result.setStatus(TaskStatusEnum.STARTED);
                event.setEventName("started");
                // ????????????
                // ????????????
                Map<String, Object> extendMap = Maps.newHashMap();
                SceneManageQueryOptions options = new SceneManageQueryOptions();
                options.setIncludeBusinessActivity(true);
                SceneManageWrapperOutput dto = cloudSceneManageService.getSceneManage(param.getSceneId(), options);
                if (dto != null && CollectionUtils.isNotEmpty(dto.getBusinessActivityConfig())) {
                    extendMap.put("businessActivityCount", dto.getBusinessActivityConfig().size());
                    extendMap.put("businessActivityBindRef", dto.getBusinessActivityConfig().stream()
                        .map(SceneManageWrapperOutput.SceneBusinessActivityRefOutput::getBindRef)
                        .filter(StringUtils::isNoneBlank)
                        .map(String::trim).distinct().collect(Collectors.toList()));
                }
                result.setExtendMap(extendMap);
                String key = ScheduleConstants.getFileSplitQueue(param.getSceneId(), param.getTaskId(), param.getTenantId());
                index = stringRedisTemplate.opsForList().leftPop(key);
            } else if ("failed".equals(param.getStatus())) {
                result.setStatus(TaskStatusEnum.FAILED);
                event.setEventName("failed");
            } else {
                isNotify = false;
            }
            if (isNotify) {
                event.setExt(result);
                eventCenterTemplate.doEvents(event);
                log.info("??????????????????????????????????????????: {}", param);
            }
            log.info("pressureNode {}-{}-{}: Accept the start result ,pressureNode number :{}",
                param.getSceneId(), param.getTaskId(), param.getTenantId(), index);
        }
        return index;
    }

    private List<SlaConfig> convertSla(SceneSlaRefOutput sla) {
        String[] businessActivity = sla.getBusinessActivity();
        List<SlaConfig> configs = new ArrayList<>(businessActivity.length);
        SlaConfig base = new SlaConfig();
        base.setId(sla.getId());
        base.setRuleName(sla.getRuleName());
        base.setStatus(sla.getStatus());
        base.setEvent(sla.getEvent());
        RuleBean rule = sla.getRule();
        base.setIndexInfo(rule.getIndexInfo());
        base.setCondition(rule.getCondition());
        base.setDuring(rule.getDuring());
        base.setTimes(rule.getTimes());

        for (String activity : businessActivity) {
            SlaConfig config = base.clone();
            if (NO_EXE_NODE_REF.contains(activity)) {
                activity = "";
            }
            config.setActivity(activity);
            configs.add(config);
        }
        return configs;
    }

    private static final List<String> NO_EXE_NODE_REF = Arrays.asList("0f1a197a2040e645dcdb4dfff8a3f960", "all");
}
