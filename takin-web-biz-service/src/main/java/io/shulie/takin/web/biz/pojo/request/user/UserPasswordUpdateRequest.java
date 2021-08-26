package io.shulie.takin.web.biz.pojo.request.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/3/8 2:49 下午
 */
@Data
@ApiModel(value = "UserPasswordUpdateRequest", description = "更新密码入参")
public class UserPasswordUpdateRequest {

    @ApiModelProperty(name = "id", value = "用户id")
    @NotNull
    private Long id;

    @ApiModelProperty(name = "oldPassword", value = "旧密码")
    @NotBlank
    private String oldPassword;

    @ApiModelProperty(name = "newPassword", value = "新密码")
    @NotBlank
    private String newPassword;
}
