package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.scenemanage.WarnDTO;
import io.shulie.takin.web.ext.entity.UserCommonExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@Data
public class ReportDetailDTO extends UserCommonExt implements Serializable {

    private static final long serialVersionUID = 6093881590337487184L;

    @ApiModelProperty(value = "报告状态：0/就绪状态，1/生成中, 2/完成生成")
    private Integer taskStatus;

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
    private List<WarnDTO> warn;

    @ApiModelProperty(value = "业务活动链路概览")
    private List<BusinessActivitySummaryDTO> businessActivity;

    @ApiModelProperty(name = "leakVerifyResult", value = "漏数验证结果")
    private LeakVerifyResult leakVerifyResult;

    @ApiModelProperty(name = "jobId", value = "压测引擎任务Id")
    private Long jobId;

    /**
     * 资源Id
     */
    private String resourceId;
}
