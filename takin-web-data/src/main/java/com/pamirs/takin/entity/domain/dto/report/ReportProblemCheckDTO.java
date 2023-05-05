package com.pamirs.takin.entity.domain.dto.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("问题诊断-业务活动-出参")
public class ReportProblemCheckDTO implements Serializable {

    @ApiModelProperty("风险序号")
    private Integer seqNo;

    @ApiModelProperty("节点ID")
    private String nodeId;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("技术风险名称")
    private String techRiskName;

    @ApiModelProperty("诊断结果")
    private String checkResult;

    @ApiModelProperty("当前值")
    private String currentValue;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("trace样本")
    private String traceSampling;
}
