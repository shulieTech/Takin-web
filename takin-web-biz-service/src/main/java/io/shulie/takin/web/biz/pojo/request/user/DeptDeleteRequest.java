package io.shulie.takin.web.biz.pojo.request.user;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/3/8 10:57 上午
 */
@Data
@ApiModel("部门删除对象")
public class DeptDeleteRequest {
    @ApiModelProperty(name = "id", value = "部门id")
    @NotNull
    private Long id;
}
