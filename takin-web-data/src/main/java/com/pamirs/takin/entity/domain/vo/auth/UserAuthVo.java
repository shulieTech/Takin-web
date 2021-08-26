package com.pamirs.takin.entity.domain.vo.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/9/8 4:38 PM
 */
@Data
@ApiModel(value = "UserAuthVo", description = "登录返参数")
public class UserAuthVo {
    @ApiModelProperty(name = "id", value = "用户id")
    private Long id;

    @ApiModelProperty(name = "name", value = "登录账号")
    private String name;

    @ApiModelProperty(name = "key", value = "用户key")
    private String key;

    @ApiModelProperty(name = "role", value = "角色")
    private Integer role;

}
