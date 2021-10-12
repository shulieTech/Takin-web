package io.shulie.takin.web.data.param.whitelist;

import java.util.List;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/12 4:20 下午
 */
@Data
public class WhitelistSearchParam extends TenantCommonExt {
    private String interfaceName;
    private List<String> interfaceNames;
    private List<Long> ids;
    private Boolean isGlobal;
    private Integer useYn;
}
