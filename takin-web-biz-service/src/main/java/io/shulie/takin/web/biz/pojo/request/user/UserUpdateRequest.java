package io.shulie.takin.web.biz.pojo.request.user;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangXT
 * @date 2020/11/4 14:58
 */
@Data
@ApiModel(value = "UserUpdateRequest", description = "更新用户入参")
public class UserUpdateRequest {

    @NotNull
    @ApiModelProperty(name = "id", value = "用户id")
    private Long id;

    @ApiModelProperty(name = "name", value = "用户名称")
    @Size(max = 20)
    @NotBlank
    private String name;

    /**
     * 密码不需要必须填写
     */
    @ApiModelProperty(name = "password", value = "密码")
    private String password;

    @ApiModelProperty(name = "deptIdList", value = "所属部门")
    @NotEmpty
    private List<Long> deptIdList;
}
