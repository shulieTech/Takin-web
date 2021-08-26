package io.shulie.takin.web.data.param.whitelist;

import java.util.List;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/14 10:58 上午
 */
@Data
public class WhitelistEffectiveAppDeleteParam {
    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 用户id
     */
    private Long userId;

    private Long wlistId;

    /**
     * 批量用
     */
    private List<Long> wlistIds;

}
