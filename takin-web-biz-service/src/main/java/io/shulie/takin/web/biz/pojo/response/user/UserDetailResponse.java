package io.shulie.takin.web.biz.pojo.response.user;

import java.util.List;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangXT
 * @date 2020/11/4 14:56
 */
@Data
public class UserDetailResponse {
    @ApiModelProperty(name = "id", value = "用户id")
    @NotBlank
    private Long id;

    @ApiModelProperty(name = "name", value = "用户名称")
    @NotBlank
    private String name;

    @ApiModelProperty(name = "deptIdList", value = "所属部门")
    @NotBlank
    private List<Long> deptIdList;
}
