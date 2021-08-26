package io.shulie.takin.web.ext.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModelProperty;
import io.shulie.takin.common.beans.page.PagingDevice;

/**
 * @author hezhongqi
 * @date 2021/7/29 18:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserCommonExt extends PagingDevice {

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "租户ID")
    private Long customerId;

    @ApiModelProperty(value = "操作人")
    private String operateName;

    @ApiModelProperty(value = "操作人id")
    private String operateId;



}
