package com.pamirs.takin.entity.domain.entity.scenemanage;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/4/18 下午3:39
 */
@Data
public class SceneRef implements Serializable {

    private static final long serialVersionUID = 8847426492729256204L;

    private Long sceneId;
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
