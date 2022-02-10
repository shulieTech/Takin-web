package io.shulie.takin.web.api.model;

import java.math.BigDecimal;
import java.util.List;

public class ScriptNodeSummaryModel {
    /**
     * 节点类型名称
     */
    private String name;
    /**
     * 节点名称
     */
    private String testName;
    /**
     * 业务活动ID
     */
    private Long activityId;
    /**
     * 节点类型
     */
    private String type;
    /**
     * TPS
     */
    private DataBean tps;
    @ApiModelProperty("平均RT")
    private DataBean avgRt;
    @ApiModelProperty("请求成功率")
    private DataBean successRate;
    @ApiModelProperty("SA")
    private DataBean sa;
    @ApiModelProperty("最大tps")
    private BigDecimal maxTps;
    @ApiModelProperty("最大rt")
    private BigDecimal maxRt;
    @ApiModelProperty("最小rt")
    private BigDecimal minRt;
    @ApiModelProperty("总请求")
    private Long totalRequest;
    @ApiModelProperty("5s请求数")
    private Long tempRequestCount;
    @ApiModelProperty("平均线程数")
    private BigDecimal avgConcurrenceNum;
    @ApiModelProperty("分布")
    private List<DistributeBean> distribute;
    @ApiModelProperty("通过标识")
    private Integer passFlag;
    @ApiModelProperty("关联应用的ID")
    private String applicationIds;
    private List<ScriptNodeSummaryBean> children;
}
