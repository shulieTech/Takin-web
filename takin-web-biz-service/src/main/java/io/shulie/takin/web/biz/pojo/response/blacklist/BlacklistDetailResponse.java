package io.shulie.takin.web.biz.pojo.response.blacklist;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/6 2:25 下午
 */
@Data
public class BlacklistDetailResponse extends UserCommonExt {
    /**
     * 主键id
     */
    private Long blistId;

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

    /**
     * 插入时间
     */
    private String gmtCreate;

    /**
     * 变更时间
     */
    private String gmtModified;

    /**
     * 是否可用(0表示未启动,1表示启动,2表示启用未校验)
     */
    private Integer useYn;

}
