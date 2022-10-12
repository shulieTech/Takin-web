package io.shulie.takin.web.ext.entity;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hezhongqi
 * @date 2021/7/29 18:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserCommonExt extends TenantCommonExt {

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "操作人")
    private String operateName;

    @ApiModelProperty(value = "操作人id")
    private String operateId;

    @ApiModelProperty(value = "客户Id")
    private Long customerId;

    @ApiModelProperty(value = "部门id")
    private Long deptId;

    @ApiModelProperty(value = "部门名称")
    private String deptName;
}
