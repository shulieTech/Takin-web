package io.shulie.takin.web.data.param.whitelist;

import java.util.List;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/14 10:16 上午
 */
@Data
public class WhitelistEffectiveAppSearchParam extends TenantCommonExt {


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 接口名
     */
    private String interfaceName;

    private Long wlistId;

    private List<Long> wlistIds;
}
