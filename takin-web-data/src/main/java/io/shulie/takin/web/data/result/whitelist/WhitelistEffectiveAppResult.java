package io.shulie.takin.web.data.result.whitelist;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.web.ext.entity.UserCommonExt;

/**
 * @author 无涯
 * @date 2021/4/14 10:16 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WhitelistEffectiveAppResult extends UserCommonExt {
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