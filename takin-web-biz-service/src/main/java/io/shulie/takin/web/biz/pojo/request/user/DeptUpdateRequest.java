package io.shulie.takin.web.biz.pojo.request.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/3/8 10:56 上午
 */
@Data
@ApiModel("部门修改对象")
public class DeptUpdateRequest {

    @ApiModelProperty(name = "id", value = "部门id")
    @NotNull
    private Long id;

    @ApiModelProperty(name = "name", value = "部门名称")
    @NotBlank
    private String name;

    @ApiModelProperty(name = "parentId", value = "父级部门id")
    private Long parentId;
}
