package io.shulie.takin.cloud.biz.service.sla.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.pamirs.takin.cloud.entity.dao.report.TReportMapper;
import com.pamirs.takin.cloud.entity.dao.scene.manage.TSceneBusinessActivityRefMapper;
import com.pamirs.takin.cloud.entity.dao.scene.manage.TWarnDetailMapper;
import com.pamirs.takin.cloud.entity.domain.entity.report.Report;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneBusinessActivityRef;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneSlaRef;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.WarnDetail;
import io.shulie.takin.adapter.api.entrypoint.pressure.PressureTaskApi;
import io.shulie.takin.adapter.api.model.common.SlaBean;
import io.shulie.takin.cloud.biz.cloudserver.SceneManageDTOConvert;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.input.report.UpdateReportSlaDataInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneSlaRefInput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneBusinessActivityRefOutput;
import io.shulie.takin.cloud.biz.service.report.CloudReportService;
import io.shulie.takin.cloud.biz.service.sla.SlaService;
import io.shulie.takin.cloud.biz.utils.SlaUtil;
import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;
import io.shulie.takin.cloud.common.bean.sla.AchieveModel;
import io.shulie.takin.cloud.common.constants.Constants;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.data.mapper.mysql.SceneSlaRefMapper;
import io.shulie.takin.cloud.data.model.mysql.SceneSlaRefEntity;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStopRequestExt;
import io.shulie.takin.cloud.model.callback.Sla;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author qianshui
 * @date 2020/4/20 ??????4:48
 */
@Service
@Slf4j
public class SlaServiceImpl extends AbstractIndicators implements SlaService {

    public static final Long EXPIRE_TIME = 24 * 3600L;
    public static final String SLA_DESTROY_KEY = "TAKIN:SLA:DESTROY:KEY";

    @Resource
    private CloudReportService cloudReportService;
    @Resource
    private TWarnDetailMapper tWarnDetailMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private PressureTaskApi pressureTaskApi;
    @Resource
    private SceneSlaRefMapper sceneSlaRefMapper;
    @Resource
    private TReportMapper tReportMapper;
    @Resource
    private TSceneBusinessActivityRefMapper tSceneBusinessActivityRefMapper;

