package io.shulie.takin.web.biz.checker;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.google.common.collect.Lists;
import com.pamirs.takin.common.constant.AppSwitchEnum;
import com.pamirs.takin.common.constant.ConfigConstants;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneBusinessActivityRefDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.BaseConfigService;
import io.shulie.takin.web.biz.service.scenemanage.SceneTaskService;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.common.vo.scene.BaffleAppVO;
import io.shulie.takin.web.data.dao.SceneExcludedApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationChecker implements StartConditionChecker {

    @Resource
    private ApplicationDAO applicationDAO;

    @Resource
    private BaseConfigService baseConfigService;

    @Resource
    private SceneExcludedApplicationDAO sceneExcludedApplicationDAO;

    @Resource
    private SceneTaskService sceneTaskService;

    @Resource
    private ApplicationService applicationService;

    @Resource
    private RedisClientUtil redisClientUtil;

    @Override
    public CheckResult check(StartConditionCheckerContext context) {
        try {
            doCheck(context);
            return CheckResult.success(type());
        } catch (Exception e) {
            return CheckResult.fail(type(), e.getMessage());
        }
    }

    private void doCheck(StartConditionCheckerContext context) {
        this.checkStatus(context);
        this.checkSwitch();
        if (StringUtils.isBlank(context.getResourceId())) {
            this.checkBusinessActivity(context);
        }
    }

    private void checkSwitch() {
        //?????????????????????????????????????????????
        if (applicationService.silenceSwitchStatusIsTrue(WebPluginUtils.traceTenantCommonExt(), AppSwitchEnum.CLOSED)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_VALIDATE_ERROR, "???????????????????????????????????????????????????");
        }
        String switchStatus = applicationService.getUserSwitchStatusForVo();
        if (!AppSwitchEnum.OPENED.getCode().equals(switchStatus)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_STATUS_ERROR, "?????????????????????????????????????????????");
        }
    }

    private void checkStatus(StartConditionCheckerContext context) {
        SceneManageWrapperOutput sceneData = context.getSceneData();
        Long sceneId = context.getSceneId();
        if (Objects.equals(SceneManageStatusEnum.RECYCLE.getValue(), sceneData.getStatus())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "?????????????????????!");
        }
        boolean flag = false;
        if (!SceneManageStatusEnum.ifFree(sceneData.getStatus())) {
            Object uniqueKey = redisClientUtil.hmget(PressureStartCache.getSceneResourceKey(sceneId), PressureStartCache.UNIQUE_KEY);
            flag = !Objects.equals(uniqueKey, context.getUniqueKey());
        }
        flag = flag || pressureRunning(context);
        if (flag) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "?????????????????????????????????!");
        }
        if (StringUtils.isBlank(context.getResourceId())) {
            cacheAssociation(context);
        }
    }

    private boolean pressureRunning(StartConditionCheckerContext context) {
        String sceneRunningKey = PressureStartCache.getSceneResourceLockingKey(context.getSceneId());
        String resourceId = context.getResourceId();
        if (StringUtils.isNotBlank(resourceId)
            && redisClientUtil.hasKey(RedisClientUtil.getLockKey(PressureStartCache.getStopFlag(resourceId)))) {
            // ??????????????????????????????
            return false;
        }
        boolean lockSuccess = redisClientUtil.reentryLockNoExpire(sceneRunningKey, context.getUniqueKey());
        if (lockSuccess) {
            redisClientUtil.expire(sceneRunningKey, 90);
        }
        return !lockSuccess;
    }

    private void cacheAssociation(StartConditionCheckerContext context) {
        Map<String, Object> param = new HashMap<>(4);
        param.put(PressureStartCache.REPORT_ID, context.getReportId());
        param.put(PressureStartCache.TASK_ID, context.getTaskId());
        param.put(PressureStartCache.UNIQUE_KEY, context.getUniqueKey());
        redisClientUtil.hmset(PressureStartCache.getSceneResourceKey(context.getSceneId()), param);
    }

    /**
     * ????????????????????????
     *
     * @param context ???????????????
     */
    private void checkBusinessActivity(StartConditionCheckerContext context) {
        SceneManageWrapperDTO sceneData = context.getSceneDataDTO();
        //??????????????????????????????????????????
        List<SceneBusinessActivityRefDTO> activityConfig = sceneData.getBusinessActivityConfig();
        Long sceneId = sceneData.getId();
        if (CollectionUtils.isEmpty(activityConfig)) {
            log.error("[{}]??????????????????????????????", sceneId);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_VALIDATE_ERROR,
                "??????????????????????????????????????????????????????ID???" + sceneId);
        }
        //???????????????????????????????????????????????????
        StringBuilder errorMsg = new StringBuilder();

        // ?????????????????????????????????ids
        List<Long> applicationIds = this.listApplicationIdsFromScene(sceneId, activityConfig);

        // ??????????????????
        boolean checkApplication = ConfigServerHelper.getBooleanValueByKey(
            ConfigServerKeyEnum.TAKIN_START_TASK_CHECK_APPLICATION);
        if (!CollectionUtils.isEmpty(applicationIds) && checkApplication) {
            List<ApplicationDetailResult> applicationMntList = applicationDAO.getApplicationByIds(applicationIds);
            // todo ?????????????????????????????????
            TBaseConfig config = baseConfigService.queryByConfigCode(ConfigConstants.SCENE_BAFFLE_APP_CONFIG);
            if (config != null && StringUtils.isNotBlank(config.getConfigValue())) {
                try {
                    List<BaffleAppVO> baffleAppVos = JsonHelper.json2List(config.getConfigValue(), BaffleAppVO.class);
                    List<String> appNames = Optional.of(baffleAppVos.stream()
                            .filter(appVO -> sceneId != null && sceneId.equals(appVO.getSceneId()))
                            .collect(Collectors.toList()))
                        .map(t -> t.get(0)).map(BaffleAppVO::getAppName).orElse(Lists.newArrayList());
                    List<Long> appIds = Lists.newArrayList();

                    List<ApplicationDetailResult> tempApps = applicationMntList.stream().filter(app -> {
                        if (appNames.contains(app.getApplicationName())) {
                            // ??????????????????id
                            appIds.add(app.getApplicationId());
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    List<Long> tempAppIds = applicationIds.stream().filter(id -> !appIds.contains(id))
                        .collect(Collectors.toList());
                    applicationMntList = tempApps;
                    applicationIds = tempAppIds;
                } catch (Exception e) {
                    log.error("?????????????????????????????????????????????{},???????????????:{}", ConfigConstants.SCENE_BAFFLE_APP_CONFIG,
                        config.getConfigValue());
                }
            }
            if (CollectionUtils.isEmpty(applicationMntList) || applicationMntList.size() != applicationIds.size()) {
                log.error("??????????????????, ??????????????????????????????????????????ID???{}", sceneId);
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_VALIDATE_ERROR,
                    "??????????????????, ??????????????????????????????????????????ID???" + sceneId);
            }

            // ??????????????????
            errorMsg.append(sceneTaskService.checkApplicationCorrelation(applicationMntList));
        }

        if (errorMsg.length() > 0) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_VALIDATE_ERROR, errorMsg.toString());
        }
    }

    /**
     * ????????????
     *
     * @param sceneId ??????id
     * @param refList ??????????????????????????????
     * @return ??????ids
     */
    private List<Long> listApplicationIdsFromScene(Long sceneId, List<SceneBusinessActivityRefDTO> refList) {
        // ????????????????????????ID???????????????ID
        List<Long> applicationIds = refList.stream()
            .map(SceneBusinessActivityRefDTO::getApplicationIds).filter(StringUtils::isNotEmpty)
            .flatMap(appIds -> Arrays.stream(appIds.split(","))
                .map(Long::valueOf)).filter(data -> data > 0L).distinct().collect(Collectors.toList());
        if (applicationIds.isEmpty()) {
            return Collections.emptyList();
        }

        // ????????????????????????id
        List<Long> excludedApplicationIds = sceneExcludedApplicationDAO.listApplicationIdsBySceneId(sceneId);
        if (excludedApplicationIds.isEmpty()) {
            return applicationIds;
        }

        // ??????????????????id
        applicationIds.removeAll(excludedApplicationIds);
        return applicationIds;
    }

    @Override
    public String type() {
        return "application";
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
