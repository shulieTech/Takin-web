package com.pamirs.takin.entity.domain.entity.scenemanage;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SceneBusinessActivityRef extends SceneRef implements Serializable {

    private static final long serialVersionUID = -7316897002322891486L;

    private Long id;

    private Long businessActivityId;

    private String businessActivityName;

    private String applicationIds;

    private String bindRef;

    private Integer isDeleted;

    private Date createTime;

    private String createName;

    private Date updateTime;

    private String updateName;

    private String goalValue;

    /**
     * 是否包含压测头
     */
    private Boolean hasPT;

    /**
     * 业务活动类型：0：正常业务活动 1：
     */
    private Integer businessType;

    /**
     *绑定业务活动
     */
    private Long bindBusinessId;
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
