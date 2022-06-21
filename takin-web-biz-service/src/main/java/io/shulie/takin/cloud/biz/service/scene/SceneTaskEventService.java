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
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneScriptRefOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneSlaRefOutput;
import io.shulie.takin.cloud.biz.service.engine.EnginePluginFilesService;
import io.shulie.takin.cloud.biz.service.schedule.impl.FileSplitService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 莫问
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
    @Value("${data.path}")
    private String nfsDir;
    @Value("${script.path}")
    private String scriptPath;

    @IntrestFor(event = "failed")
    public void failed(Event event) {
        log.info("监听到启动失败事件.....");
        Object object = event.getExt();
        TaskResult taskBean = (TaskResult)object;
        if (taskBean != null) {
            cloudSceneTaskService.handleSceneTaskEvent(taskBean);
        }
    }

    @IntrestFor(event = "started")
    public void started(Event event) {
        log.info("监听到启动成功事件.....");
        Object object = event.getExt();
        TaskResult taskBean = (TaskResult)object;
        if (taskBean != null) {
            cloudSceneTaskService.handleSceneTaskEvent(taskBean);
        }
    }

    /**
     * 场景任务启动事件
     *
     * @param scene    场景
     * @param reportId 报告主键
     */
    public void callStartEvent(SceneManageWrapperOutput scene, Long reportId) {
        Long sceneId = scene.getId();
        Long customerId = scene.getTenantId();
        ScheduleStartRequestExt scheduleStartRequest = new ScheduleStartRequestExt();
        scheduleStartRequest.setContinuedTime(scene.getTotalTestTime());
        scheduleStartRequest.setSceneId(sceneId);
        scheduleStartRequest.setTaskId(reportId);
        // 客户id
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

        //一个插件可能会有多个版本，需要根据版本号来获取相应的文件路径 modified by xr.l 20210712
        if (CollectionUtils.isNotEmpty(scene.getEnginePlugins())) {
            scheduleStartRequest.setEnginePluginsFilePath(enginePluginFilesService.findPluginFilesPathByPluginIdAndVersion(scene.getEnginePlugins()));
        } else {
            scheduleStartRequest.setEnginePluginsFilePath(Lists.newArrayList());
        }

        //添加巡检参数
        scheduleStartRequest.setLoopsNum(scene.getLoopsNum());
        scheduleStartRequest.setConcurrenceNum(scene.getConcurrenceNum());
        scheduleStartRequest.setFixedTimer(scene.getFixedTimer());
        scheduleStartRequest.setInspect(scene.isInspect());
        scheduleStartRequest.setTryRun(scene.isTryRun());

        List<ScheduleStartRequestExt.DataFile> dataFileList = new ArrayList<>();
        scene.getUploadFile().forEach(file -> {
            if (file.getFileType() == 0) {
                scheduleStartRequest.setScriptPath(reWritePathIfNecessary(file));
            } else {
                ScheduleStartRequestExt.DataFile dataFile = new ScheduleStartRequestExt.DataFile();
                dataFile.setName(file.getFileName());
                dataFile.setPath(reWritePathIfNecessary(file));
                dataFile.setSplit(file.getIsSplit() != null && file.getIsSplit() == 1);
                dataFile.setOrdered(file.getIsOrderSplit() != null && file.getIsOrderSplit() == 1);
                dataFile.setRefId(file.getId());
                dataFile.setFileType(file.getFileType());
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
        log.info("场景[{}]任务启动事件.....{}", scene, reportId);
    }

    /**
     * 停止场景压测任务
     *
     * @param reportResult 报告结果
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
        log.info("主动停止场景[{}]任务事件.....{}", reportResult.getSceneId(), reportResult.getId());
    }

    /**
     * 启动结果
     *
     * @param param 参数
     */
    public String callStartResultEvent(SceneTaskNotifyParam param) {
        String index = "";
        if (param != null) {
            log.info("收到pod通知参数:{}", param);
            Event event = new Event();
            TaskResult result = new TaskResult();
            result.setSceneId(param.getSceneId());
            result.setTaskId(param.getTaskId());
            result.setTenantId(param.getTenantId());
            result.setMsg(param.getMsg());

            boolean isNotify = true;
            if ("started".equals(param.getStatus())) {
                // 压力节点 启动成功
                result.setStatus(TaskStatusEnum.STARTED);
                event.setEventName("started");
                // 扩展配置
                // 扩展配置
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
                log.info("成功处理压力引擎节点通知事件: {}", param);
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

    private String reWritePathIfNecessary(SceneScriptRefOutput refOutput) {
        FileSplitService.reWriteAttachmentSceneScriptRefOutput(refOutput);
        String prefix = scriptPath.replaceAll(nfsDir, "");
        if (StringUtils.isBlank(prefix)) {
            return refOutput.getUploadPath();
        }
        if (prefix.startsWith("/")) {
            prefix = prefix.substring(1);
        }
        if (!prefix.endsWith("/")) {
            prefix += "/";
        }
        return prefix + refOutput.getUploadPath();
    }
}
