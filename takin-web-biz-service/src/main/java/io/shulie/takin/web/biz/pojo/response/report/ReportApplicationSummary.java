package io.shulie.takin.web.biz.pojo.response.report;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.web.biz.pojo.output.report.ReportAppMapOut;
import io.shulie.takin.web.data.model.mysql.ReportApplicationSummaryEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class ReportApplicationSummary {
    private Long id;
    private Long reportId;

    private String appName;

    private TargetTps targetTps;

    @Data
    public static class TargetTps {
        @ApiModelProperty(value = "tps数组")
        private Double tps[];

        @ApiModelProperty(value = "rt数组")
        private Double rt[];

        @ApiModelProperty(value = "成功率数组")
        private Double successRate[];

        @ApiModelProperty(value = "请求数数组")
        private Integer concurrent[];

        private String[] xtime;

        private String[] xcost;

        private String[] count;
    }

    public static ReportApplicationSummaryEntity genReportApplicationSummaryEntity(ReportApplicationSummary result) {
        ReportApplicationSummaryEntity entity = new ReportApplicationSummaryEntity();
        entity.setId(result.getId());
        entity.setReportId(result.getReportId());
        entity.setApplicationName(result.getAppName());
        String json = null;
        if (result.targetTps != null) {
            json = JSON.toJSONString(result.targetTps);
        }
        entity.setTargetTps(json);
        return entity;
    }

    public static ReportApplicationSummaryEntity genReportApplicationSummaryEntity(ReportAppMapOut result, long reportId,Long id) {
        ReportApplicationSummaryEntity entity = new ReportApplicationSummaryEntity();
        entity.setId(id);
        entity.setReportId(reportId);
        entity.setApplicationName(result.getAppName());
        TargetTps tps = new TargetTps();
        tps.setTps(result.getTps());
        tps.setRt(result.getRt());
        tps.setSuccessRate(result.getSuccessRate());
        tps.setConcurrent(result.getConcurrent());
        tps.setXtime(result.getXtime());
        tps.setXcost(result.getXcost());
        tps.setCount(result.getCount());
        entity.setTargetTps(JSON.toJSONString(tps));
        return entity;
    }

    public static ReportApplicationSummary genRepportApplicationSummary(ReportApplicationSummaryEntity reportApplicationSummary) {
        ReportApplicationSummary summary = new ReportApplicationSummary();
        summary.setId(reportApplicationSummary.getId());
        summary.setReportId(reportApplicationSummary.getReportId());
        summary.setAppName(reportApplicationSummary.getApplicationName());
        if (StringUtils.isNotBlank(reportApplicationSummary.getTargetTps())) {
            TargetTps tps = JSON.parseObject(reportApplicationSummary.getTargetTps(), TargetTps.class);
            summary.setTargetTps(tps);
        }
        return summary;
    }

    public static ReportAppMapOut genReportAppMapOut(ReportApplicationSummary summary) {
        ReportAppMapOut reportAppMapOut = new ReportAppMapOut();
        reportAppMapOut.setAppName(summary.getAppName());
        reportAppMapOut.setTps(summary.getTargetTps().getTps());
        reportAppMapOut.setRt(summary.getTargetTps().getRt());
        reportAppMapOut.setSuccessRate(summary.getTargetTps().getSuccessRate());
        reportAppMapOut.setConcurrent(summary.getTargetTps().getConcurrent());
        reportAppMapOut.setXtime(summary.getTargetTps().getXtime());
        reportAppMapOut.setXcost(summary.getTargetTps().getXcost());
        reportAppMapOut.setCount(summary.getTargetTps().getCount());
        return reportAppMapOut;
    }
}
