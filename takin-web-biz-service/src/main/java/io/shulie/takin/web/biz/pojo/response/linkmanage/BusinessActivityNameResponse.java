package io.shulie.takin.web.biz.pojo.response.linkmanage;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class BusinessActivityNameResponse implements Serializable {
    private static final long serialVersionUID = 4523369496625921607L;

    @ApiModelProperty(name = "businessActivityId", value = "业务活动ID")
    @NotNull(message = "业务活动ID不能为空")
    private Long businessActivityId;

    @ApiModelProperty(name = "businessActivityName", value = "业务活动名称")
    private String businessActivityName;

    @ApiModelProperty(name = "targetTPS", value = "目标TPS")
    @NotNull(message = "目标TPS不能为空")
    private Integer targetTPS;

    @ApiModelProperty(name = "targetRT", value = "目标RT")
    @NotNull(message = "目标RT不能为空")
    private Integer targetRT;

    @ApiModelProperty(name = "targetSuccessRate", value = "目标成功率")
    @NotNull(message = "目标成功率不能为空")
    private BigDecimal targetSuccessRate;

    @ApiModelProperty(name = "targetSA", value = "目标SA")
    @NotNull(message = "目标SA不能为空")
    private BigDecimal targetSA;
}
