package io.shulie.takin.web.biz.pojo.output.report;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("压测报告比对-TPS、rt")
public class ReportCompareTrendOut implements Serializable {

    private Long reportId;

    private List<String> xTime;

    private List<String> concurrent;

    private List<String> tps;

    private List<String> rt;

    private List<String> successRate;

    private List<String> sa;

    private List<String> xCost = new ArrayList<>();

    private List<String> count = new ArrayList<>();
}
