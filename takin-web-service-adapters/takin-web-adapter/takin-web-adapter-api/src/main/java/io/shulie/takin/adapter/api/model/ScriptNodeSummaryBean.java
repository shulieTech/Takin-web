package io.shulie.takin.adapter.api.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.shulie.takin.adapter.api.model.common.DataBean;
import io.shulie.takin.adapter.api.model.common.DistributeBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author moriarty
 */
@Data
@ApiModel(value = "报告节点数据对象")
public class ScriptNodeSummaryBean {

    @ApiModelProperty(value = "节点类型名称")
    private String name;

    @ApiModelProperty(value = "节点名称")
    private String testName;

    @ApiModelProperty(value = "元素节点的md5值")
    private String md5;

    @ApiModelProperty(value = "业务活动ID")
    private Long activityId;

    @ApiModelProperty(value = "节点类型")
    private String type;

    @ApiModelProperty(value = "元素的绝对路径")
    private String xpath;

    @ApiModelProperty(value = "xpath的md5")
    private String xpathMd5;

    @ApiModelProperty(value = "TPS")
    private DataBean tps;

    @ApiModelProperty(value = "平均RT")
    private DataBean avgRt;

    @ApiModelProperty(value = "请求成功率")
    private DataBean successRate;

    @ApiModelProperty(value = "SA")
    private DataBean sa;

    @ApiModelProperty(value = "最大tps")
    private BigDecimal maxTps;

    @ApiModelProperty(value = "最大rt")
    private BigDecimal maxRt;

    @ApiModelProperty(value = "最小rt")
    private BigDecimal minRt;

    @ApiModelProperty(value = "总请求")
    private Long totalRequest;

    @ApiModelProperty(value = "5s请求数")
    private Long tempRequestCount;

    @ApiModelProperty(value = "平均线程数")
    private BigDecimal avgConcurrenceNum;

    @ApiModelProperty(value = "分布")
    private List<DistributeBean> distribute;

    private String rtDistribute;

    @ApiModelProperty(value = "通过标识")
    private Integer passFlag;

    @ApiModelProperty(value = "关联应用的ID")
    private String applicationIds;

    @ApiModelProperty(value = "并发阶梯递增模式线程数")
    private List<BigDecimal> concurrentStageThreadNum;

    private List<ScriptNodeSummaryBean> children;

    private String features;

}
