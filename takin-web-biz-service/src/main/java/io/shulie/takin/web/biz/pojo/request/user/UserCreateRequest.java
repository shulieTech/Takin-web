package io.shulie.takin.web.biz.pojo.request.user;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangXT
 * @date 2020/11/4 14:58
 */
@Data
@ApiModel(value = "UserCreateRequest", description = "新增用户入参")
public class UserCreateRequest {

    @ApiModelProperty(name = "name", value = "用户名称")
    @NotBlank
    private String name;

    @ApiModelProperty(name = "password", value = "密码")
    @NotBlank
    private String password;

    @ApiModelProperty(name = "deptIdList", value = "所属部门")
    @NotEmpty
    private List<Long> deptIdList;

}
