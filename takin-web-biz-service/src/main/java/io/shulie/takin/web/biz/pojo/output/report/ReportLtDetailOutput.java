package io.shulie.takin.web.biz.pojo.output.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 无涯
 * @date 2021/2/3 2:01 下午
 */
@Data
public class ReportLtDetailOutput implements Serializable {

    @ApiModelProperty(value = "报告ID")
    private Long reportId;

    @ApiModelProperty(value = "压测场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    @ApiModelProperty(value = "消耗流量")
    private BigDecimal amount;

    @ApiModelProperty(value = "压测时长")
    private String pressureTestTime;

    @ApiModelProperty(value = "压测结论: 0/不通过，1/通过")
    private Integer conclusion;

    @ApiModelProperty(value = "压测结论描述")
    private String conclusionRemark;

    @ApiModelProperty(value = "请求总数")
    private Long totalRequest;

    @ApiModelProperty(value = "最大并发数")
    private Integer maxConcurrent;

    @ApiModelProperty(value = "平均并发数")
    private BigDecimal avgConcurrent;

    @ApiModelProperty(value = "实际TPS")
    private BigDecimal realTps;

    @ApiModelProperty(value = "目标TPS")
    private Integer targetTps;

    @ApiModelProperty(value = "最大TPS")
    private BigDecimal maxTps;

    @ApiModelProperty(value = "平均RT")
    private BigDecimal avgRt;

    @ApiModelProperty(value = "最大RT")
    private BigDecimal maxRt;

    @ApiModelProperty(value = "成功率")
    private BigDecimal successRate;

    @ApiModelProperty(value = "sa")
    private BigDecimal sa;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "压测引擎任务ID")
    private Long jobId;

    @ApiModelProperty(value = "业务活动列表")
    private List<BusinessActivityReportOutput> businessActivities;

    @ApiModelProperty(value = "应用列表")
    private List<String> appNames;

    @ApiModelProperty(value = "压测报告列表")
    private List<SceneReportListOutput> reports;
}
