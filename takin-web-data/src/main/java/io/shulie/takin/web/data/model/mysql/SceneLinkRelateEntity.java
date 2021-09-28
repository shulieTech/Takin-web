package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 链路场景关联表
 */
@Data
@TableName(value = "t_scene_link_relate")
public class SceneLinkRelateEntity {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 链路入口
     */
    @TableField(value = "ENTRANCE")
    private String entrance;

    /**
     * 是否有效 0:有效;1:无效
     */
    @TableField(value = "IS_DELETED")
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 业务链路ID
     */
    @TableField(value = "BUSINESS_LINK_ID")
    private String businessLinkId;

    /**
     * 技术链路ID
     */
    @TableField(value = "TECH_LINK_ID")
    private String techLinkId;

    /**
     * 场景ID
     */
    @TableField(value = "SCENE_ID")
    private String sceneId;

    /**
     * 当前业务链路ID的上级业务链路ID
     */
    @TableField(value = "PARENT_BUSINESS_LINK_ID")
    private String parentBusinessLinkId;

    /**
     * 前端数结构对象key
     */
    @TableField(value = "FRONT_UUID_KEY")
    private String frontUuidKey;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
