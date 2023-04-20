package io.shulie.takin.web.biz.pojo.output.report;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("压测报告比对-TPS、rt")
public class ReportCompareTrendOut implements Serializable {

    private List<String> xTime;

    private List<String> concurrent1;

    private List<String> tps1;

    private List<String> rt1;

    private List<String> successRate1;

    private List<String> sa1;

    private List<String> concurrent2;

    private List<String> tps2;

    private List<String> rt2;

    private List<String> successRate2;

    private List<String> sa2;

    private List<String> xCost = new ArrayList<>();

    private List<String> count1 = new ArrayList<>();

    private List<String> count2 = new ArrayList<>();
}
