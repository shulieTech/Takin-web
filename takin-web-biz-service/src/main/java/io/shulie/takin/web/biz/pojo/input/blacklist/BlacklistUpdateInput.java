package io.shulie.takin.web.biz.pojo.input.blacklist;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/6 2:22 下午
 */
@Data
public class BlacklistUpdateInput {

    /**
     * 主键id
     */
    private Long blistId;

    /**
     * 黑名单类型
     */
    private Integer type ;

    /**
     * rediskey
     */
    private String redisKey ;


    /**
     * 应用id
     */
    private Long applicationId ;



    /**
     * 是否可用(0表示未启动,1表示启动,2表示启用未校验)
     */
    private Integer useYn;


}
