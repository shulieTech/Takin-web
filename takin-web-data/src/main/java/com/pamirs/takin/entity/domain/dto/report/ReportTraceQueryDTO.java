package com.pamirs.takin.entity.domain.dto.report;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 压测报告；统计返回
 *
 * @author qianshui
 * @date 2020/7/22 下午2:19
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("入参类 -- 报告查询入参类")
@Data
public class ReportTraceQueryDTO extends PageBaseDTO {

    private static final long serialVersionUID = 8928035842416997931L;

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("报告id，如果是压测报告中有此参数")
    private Long reportId;

    @ApiModelProperty("压测引擎任务Id，对应大数据taskId")
    private Long taskId;

    @ApiModelProperty("开始压测的时间戳")
    private Long startTime;

    @ApiModelProperty("压测结束的时间戳")
    private Long endTime;

    @ApiModelProperty("查询条件，null 为全部，1为成功，0为失败, 2 断言失败")
    private Integer resultType;

    @ApiModelProperty(value = "traceId")
    private String traceId;

    @ApiModelProperty(value = "耗时ms，比较规则 大于")
    private Long minCost;

    @ApiModelProperty(value = "耗时ms，比较规则 小于等于")
    private Long maxCost;

    @ApiModelProperty("调用类型")
    private String middlewareName;

    @ApiModelProperty("接口名")
    private String serviceName;

    @ApiModelProperty("方法名")
    private String methodName;

    @ApiModelProperty("调用参数")
    private String request;

    @ApiModelProperty("排序字段：startDate、cost")
    private String sortField;

    @ApiModelProperty("排序方式：asc、desc")
    private String sortType;

    /**
     * 1-agent上报trace明细
     * 2-压测报告请求trace明细
     */
    @ApiModelProperty("日志类型：1-agent上报trace明细、2-压测报告请求trace明细")
    private Integer queryType;

    @ApiModelProperty("脚本路径md5")
    private String xpathMd5;
}
