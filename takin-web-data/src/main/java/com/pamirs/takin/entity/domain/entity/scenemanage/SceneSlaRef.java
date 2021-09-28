package com.pamirs.takin.entity.domain.entity.scenemanage;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SceneSlaRef extends SceneRef implements Serializable {

    private static final long serialVersionUID = 7858797202756518503L;

    private Long id;

    private String slaName;

    private String businessActivityIds;

    private Integer targetType;

    private Integer status;

    private Integer isDeleted;

    private Date createTime;

    private String createName;

    private Date updateTime;

    private String updateName;

    private String condition;

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