    @Override
    public void detection(List<Sla.SlaInfo> slaInfo) {
        Set<Object> keys = stringRedisTemplate.opsForHash().keys(SLA_DESTROY_KEY);
        List keyList = new ArrayList(keys);
        for (Sla.SlaInfo info : slaInfo) {
            Report report = tReportMapper.getReportByTaskId(info.getJobId());
            String ref = info.getRef();
            if (org.apache.commons.lang3.StringUtils.isNoneBlank(ref)) {
                String id = info.getAttach();
                String bindRef = info.getRef();
                SceneBusinessActivityRef activityRef = tSceneBusinessActivityRefMapper.selectByBindRef(bindRef, report.getSceneId());
                if (Objects.isNull(activityRef)) {
                    continue;
                }
                SceneSlaRefEntity slaRef = sceneSlaRefMapper.selectById(id);
                SceneSlaRef sceneSlaRef = new SceneSlaRef();
                BeanUtils.copyProperties(slaRef, sceneSlaRef);
                SceneManageWrapperOutput.SceneSlaRefOutput output = SceneManageDTOConvert.INSTANCE.of(sceneSlaRef);
                String event = output.getEvent();
                SceneSlaRefInput input = BeanUtil.copyProperties(output, SceneSlaRefInput.class);
                SendMetricsEvent sendMetricsEvent = new SendMetricsEvent();
                Map<String, Object> conditionMap = SlaUtil.matchCondition(input, sendMetricsEvent);
                conditionMap.put("real", info.getNumber());

                SceneBusinessActivityRefOutput businessActivity = new SceneBusinessActivityRefOutput();
                businessActivity.setBindRef(bindRef);
                businessActivity.setBusinessActivityId(activityRef.getBusinessActivityId());
                businessActivity.setBusinessActivityName(activityRef.getBusinessActivityName());


                //???redis?????????????????????
                String object = (String) stringRedisTemplate.opsForHash().get(SLA_DESTROY_KEY, id);
                AchieveModel model = (object != null ? JSON.parseObject(object, AchieveModel.class) : null);
                if (!matchContinue(model, System.currentTimeMillis(), output.getRule().getTimes() == 1)) {//??????????????????????????????????????????
                    Map<String, Object> dataMap = Maps.newHashMap();
                    dataMap.put(id,
                            JSON.toJSONString(new AchieveModel(1, System.currentTimeMillis())));
                    stringRedisTemplate.opsForHash().putAll(SLA_DESTROY_KEY, dataMap);
                    stringRedisTemplate.expire(SLA_DESTROY_KEY, EXPIRE_TIME, TimeUnit.SECONDS);
                    continue;
                }
                if (model == null) model = new AchieveModel();
                model.setTimes(model.getTimes() + 1);
                model.setLastAchieveTime(System.currentTimeMillis());
                if (model.getTimes() >= output.getRule().getTimes()) {//??????????????????????????????
                    try {
                        ScheduleStopRequestExt scheduleStopRequest = new ScheduleStopRequestExt();
                        scheduleStopRequest.setTaskId(info.getJobId());
                        scheduleStopRequest.setSceneId(slaRef.getSceneId());
                        // ????????????id
//                    scheduleStopRequest.setTenantId();
                        Map<String, Object> extendMap = new HashMap<>(1);
                        extendMap.put(Constants.SLA_DESTROY_EXTEND, "SLA??????????????????????????????");
                        scheduleStopRequest.setExtend(extendMap);
                        //???????????????????????????
                        String resourceId = String.valueOf(info.getResourceId());
                        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(PressureStartCache.getResourceKey(resourceId)))) {
                            // ???????????????????????????????????????
                            SendMetricsEvent metricsEvent = new SendMetricsEvent();
                            metricsEvent.setReportId(report.getId());
                            metricsEvent.setTimestamp(System.currentTimeMillis());
                            WarnDetail warnDetail = buildWarnDetail(conditionMap, businessActivity, metricsEvent, output);
                            //t_warn_detail
                            tWarnDetailMapper.insertSelective(warnDetail);
                            if (SceneManageConstant.EVENT_DESTORY.equals(event)) {
                                // ??????sla????????????
                                UpdateReportSlaDataInput slaDataInput = new UpdateReportSlaDataInput();
                                SlaBean slaBean = new SlaBean();
                                slaBean.setRuleName(slaRef.getSlaName());
                                slaBean.setBusinessActivity(activityRef.getBusinessActivityName());
                                slaBean.setBindRef(bindRef);
                                slaBean.setRule(warnDetail.getWarnContent());
                                slaDataInput.setReportId(report.getId());
                                slaDataInput.setSlaBean(slaBean);
                                //??????report
                                cloudReportService.updateReportSlaData(slaDataInput);
                                callRunningFailedEvent(resourceId, "SLA??????");
                            }
                        } else {
                            log.info("???????????????,sla????????????");
                        }
                    } catch (Exception e) {
                        log.warn("???SLA???????????????????????????????????????:{}", e.getMessage(), e);
                    }
                } else {//???????????????????????????????????????
                    stringRedisTemplate.opsForHash().put(SLA_DESTROY_KEY, id, JSON.toJSONString(model));
                    keyList.remove(id);
                }

            }
        }
        doClean(keyList);
    }

    private void doClean(List keyList) {
        keyList.forEach(key -> stringRedisTemplate.opsForHash().delete(SLA_DESTROY_KEY, key));
    }

    private Boolean matchContinue(AchieveModel model, Long timestamp, boolean isHit) {
        if (model == null) {
            if (isHit) {
                return true;
            }
            return false;
        }
        log.info("???sla??????????????????????????????????????????={}, ????????????={}?????????={}",
                model.getLastAchieveTime(), timestamp,
                (timestamp - model.getLastAchieveTime()));
        return true;
    }

    /**
     * ??????????????????
     *
     * @param conditionMap        ??????Map
     * @param businessActivityDTO ???????????????????????????????????????
     * @param metricsEvent        ??????
     * @param slaDto              sla??????
     * @return ????????????
     */
    private WarnDetail buildWarnDetail(Map<String, Object> conditionMap,
                                       SceneBusinessActivityRefOutput businessActivityDTO,
                                       SendMetricsEvent metricsEvent,
                                       SceneManageWrapperOutput.SceneSlaRefOutput slaDto) {
        WarnDetail warnDetail = new WarnDetail();
        warnDetail.setPtId(metricsEvent.getReportId());
        warnDetail.setSlaId(slaDto.getId());
        warnDetail.setSlaName(slaDto.getRuleName());
        warnDetail.setBindRef(businessActivityDTO.getBindRef());
        warnDetail.setBusinessActivityId(businessActivityDTO.getBusinessActivityId());
        warnDetail.setBusinessActivityName(businessActivityDTO.getBusinessActivityName());
        String sb = String.valueOf(conditionMap.get("type"))
                + conditionMap.get("compare")
                + slaDto.getRule().getDuring()
                + conditionMap.get("unit")
                + ", ??????"
                + slaDto.getRule().getTimes()
                + "???";
        warnDetail.setWarnContent(sb);
        warnDetail.setWarnTime(DateUtil.date(metricsEvent.getTimestamp()));
        warnDetail.setRealValue((Double) conditionMap.get("real"));
        return warnDetail;
    }

}
