package io.shulie.takin.web.biz.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import io.shulie.takin.adapter.api.entrypoint.scene.manage.CloudSceneManageApi;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators.ResourceContext;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartInput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneManageService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneTaskService;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOptions;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.ext.content.enums.AssetTypeEnum;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckResult;
import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckStatus;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CompositeStartConditionChecker implements InitializingBean {

    @Resource
    private RedisClientUtil redisClientUtil;

    @Resource
    private CloudSceneManageApi cloudSceneManageApi;

    @Resource
    private CloudSceneManageService cloudSceneManageService;

    @Resource
    private CloudSceneTaskService cloudSceneTaskService;

    @Resource
    private List<StartConditionChecker> checkerList;

    @Resource
    private EventCenterTemplate eventCenterTemplate;

    // 此处特殊使用
    public List<CheckResult> doCheck(StartConditionCheckerContext context) {
        initContext(context);
        initTaskAndReportIfNecessary(context);
        List<CheckResult> resultList = new ArrayList<>(checkerList.size());
        for (StartConditionChecker checker : checkerList) {
            CheckResult checkResult = doCheck(context, checker);
            resultList.add(checkResult);
            if (checkResult.getStatus().equals(CheckStatus.FAIL.ordinal())) {
                context.setMessage(checkResult.getMessage());
                callCheckFailEvent(context);
                break;
            }
        }
        return resultList;
    }

    private CheckResult doCheck(StartConditionCheckerContext context, StartConditionChecker checker) {
        String preStopKey = PressureStartCache.getScenePreStopKey(context.getSceneId(), WebPluginUtils.traceUserId());
        String preStopTime = redisClientUtil.getString(preStopKey);
        if (StringUtils.isNotBlank(preStopTime)) {
            if (Long.parseLong(preStopTime) > context.getTime()) {
                redisClientUtil.delete(preStopKey);
                return new CheckResult(checker.type(), CheckStatus.FAIL.ordinal(), "取消压测");
            }
        }
        return checker.check(context);
    }

    private void initContext(StartConditionCheckerContext context) {
        Long sceneId = context.getSceneId();
        if (context.getSceneData() == null) {
            SceneManageQueryOptions options = new SceneManageQueryOptions();
            options.setIncludeBusinessActivity(true);
            options.setIncludeScript(true);
            context.setSceneData(cloudSceneManageService.getSceneManage(sceneId, options));
        }

        SceneTaskStartInput input = new SceneTaskStartInput();
        input.setOperateId(WebPluginUtils.traceUserId());
        input.setAssetType(AssetTypeEnum.PRESS_REPORT.getCode());
        context.setInput(input);

        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(sceneId);
        context.setSceneDataDTO(JsonHelper.json2Bean(JsonHelper.bean2Json(cloudSceneManageApi.getSceneDetail(req)),
            SceneManageWrapperDTO.class));
    }

    private void initTaskAndReportIfNecessary(StartConditionCheckerContext context) {
        String resourceId = context.getResourceId();
        if (StringUtils.isBlank(resourceId)) {
            SceneManageWrapperOutput sceneData = context.getSceneData();
            SceneTaskStartInput input = context.getInput();
            PressureTaskEntity pressureTask = cloudSceneTaskService.initPressureTask(sceneData, input);
            ReportEntity report = cloudSceneTaskService.initReport(sceneData, input, pressureTask);
            context.setTaskId(pressureTask.getId());
            context.setReportId(report.getId());
        } else {
            completeByCache(context);
        }
        context.setInitTaskAndReport(true);
    }

    private void completeByCache(StartConditionCheckerContext context) {
        String resourceId = context.getResourceId();
        Object taskId = redisClientUtil.hmget(PressureStartCache.getResourceKey(resourceId), PressureStartCache.TASK_ID);
        if (Objects.nonNull(taskId)) {
            context.setTaskId(Long.valueOf(String.valueOf(taskId)));
        }
        Object reportId = redisClientUtil.hmget(PressureStartCache.getResourceKey(resourceId), PressureStartCache.REPORT_ID);
        if (Objects.nonNull(reportId)) {
            context.setReportId(Long.valueOf(String.valueOf(reportId)));
        }
        Object uniqueKey = redisClientUtil.hmget(PressureStartCache.getResourceKey(resourceId), PressureStartCache.UNIQUE_KEY);
        if (Objects.nonNull(reportId)) {
            context.setUniqueKey(String.valueOf(uniqueKey));
        }
    }

    private void callCheckFailEvent(StartConditionCheckerContext context) {
        Event event = new Event();
        event.setEventName(PressureStartCache.CHECK_FAIL_EVENT);
        ResourceContext resourceContext = new ResourceContext();
        resourceContext.setSceneId(context.getSceneId());
        resourceContext.setTaskId(context.getTaskId());
        resourceContext.setReportId(context.getReportId());
        resourceContext.setResourceId(context.getResourceId());
        resourceContext.setMessage(context.getMessage());
        resourceContext.setUniqueKey(context.getUniqueKey());
        event.setExt(resourceContext);
        eventCenterTemplate.doEvents(event);
    }

    @Override
    public void afterPropertiesSet() {
        if (checkerList == null) {
            checkerList = new ArrayList<>(0);
        }
        checkerList.sort((it1, it2) -> {
            int i1 = it1.getOrder();
            int i2 = it2.getOrder();
            return Integer.compare(i1, i2);
        });
    }
}
