package io.shulie.takin.cloud.biz.collector;

import cn.hutool.core.date.DateUnit;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.utils.Executors;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.CollectorUtil;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.param.report.ReportQueryParam;
import io.shulie.takin.cloud.data.param.report.ReportQueryParam.PressureTypeRelation;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-20 22:13
 */
@Slf4j
@Component
public class PushWindowDataScheduled extends AbstractIndicators {

    @Resource
    private ReportDao reportDao;

    private void finishPushData(ReportResult report, Long timeWindow, long endTime) {
        if (null == report) {
            return;
        }
        Long sceneId = report.getSceneId();
        Long reportId = report.getId();
        Long customerId = report.getTenantId();
        String taskKey = getPressureTaskKey(sceneId, reportId, customerId);
        String last = String.valueOf(redisTemplate.opsForValue().get(last(taskKey)));
        long nowTimeWindow = CollectorUtil.getTimeWindowTime(System.currentTimeMillis());
        log.info("finishPushData {}-{}-{} last={}, timeWindow={}, endTime={}, now={}", sceneId, reportId, customerId,
            last, showTime(timeWindow), showTime(endTime), showTime(nowTimeWindow));

        if (null != report.getEndTime()) {
            endTime = Math.min(endTime, report.getEndTime().getTime());
        }

        if (ScheduleConstants.LAST_SIGN.equals(last) || (null != timeWindow && timeWindow > endTime)) {
            // 比较 endTime timeWindow

            // 清除 SLA配置 清除PushWindowDataScheduled 删除pod job configMap  生成报告
            ResourceContext context = new ResourceContext();
            context.setSceneId(sceneId);
            context.setReportId(reportId);
            context.setTenantId(customerId);
            context.setResourceId(report.getResourceId());
            notifyFinish(context);
            redisTemplate.delete(last(taskKey));
            log.info("---> 本次压测{}-{}-{}完成，已发送finished事件！<------", sceneId, reportId, customerId);
        }
    }


    /**
     * 每五秒执行一次
     * 每次从redis中取10秒前的数据
     */
    @Async("collectorSchedulerPool")
    @Scheduled(cron = "0/5 * * * * ? ")
    public void pushData() {
        ReportQueryParam param = new ReportQueryParam();
        param.setStatus(0);
        param.setIsDel(0);
        param.setPressureTypeRelation(new PressureTypeRelation(PressureSceneEnum.INSPECTION_MODE.getCode(), false));
        param.setJobIdNotNull(true);
        List<ReportResult> results = reportDao.queryReportList(param);
        if (CollectionUtils.isEmpty(results)) {
            log.debug("没有需要处理的报告！");
            return;
        }
        List<Long> reportIds = CommonUtil.getList(results, ReportResult::getId);
        log.info("找到需要处理的报告：" + JsonHelper.bean2Json(reportIds));
        results.stream().filter(Objects::nonNull).forEach(this::combineMetricsData);
    }

    private String showTime(Long timestamp) {
        if (null == timestamp) {return "";}
        // 忽略时间精度到天
        long d1 = timestamp / DateUnit.DAY.getMillis();
        long d2 = System.currentTimeMillis() / DateUnit.DAY.getMillis();
        // 转换时间
        cn.hutool.core.date.DateTime timestampDate = cn.hutool.core.date.DateUtil.date(timestamp);
        String timeString = d1 == d2 ?
            // 同一日则显示时间 HH:mm:ss
            timestampDate.toTimeStr() :
            // 不同日则显示日期时间 yyyy-MM-dd HH:mm:ss
            timestampDate.toString();
        // 返回
        return timestamp + "(" + timeString + ")";
    }

    public void combineMetricsData(ReportResult r) {
        if (Objects.isNull(r)) {
            return;
        }
        Runnable runnable = () -> {
            Long sceneId = r.getSceneId();
            Long reportId = r.getId();
            Long customerId = r.getTenantId();
            String lockKey = String.format("finishPushData:%s:%s:%s", sceneId, reportId, customerId);
            if (!lock(lockKey, "1")) {
                return;
            }
            TenantCommonExt ext = new TenantCommonExt();
            ext.setSource(ContextSourceEnum.JOB.getCode());
            ext.setTenantId(customerId);
            ext.setEnvCode(r.getEnvCode());
            TenantInfoExt infoExt = WebPluginUtils.getTenantInfo(customerId);
            if(infoExt == null) {
                log.error("租户信息未找到【{}】", customerId);
                return;
            }
            ext.setTenantAppKey(infoExt.getTenantAppKey());
            try {
                WebPluginUtils.setTraceTenantContext(ext);
                SceneManageWrapperOutput scene = cloudSceneManageService.getSceneManage(sceneId, null);
                if (null == scene) {
                    log.info("no such scene manager!sceneId=" + sceneId);
                    return;
                }
                //结束时间取开始压测时间-10s+总测试时间+3分钟， 3分钟富裕时间，给与pod启动和压测引擎启动延时时间
                long endTime = TimeUnit.MINUTES.toMillis(3L);
                if (null != r.getStartTime()) {
                    endTime += (r.getStartTime().getTime() - TimeUnit.SECONDS.toMillis(10));
                } else if (null != r.getGmtCreate()) {
                    endTime += r.getGmtCreate().getTime();
                }
                if (null != scene.getTotalTestTime()) {
                    endTime += TimeUnit.SECONDS.toMillis(scene.getTotalTestTime());
                } else if (null != scene.getPressureTestSecond()) {
                    endTime += TimeUnit.SECONDS.toMillis(scene.getPressureTestSecond());
                }

                long timeWindow = CollectorUtil.getNowTimeWindow();
                if (null != r.getEndTime() && timeWindow >= r.getEndTime().getTime()) {
                    // 更新压测场景状态  压测引擎运行中,压测引擎停止压测 ---->压测引擎停止压测
                    cloudSceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId,
                            customerId)
                        .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING, SceneManageStatusEnum.STOP)
                        .updateEnum(SceneManageStatusEnum.STOP)
                        .build());
                }
                finishPushData(r,timeWindow, endTime);
            } catch (Throwable t) {
                log.error("pushData2 error!", t);
            } finally {
                unlock(lockKey, "0");
            }
        };
        Executors.execute(runnable);
    }
}
