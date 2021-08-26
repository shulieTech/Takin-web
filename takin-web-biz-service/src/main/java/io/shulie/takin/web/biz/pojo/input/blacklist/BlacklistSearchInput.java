package io.shulie.takin.web.biz.pojo.input.blacklist;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/6 2:22 下午
 */
@Data
public class BlacklistSearchInput extends PagingDevice {
    /**
     * 黑名单类型
     */
    private Integer type ;

    /**
     * redisKey
     */
    private String redisKey ;

    /**
     * 应用id
     */
    private Long applicationId ;

}
