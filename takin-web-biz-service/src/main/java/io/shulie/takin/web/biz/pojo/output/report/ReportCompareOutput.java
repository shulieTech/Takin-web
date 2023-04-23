package io.shulie.takin.web.biz.pojo.output.report;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("压测报告比对")
public class ReportCompareOutput implements Serializable {

    private List<ReportCompareTargetOut> targetData;

    private List<ReportCompareRtOutput> rtData;

    private List<ReportCompareColumnarOut> columnarData = new ArrayList<>();

    private List<ReportCompareTrendOut> trendData = new ArrayList<>();


}
