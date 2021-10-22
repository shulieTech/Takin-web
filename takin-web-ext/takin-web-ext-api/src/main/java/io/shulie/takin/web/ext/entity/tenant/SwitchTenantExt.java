package io.shulie.takin.web.ext.entity.tenant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.ext.entity.tenant
 * @ClassName: SwitchTenantExt
 * @Description: 切换租户类
 * @Date: 2021/10/20 17:14
 */
@Data
public class SwitchTenantExt {
    /**
     * 目标 租户code
     */
    @ApiModelProperty("target tenant code")
    private String targetTenantCode;
    /**
     * 目标 环境code
     */
    @ApiModelProperty("target env code")
    private String targetEnvCode;
}
