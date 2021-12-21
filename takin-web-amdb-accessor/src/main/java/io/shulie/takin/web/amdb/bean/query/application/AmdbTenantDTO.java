package io.shulie.takin.web.amdb.bean.query.application;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/12/21 3:54 下午
 * @Description:
 */
@Data
@Builder
public class AmdbTenantDTO {
    /**
     * 租户key
     */
    private String tenantAppKey;

    /**
     * 环境编码
     */
    private String envCode;
}
