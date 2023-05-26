package io.shulie.takin.web.biz.pojo.output.report;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@ApiModel(value = "ReportAppMapOut", description = "应用趋势图")
public class ReportAppMapOut {

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "tps数组")
    private Double tps[];

    @ApiModelProperty(value = "rt数组")
    private Double rt[];

    @ApiModelProperty(value = "成功率数组")
    private Double successRate[];

    @ApiModelProperty(value = "请求数数组")
    private Integer concurent[];

    private String[] xtime;

    private String[] xcost;

    private String[] conut;

    public static void main(String[] args) {
        ReportAppMapOut reportAppMapOut = new ReportAppMapOut();
        reportAppMapOut.setAppName("appName");
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("1", 1);
        reportAppMapOut.setTps(new Double[]{1.0, 2.0});
        reportAppMapOut.setRt(new Double[]{1.0, 2.0});
        reportAppMapOut.setSuccessRate(new Double[]{1.0, 2.0});
        reportAppMapOut.setConcurent(new Integer[]{1, 2});
        System.out.println(JSON.toJSONString(reportAppMapOut));
    }
}
