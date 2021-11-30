package io.shulie.takin.web.data.param.blacklist;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/6 2:22 下午
 */
@Data
public class BlacklistSearchParam extends UserCommonExt {
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
     * 是否管理员
     */
    private Boolean isAdmin;
}
