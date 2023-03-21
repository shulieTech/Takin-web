package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author junshi
 * @ClassName ReturnVarRequest
 * @Description
 * @createTime 2023年03月15日 15:43
 */
@Data
@ApiModel("断言格式的入参")
public class AssertRequest implements Serializable {

    @ApiModelProperty(value = "检查点类型", required = true)
    @NotBlank(message = "检查点类型不能为空")
    private String checkPointType;

    @ApiModelProperty(value = "检查对象", required = true)
    @NotBlank(message = "检查对象不能为空")
    private String checkObject;

    @ApiModelProperty(value = "检查条件 1-匹配 2-包含 8-相等", required = true)
    @NotBlank(message = "检查条件不能为空")
    private String checkCondition;

    @ApiModelProperty(value = "检查内容", required = true)
    @NotBlank(message = "检查内容不能为空")
    private String checkContent;
}
