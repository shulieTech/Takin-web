package io.shulie.takin.adapter.api.model.response.scenemanage;

import java.math.BigDecimal;
import java.util.List;

import io.shulie.takin.adapter.api.model.common.DataBean;
import io.shulie.takin.adapter.api.model.common.DistributeBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@Data
public class BusinessActivitySummaryBean {

    @ApiModelProperty(value = "活动ID")
    private Long businessActivityId;

    @ApiModelProperty(value = "活动名称")
    private String businessActivityName;

    @ApiModelProperty(value = "应用ID")
    private String applicationIds;

    @ApiModelProperty(value = "绑定关系")
    private String bindRef;

    @ApiModelProperty(value = "总请求")
    private Long totalRequest;

    @ApiModelProperty(value = "平均线程数")
    private BigDecimal avgConcurrenceNum;

    @ApiModelProperty(value = "TPS")
    private DataBean tps;

    @ApiModelProperty(value = "平均RT")
    private DataBean avgRT;

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

    @ApiModelProperty(value = "分布")
    private List<DistributeBean> distribute;

    @ApiModelProperty(value = "通过标识")
    private Integer passFlag;

    private String features;

}

