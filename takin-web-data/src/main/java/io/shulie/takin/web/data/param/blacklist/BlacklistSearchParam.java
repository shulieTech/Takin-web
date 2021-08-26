package io.shulie.takin.web.data.param.blacklist;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/6 2:22 下午
 */
@Data
public class BlacklistSearchParam extends PagingDevice {
    /**
     * 黑名单类型
     */
    private Integer type ;

    /**
     * 黑名单类型
     */
    private String redisKey ;


    /**
     * 应用id
     */
    private Long applicationId ;


    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 是否管理员
     */
    private Boolean isAdmin;
}
