package io.shulie.takin.cloud.biz.notify.processor.calibration;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.pamirs.takin.cloud.entity.domain.dto.report.StatReportDTO;
import io.shulie.takin.cloud.biz.collector.PushWindowDataScheduled;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.notify.CloudNotifyProcessor;
import io.shulie.takin.cloud.biz.service.report.CloudReportService;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.common.influxdb.InfluxUtil;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.cloud.common.utils.GsonUtil;
import io.shulie.takin.cloud.common.utils.JsonPathUtil;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.service.report.impl.SummaryService;
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
    @Resource
    private PressureDataCalibration pressureDataCalibration;
    @Resource
    private SummaryService summaryService;

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
        pressureDataCalibration.processCalibrationStatus(param.getJobId(),
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
                pressureDataCalibration.processCalibrationStatus(jobId, true, "", true);
                if (Objects.nonNull(finalAction)) {
                    finalAction.run();
                }
            };
            pushWindowDataScheduled.combineMetricsData(reportDao.selectByJobId(jobId), true, action);
        } else {
            // 失败了直接更新
            pressureDataCalibration.processCalibrationStatus(jobId, false, param.getContent(), true);
        }
    }

    // copy from io.shulie.takin.cloud.biz.service.report.impl.CloudReportServiceImpl.saveReportResult
    private void updateReport(Long jobId) {
        ReportResult report = reportDao.selectByJobId(jobId);
        String testPlanXpathMd5 = getTestPlanXpathMd5(report.getScriptNodeTree());
        String transaction = StringUtils.isBlank(testPlanXpathMd5) ? ReportConstants.ALL_BUSINESS_ACTIVITY
            : testPlanXpathMd5;
        Long reportId = report.getId();
        Long sceneId = report.getSceneId();
        Long tenantId = report.getTenantId();
        boolean isConclusion = cloudReportService.updateReportBusinessActivity(jobId, sceneId, reportId, tenantId);
        if (isSla(report)) {
            report.setConclusion(ReportConstants.FAIL);
            getReportFeatures(report, ReportConstants.FEATURES_ERROR_MSG, "触发SLA终止规则");
        } else if (!isConclusion) {
            report.setConclusion(ReportConstants.FAIL);
            getReportFeatures(report, ReportConstants.FEATURES_ERROR_MSG, "业务活动指标不达标");
        } else {
            report.setConclusion(ReportConstants.PASS);
            getReportFeatures(report, ReportConstants.FEATURES_ERROR_MSG, "");
        }
        summaryService.calcReportSummay(reportId);
        StatReportDTO statReport = cloudReportService.statReport(jobId, sceneId, reportId, tenantId, transaction);
        ReportUpdateParam updateParam = new ReportUpdateParam();
        if (Objects.nonNull(statReport)) {
            log.info("cloud订正压测报告数据成功:jobId=[{}], requestCount=[{}]", jobId, statReport.getTotalRequest());
            updateParam.setTotalRequest(statReport.getTotalRequest());
            updateParam.setAvgRt(statReport.getAvgRt());
            updateParam.setAvgTps(statReport.getTps());
            updateParam.setSuccessRate(statReport.getSuccessRate());
            updateParam.setSa(statReport.getSa());
            updateParam.setAvgConcurrent(statReport.getAvgConcurrenceNum());
            updateParam.setConcurrent(statReport.getMaxConcurrenceNum());
        }
        updateParam.setId(reportId);
        updateParam.setGmtUpdate(new Date());
        updateParam.setFeatures(report.getFeatures());
        updateParam.setConclusion(report.getConclusion());
        reportDao.updateReport(updateParam);
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

    private boolean isSla(ReportResult reportResult) {
        if (StringUtils.isBlank(reportResult.getFeatures())) {
            return false;
        }
        JSONObject jsonObject = JSON.parseObject(reportResult.getFeatures());
        // sla熔断数据
        return jsonObject.containsKey(ReportConstants.SLA_ERROR_MSG)
            && StringUtils.isNotEmpty(jsonObject.getString(ReportConstants.SLA_ERROR_MSG));
    }

    private void getReportFeatures(ReportResult reportResult, String errKey, String errMsg) {
        Map<String, String> map = Maps.newHashMap();
        if (StringUtils.isNotBlank(reportResult.getFeatures())) {
            map = JsonHelper.string2Obj(reportResult.getFeatures(), new TypeReference<Map<String, String>>() {
            });
        }
        if (StringUtils.isNotBlank(errKey)) {
            if (StringUtils.isBlank(errMsg)) {
                map.remove(errKey);
            } else {
                errMsg = StringUtils.trim(errMsg);
                if (!errMsg.startsWith("[") && !errMsg.startsWith("{") && errMsg.length() > 100) {
                    errMsg = errMsg.substring(0, 100);
                }
                map.put(errKey, errMsg);
            }
            reportResult.setFeatures(GsonUtil.gsonToString(map));
        }
    }
}
