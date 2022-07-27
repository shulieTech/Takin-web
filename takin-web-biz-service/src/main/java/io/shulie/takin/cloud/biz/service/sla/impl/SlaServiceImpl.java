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
 * @date 2020/4/20 下午4:48
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


                //从redis中获取条件数据
                String object = (String) stringRedisTemplate.opsForHash().get(SLA_DESTROY_KEY, id);
                AchieveModel model = (object != null ? JSON.parseObject(object, AchieveModel.class) : null);
                if (!matchContinue(model, System.currentTimeMillis(), output.getRule().getTimes() == 1)) {//为空记录条件数据或者当次命中
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
                if (model.getTimes() >= output.getRule().getTimes()) {//连续命中次数满足条件
                    try {
                        ScheduleStopRequestExt scheduleStopRequest = new ScheduleStopRequestExt();
                        scheduleStopRequest.setTaskId(info.getJobId());
                        scheduleStopRequest.setSceneId(slaRef.getSceneId());
                        // 增加顾客id
//                    scheduleStopRequest.setTenantId();
                        Map<String, Object> extendMap = new HashMap<>(1);
                        extendMap.put(Constants.SLA_DESTROY_EXTEND, "SLA发送压测任务终止事件");
                        scheduleStopRequest.setExtend(extendMap);
                        //报告未结束，才通知
                        String resourceId = String.valueOf(info.getResourceId());
                        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(PressureStartCache.getResourceKey(resourceId)))) {
                            // 熔断数据也记录到告警明细中
                            SendMetricsEvent metricsEvent = new SendMetricsEvent();
                            metricsEvent.setReportId(report.getId());
                            metricsEvent.setTimestamp(System.currentTimeMillis());
                            WarnDetail warnDetail = buildWarnDetail(conditionMap, businessActivity, metricsEvent, output);
                            //t_warn_detail
                            tWarnDetailMapper.insertSelective(warnDetail);
                            if (SceneManageConstant.EVENT_DESTORY.equals(event)) {
                                // 记录sla熔断数据
                                UpdateReportSlaDataInput slaDataInput = new UpdateReportSlaDataInput();
                                SlaBean slaBean = new SlaBean();
                                slaBean.setRuleName(slaRef.getSlaName());
                                slaBean.setBusinessActivity(activityRef.getBusinessActivityName());
                                slaBean.setBindRef(bindRef);
                                slaBean.setRule(warnDetail.getWarnContent());
                                slaDataInput.setReportId(report.getId());
                                slaDataInput.setSlaBean(slaBean);
                                //更新report
                                cloudReportService.updateReportSlaData(slaDataInput);
                                callRunningFailedEvent(resourceId, "SLA熔断");
                            }
                        } else {
                            log.info("报告被删除,sla未处理完");
                        }
                    } catch (Exception e) {
                        log.warn("【SLA】发送压测任务终止事件失败:{}", e.getMessage(), e);
                    }
                } else {//不满足连续条件更新条件数据
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
        log.info("【sla】校验是否连续，上次触发时间={}, 当前时间={}，相差={}",
                model.getLastAchieveTime(), timestamp,
                (timestamp - model.getLastAchieveTime()));
        return true;
    }

    /**
     * 创建告警明细
     *
     * @param conditionMap        条件Map
     * @param businessActivityDTO 关联的业务活动（脚本节点）
     * @param metricsEvent        数据
     * @param slaDto              sla内容
     * @return 告警条件
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
                + ", 连续"
                + slaDto.getRule().getTimes()
                + "次";
        warnDetail.setWarnContent(sb);
        warnDetail.setWarnTime(DateUtil.date(metricsEvent.getTimestamp()));
        warnDetail.setRealValue((Double) conditionMap.get("real"));
        return warnDetail;
    }

}
