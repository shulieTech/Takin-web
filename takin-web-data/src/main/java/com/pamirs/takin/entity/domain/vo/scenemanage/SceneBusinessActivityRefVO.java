package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/4/17 下午5:13
 */
@Data
public class SceneBusinessActivityRefVO implements Serializable {

    private static final long serialVersionUID = -2028726088507717658L;

    @ApiModelProperty(name = "businessActivityId", value = "业务活动ID")
    @NotNull(message = "业务活动ID不能为空")
    private Long businessActivityId;

    @ApiModelProperty(name = "businessActivityName", value = "业务活动名称")
    private String businessActivityName;

    @ApiModelProperty(name = "targetTPS", value = "目标TPS")
    @NotNull(message = "目标TPS不能为空")
    @Min(message = "目标TPS必须大于0", value = 1)
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

    private String bindRef;

    private String applicationIds;

    //关联上传脚本id
    private Long scriptId;
}
