package com.pamirs.takin.entity.domain.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.LongToStringFormatSerialize;
import lombok.Getter;
import lombok.Setter;

/**
 * 应用隔离配置表
 */
@Getter
@Setter
public class TIsolationAppMainConfig extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用id
     */
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    private Long applicationId;

    /**
     * 是否检查网络
     */
    private Integer checkNetwork;

    /**
     * mock应用节点
     */
    private String mockAppNodes;

    /**
     * 隔离应用开关
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private long tenantId;
    /**
     * 用户id
     */
    private long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;

}
