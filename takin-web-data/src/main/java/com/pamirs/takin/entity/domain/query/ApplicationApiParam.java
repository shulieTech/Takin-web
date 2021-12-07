package com.pamirs.takin.entity.domain.query;

import io.shulie.takin.web.data.common.BaseTenantBean;
import lombok.Data;

/**
 * @author caijianying
 */
@Data
public class ApplicationApiParam extends BaseTenantBean {

    private String appName;
}
