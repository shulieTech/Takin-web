package io.shulie.takin.web.ext.entity.tenant;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.ext.entity.tenant
 * @ClassName: TenantCommonExt
 * @Description: TODO
 * @Date: 2021/9/28 10:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantCommonExt extends PagingDevice {
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;

    @ApiModelProperty(value = "环境")
    private String envCode;

    @ApiModelProperty(value = "租户code")
    private String userAppKey;
}
