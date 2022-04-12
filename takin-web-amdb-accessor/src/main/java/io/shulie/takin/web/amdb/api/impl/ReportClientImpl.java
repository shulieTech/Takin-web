package io.shulie.takin.web.amdb.api.impl;

import java.util.List;

import javax.annotation.Resource;

import io.shulie.takin.web.amdb.api.ReportClient;
import io.shulie.takin.web.amdb.bean.query.report.ReportQueryDTO;
import io.shulie.takin.web.amdb.bean.result.report.ReportActivityDTO;
import io.shulie.takin.web.amdb.bean.result.report.ReportActivityInterfaceDTO;
import io.shulie.takin.web.amdb.bean.result.report.ReportInterfaceMetricsDTO;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.common.exception.TakinWebException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import static io.shulie.takin.web.common.exception.TakinWebExceptionEnum.REPORT_ACTIVITY_INTERFACE_QUERY_ERROR;
import static io.shulie.takin.web.common.exception.TakinWebExceptionEnum.REPORT_ACTIVITY_QUERY_ERROR;
import static io.shulie.takin.web.common.exception.TakinWebExceptionEnum.REPORT_CREATE_TABLE_ERROR;
import static io.shulie.takin.web.common.exception.TakinWebExceptionEnum.REPORT_INTERFACE_METRICS_QUERY_ERROR;
import static io.shulie.takin.web.common.exception.TakinWebExceptionEnum.REPORT_START_REPORT_ANALYZE_ERROR;

@Component
@Slf4j
public class ReportClientImpl implements ReportClient {

    private static final String ACTIVITY_PATH = "/amdb/report/activity/list";
    private static final String ACTIVITY_INTERFACE_PATH = "/amdb/report/activity/interface/list";
    private static final String INTERFACE_METRICS_PATH = "/amdb/report/activity/interface/metrics";
    private static final String CREATE_TABLE_PATH = "/amdb/report/create/table?reportId=@reportId@";

    @Resource
    private AmdbClientProperties properties;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public List<ReportActivityDTO> listReportActivity(ReportQueryDTO query) {
        String url = properties.getUrl().getAmdb() + ACTIVITY_PATH;
        try {
            return AmdbHelper.builder().url(url).httpMethod(HttpMethod.POST).param(query).eventName("查询压测报告入口信息")
                .exception(REPORT_ACTIVITY_QUERY_ERROR).list(ReportActivityDTO.class).getData();
        } catch (Exception e) {
            throw new TakinWebException(REPORT_ACTIVITY_QUERY_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public List<ReportActivityInterfaceDTO> listReportActivityInterface(ReportQueryDTO query) {
        String url = properties.getUrl().getAmdb() + ACTIVITY_INTERFACE_PATH;
        try {
            return AmdbHelper.builder().url(url).httpMethod(HttpMethod.POST).param(query).eventName("查询压测报告业务活动接口信息")
                .exception(REPORT_ACTIVITY_INTERFACE_QUERY_ERROR).list(ReportActivityInterfaceDTO.class).getData();
        } catch (Exception e) {
            throw new TakinWebException(REPORT_ACTIVITY_INTERFACE_QUERY_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public List<ReportInterfaceMetricsDTO> listReportInterfaceMetrics(ReportQueryDTO query) {
        String url = properties.getUrl().getAmdb() + INTERFACE_METRICS_PATH;
        try {
            return AmdbHelper.builder().url(url).httpMethod(HttpMethod.POST).param(query).eventName("压测报告业务活动接口指标信息")
                .exception(REPORT_INTERFACE_METRICS_QUERY_ERROR).list(ReportInterfaceMetricsDTO.class).getData();
        } catch (Exception e) {
            throw new TakinWebException(REPORT_INTERFACE_METRICS_QUERY_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public void createReportTraceTable(Long reportId) {
        String url = properties.getUrl().getAmdb() + CREATE_TABLE_PATH.replace("@reportId@", String.valueOf(reportId));
        try {
            AmdbHelper.builder().url(url).eventName("压测报告自动建表")
                .exception(REPORT_CREATE_TABLE_ERROR).one(Boolean.class);
        } catch (Exception e) {
            log.error("压测报告建表时报", e);
        }
    }

    @Override
    public void startAnalyze(Long reportId) {
        try {
            // redis key见：io.shulie.surge.data.deploy.pradar.report.ReportTaskServer
            String url = String.valueOf(redisTemplate.opsForValue().get("report:analyze:url"));
            if (StringUtils.isBlank(url)) {
                log.info("redis key[report:analyze:url]不存在或内容为空");
                return;
            }
            url += "?reportId=" + reportId;
            AmdbHelper.builder().url(url).eventName("启动压测报告分析任务")
                .exception(REPORT_START_REPORT_ANALYZE_ERROR).one(Boolean.class);
        } catch (Exception e) {
            log.error("启动压测报告数据分析异常", e);
        }
    }
}
