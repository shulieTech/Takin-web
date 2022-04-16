package io.shulie.takin.cloud.biz.notify.processor.calibration;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import com.pamirs.takin.cloud.entity.domain.dto.report.StatReportDTO;
import io.shulie.takin.cloud.biz.collector.PushWindowDataScheduled;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.notify.CloudNotifyProcessor;
import io.shulie.takin.cloud.biz.service.report.CloudReportService;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.common.influxdb.InfluxUtil;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.cloud.common.utils.JsonPathUtil;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.web.common.util.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataCalibrationProcessor extends AbstractIndicators
    implements CloudNotifyProcessor<DataCalibrationNotifyParam> {

    @Resource
    private CloudReportService cloudReportService;
    @Resource
    private RedisClientUtil redisClientUtil;
    @Resource
    private ReportDao reportDao;
    @Resource
    private InfluxWriter influxWriter;
    @Resource
    private PushWindowDataScheduled pushWindowDataScheduled;

    @Override
    public CallbackType type() {
        return CallbackType.EXCESS_JOB;
    }

    /**
     * amdb	    cloud
     * 00		00      未校准
     * 01		01      校准中
     * 10		10      校准失败
     * 11		11      校准成功
     */
    @Override
    public String process(DataCalibrationNotifyParam param) {
        long jobId = param.getJobId();
        String statusKey = PressureStartCache.getDataCalibrationStatusKey(jobId);
        if (!redisClientUtil.hasKey(statusKey)) {
            return param.getResourceId();
        }
        String source = param.getSource();
        if (StringUtils.isBlank(source)) {
            source = "cloud";
        }
        String callbackKey = PressureStartCache.getDataCalibrationCallbackKey(jobId, source);
        if (redisClientUtil.lockNoExpire(callbackKey, String.valueOf(System.currentTimeMillis()))) {
            boolean execute = false;
            Runnable action = () -> redisClientUtil.del(RedisClientUtil.getLockKey(callbackKey));
            if ("amdb".equalsIgnoreCase(source) && !redisClientUtil.isSet(statusKey, 0)) {
                execute = true;
                processAmdb(param, action);
            } else if ("cloud".equalsIgnoreCase(source) && !redisClientUtil.isSet(statusKey, 2)) {
                execute = true;
                processCloud(param, action);
            }
            if (!execute) {
                action.run();
            }
        }
        return param.getResourceId();
    }

    private void processAmdb(DataCalibrationNotifyParam param, Runnable finalAction) {
        processCalibrationStatus(param.getJobId(),
            Boolean.TRUE.equals(param.getCompleted()), param.getContent(), false);
        finalAction.run();
    }

    private void processCloud(DataCalibrationNotifyParam param, Runnable finalAction) {
        boolean success = Boolean.TRUE.equals(param.getCompleted());
        long jobId = param.getJobId();
        if (success) {
            // 删除表
            influxWriter.truncateMeasurement(InfluxUtil.getMeasurement(jobId, null, null, null));
            Runnable action = () -> {
                updateReport(jobId);
                processCalibrationStatus(jobId, true, "", true);
                if (Objects.nonNull(finalAction)) {
                    finalAction.run();
                }
            };
            pushWindowDataScheduled.combineMetricsData(reportDao.selectByJobId(jobId), true, action);
        } else {
            // 失败了直接更新
            processCalibrationStatus(jobId, false, param.getContent(), true);
        }
    }

    private void updateReport(Long jobId) {
        ReportResult report = reportDao.selectByJobId(jobId);
        String testPlanXpathMd5 = getTestPlanXpathMd5(report.getScriptNodeTree());
        String transaction = StringUtils.isBlank(testPlanXpathMd5) ? ReportConstants.ALL_BUSINESS_ACTIVITY
            : testPlanXpathMd5;
        Long reportId = report.getId();
        Long sceneId = report.getSceneId();
        Long tenantId = report.getTenantId();
        cloudReportService.updateReportBusinessActivity(jobId, sceneId, reportId, tenantId);
        StatReportDTO statReport = cloudReportService.statReport(jobId, sceneId, reportId, tenantId, transaction);
        if (Objects.nonNull(statReport)) {
            log.info("cloud订正压测报告数据成功:jobId=[{}], requestCount=[{}]", jobId, statReport.getTotalRequest());
            ReportUpdateParam updateParam = new ReportUpdateParam();
            updateParam.setId(reportId);
            updateParam.setTotalRequest(statReport.getTotalRequest());
            updateParam.setAvgRt(statReport.getAvgRt());
            updateParam.setAvgTps(statReport.getTps());
            updateParam.setSuccessRate(statReport.getSuccessRate());
            updateParam.setSa(statReport.getSa());
            updateParam.setAvgConcurrent(statReport.getAvgConcurrenceNum());
            updateParam.setConcurrent(statReport.getMaxConcurrenceNum());
            updateParam.setGmtUpdate(new Date());
            reportDao.updateReport(updateParam);
        }
    }

    private String getTestPlanXpathMd5(String scriptNodeTree) {
        if (StringUtils.isBlank(scriptNodeTree)) {
            return null;
        }
        List<ScriptNode> currentNode = JsonPathUtil.getCurrentNodeByType(scriptNodeTree, NodeTypeEnum.TEST_PLAN.name());
        if (CollectionUtils.isNotEmpty(currentNode) && currentNode.size() == 1) {
            return currentNode.get(0).getXpathMd5();
        }
        return null;
    }
}
