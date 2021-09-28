package com.pamirs.takin.entity.domain.entity.linkmanage;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/12/3 10:19
 */
@Data
public class SceneAndBusinessLink {

    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 技术链路id
     */
    private Long techId;
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
