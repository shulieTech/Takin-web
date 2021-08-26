package io.shulie.takin.web.biz.pojo.request.user;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/3/8 5:43 下午
 */
@Data
@ApiModel(value = "UserDetailQueryRequest", description = "用户详情入参")
public class UserDetailQueryRequest {
    @ApiModelProperty(name = "id", value = "用户id")
    @NotNull
    private Long id;
}
