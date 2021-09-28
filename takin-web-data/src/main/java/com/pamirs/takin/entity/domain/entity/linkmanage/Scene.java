package com.pamirs.takin.entity.domain.entity.linkmanage;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * t_scene
 *
 * @author
 */
@Data
public class Scene implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    private Long id;
    /**
     * 场景名
     */
    private String sceneName;
    /**
     * 场景所绑定的业务链路名集合
     */
    private String businessLink;
    /**
     * 场景等级 :p0/p1/02/03
     */
    private String sceneLevel;
    /**
     * 是否核心场景 0:不是;1:是
     */
    private Integer isCore;
    /**
     * 是否有变更 0:没有变更，1有变更
     */
    private Integer isChanged;
    /**
     * 是否有效 0:有效;1:无效
     */
    private Integer isDeleted;
    /**
     * 插入时间
     */
    private Date createTime;
    /**
     * 变更时间
     */
    private Date updateTime;
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
