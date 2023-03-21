package io.shulie.takin.web.biz.pojo.request.pts;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junshi
 * @ClassName KeyValueRequest
 * @Description
 * @createTime 2023年03月15日 15:39
 */
@Data
@ApiModel("key-value格式的入参")
public class KeyValueRequest implements Serializable {

    @ApiModelProperty(value = "key", required = true)
    @NotBlank(message = "key不能为空")
    private String key;

    @ApiModelProperty(value = "value", required = true)
    @NotBlank(message = "value不能为空")
    private String value;
}
