package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;

/**
 * 应用中间件列表信息
 */
@Data
@TableName(value = "t_app_middleware_info")
public class AppMiddlewareInfoEntity extends UserBaseEntity {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 应用ID
     */
    @TableField(value = "APPLICATION_ID")
    private Long applicationId;

    /**
     * jar名称
     */
    @TableField(value = "JAR_NAME")
    private String jarName;

    /**
     * Pradar插件名称
     */
    @TableField(value = "PLUGIN_NAME")
    private String pluginName;

    /**
     * Jar类型
     */
    @TableField(value = "JAR_TYPE")
    private String jarType;

    /**
     * 是否增强成功 0:有效;1:未生效
     */
    @TableField(value = "ACTIVE")
    private Boolean active;

    /**
     * 是否隐藏 0:隐藏;1:不隐藏
     */
    @TableField(value = "HIDDEN")
    private Boolean hidden;

    /**
     * 是否有效 0:有效;1:无效
     */
    @TableField(value = "IS_DELETED")
    private Boolean isDeleted;

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
