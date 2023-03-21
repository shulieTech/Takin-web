package io.shulie.takin.web.biz.pojo.request.pts;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junshi
 * @ClassName PtsPreLinkRequest
 * @Description
 * @createTime 2023年03月15日 16:08
 */
@Data
@ApiModel("新增业务流程-前置链路入参")
public class PtsPreLinkRequest extends PtsLinkRequest implements Serializable {

    @ApiModelProperty(value = "循环次数", required = true)
    @NotNull(message = "循环次数不能为空")
    private Integer loopCount;
}
