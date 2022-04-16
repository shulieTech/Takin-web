package io.shulie.takin.cloud.biz.output.report;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import io.shulie.takin.cloud.common.bean.scenemanage.WarnBean;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.adapter.api.model.ScriptNodeSummaryBean;
import io.shulie.takin.adapter.api.model.common.SlaBean;
import io.shulie.takin.adapter.api.model.common.StopReasonBean;
import io.shulie.takin.adapter.api.model.response.scenemanage.BusinessActivitySummaryBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2021/2/3 11:46 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportDetailOutput extends ContextExt {

    @ApiModelProperty(value = "报告状态：0/就绪状态，1/生成中, 2/完成生成")
    private Integer taskStatus;

    @ApiModelProperty(value = "sla熔断数据")
    private SlaBean slaMsg;

    @ApiModelProperty(value = "停止原因")
    private List<StopReasonBean> stopReasons;

    @ApiModelProperty(value = "报告ID")
    private Long id;

    @ApiModelProperty(value = "消耗流量")
    private BigDecimal amount;

    @ApiModelProperty(value = "场景名称")
    private Long sceneId;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "压测结论: 0/不通过，1/通过")
    private Integer conclusion;

    @ApiModelProperty(value = "压测结论描述")
    private String conclusionRemark;

    @ApiModelProperty(value = "警告次数")
    private Long totalWarn;

    @ApiModelProperty(value = "请求总数")
    private Long totalRequest;

    @ApiModelProperty(value = "最大并发")
    private Integer concurrent;

    @ApiModelProperty(value = "施压类型,0:并发,1:tps,2:自定义;不填默认为0")
    private Integer pressureType;

    @ApiModelProperty(value = "平均并发数")
    private BigDecimal avgConcurrent;

    @ApiModelProperty(value = "目标TPS")
    private Integer tps;

    @ApiModelProperty(value = "平均TPS")
    private BigDecimal avgTps;

    @ApiModelProperty(value = "平均RT")
    private BigDecimal avgRt;

    @ApiModelProperty(value = "成功率")
    private BigDecimal successRate;

    @ApiModelProperty(value = "sa")
    private BigDecimal sa;

    @ApiModelProperty(value = "测试时长")
    private String testTime;

    @ApiModelProperty(value = "测试总时长")
    private String testTotalTime;

    @ApiModelProperty(value = "警告列表")
    private List<WarnBean> warn;

    @ApiModelProperty(value = "业务活动链路概览")
    private List<BusinessActivitySummaryBean> businessActivity;

    private Long scriptId;

    @ApiModelProperty(value = "节点链路详情")
    private List<ScriptNodeSummaryBean> nodeDetail;

    /**
     * 资源Id
     */
    private String resourceId;

    /**
     * 压测引擎任务Id
     */
    private Long jobId;
    /**
     * 0-未校准
     * 1-校准中
     * 2-校准失败
     * 3-校准成功
     */
    private Integer calibration;
    private Integer calibrationStatus;
    private String calibrationMessage;
}
