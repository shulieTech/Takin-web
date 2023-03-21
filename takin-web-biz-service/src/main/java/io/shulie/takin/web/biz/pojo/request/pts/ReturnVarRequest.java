package io.shulie.takin.web.biz.pojo.request.pts;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junshi
 * @ClassName ReturnVarRequest
 * @Description
 * @createTime 2023年03月15日 15:43
 */
@Data
@ApiModel("出参格式的入参")
public class ReturnVarRequest implements Serializable {

    @ApiModelProperty(value = "出参名", required = true)
    @NotBlank(message = "出参名不能为空")
    private String varName;

    @ApiModelProperty(value = "来源", required = true)
    @NotBlank(message = "来源不能为空")
    private String varSource;

    @ApiModelProperty(value = "解析表达式", required = true)
    @NotBlank(message = "解析表达式不能为空")
    private String parseExpress;

    @ApiModelProperty(value = "第几个匹配项，默认第一个匹配")
    private Integer matchIndex;
}
