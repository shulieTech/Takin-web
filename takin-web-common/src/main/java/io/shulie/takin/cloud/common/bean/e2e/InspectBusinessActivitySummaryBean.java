package io.shulie.takin.cloud.common.bean.e2e;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/5/12 8:26 下午
 */
@Data
public class InspectBusinessActivitySummaryBean {
    @ApiModelProperty(value = "活动ID")
    private Long businessActivityId;

    @ApiModelProperty(value = "活动名称")
    private String businessActivityName;

    @ApiModelProperty(value = "请求总数")
    private Long totalRequest;

    @ApiModelProperty(value = "平均成功率")
    private BigDecimal avgSuccessRate;

    @ApiModelProperty(value = "平均RT")
    private BigDecimal avgRt;

    @ApiModelProperty(value = "平均TPS")
    private BigDecimal avgTps;
}
