package io.shulie.takin.web.data.result.whitelist;

import java.util.Date;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/14 10:16 上午
 */
@Data
public class WhitelistEffectiveAppResult {
    private Long id;

    /**
     * 白名单id
     */
    private Long wlistId;
    /**
     * 接口名
     */
    private String interfaceName;

    /**
     * 类型
     */
    private String type;

    /**
     * 生效应用
     */
    private String effectiveAppName;

    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtModified;

    /**
     * 软删
     */
    private Boolean isDeleted;

}