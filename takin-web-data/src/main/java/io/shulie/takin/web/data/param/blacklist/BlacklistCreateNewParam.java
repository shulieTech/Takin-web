package io.shulie.takin.web.data.param.blacklist;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

import java.util.Date;

/**
 * @author 无涯
 * @date 2021/4/6 2:22 下午
 */
@Data
public class BlacklistCreateNewParam extends UserCommonExt {

    /**
     * 黑名单类型, 默认 0, redisKey
     */
    private Integer type;

    /**
     * redisKey
     */
    private String redisKey;

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 插入时间
     */
    private Date gmtCreate;

    /**
     * 变更时间
     */
    private Date gmtModified;

    /**
     * 是否可用(0表示未启动,1表示启动,2表示启用未校验)
     */
    private Integer useYn;

    public BlacklistCreateNewParam() {
        gmtCreate = new Date();
        gmtModified = new Date();
    }
}
