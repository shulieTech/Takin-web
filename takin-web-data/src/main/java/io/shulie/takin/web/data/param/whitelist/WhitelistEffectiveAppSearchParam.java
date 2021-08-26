package io.shulie.takin.web.data.param.whitelist;

import java.util.List;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/14 10:16 上午
 */
@Data
public class WhitelistEffectiveAppSearchParam {
    /**
     * 租户id
     */
    private Long customerId;

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
