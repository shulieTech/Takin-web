package io.shulie.takin.web.biz.pojo.request.pts;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junshi
 * @ClassName IdRequest
 * @Description
 * @createTime 2023年03月15日 17:24
 */
@Data
@ApiModel("id入参")
public class IdRequest implements Serializable {

    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id不能为空")
    private Long id;
}
