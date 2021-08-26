package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 链路中间件关联表
 */
@Data
@TableName(value = "t_middleware_link_relate")
public class MiddlewareLinkRelateEntity {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 中间件id
     */
    @TableField(value = "MIDDLEWARE_ID")
    private String middlewareId;

    /**
     * 技术链路id
     */
    @TableField(value = "TECH_LINK_ID")
    private String techLinkId;

    /**
     * 业务链路id
     */
    @TableField(value = "BUSINESS_LINK_ID")
    private String businessLinkId;

    /**
     * 场景id
     */
    @TableField(value = "SCENE_ID")
    private String sceneId;

    /**
     * 是否有效 0:有效;1:无效
     */
    @TableField(value = "IS_DELETED")
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME")
    private LocalDateTime updateTime;
}
