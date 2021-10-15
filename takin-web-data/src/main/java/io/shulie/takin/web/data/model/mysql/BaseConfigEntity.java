package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * takin配置表
 */
@Data
@TableName(value = "t_base_config")
public class BaseConfigEntity {
    /**
     * 配置编码
     */
    @TableId(value = "CONFIG_CODE", type = IdType.INPUT)
    private String configCode;

    /**
     * 配置值
     */
    @TableField(value = "CONFIG_VALUE")
    private String configValue;

    /**
     * 配置说明
     */
    @TableField(value = "CONFIG_DESC")
    private String configDesc;

    /**
     * 是否可用(0表示未启用,1表示启用)
     */
    @TableField(value = "USE_YN")
    private Integer useYn;

    /**
     * 插入时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME")
    private LocalDateTime updateTime;

    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 环境编码
     */
    @TableField(value = "env_code")
    private String envCode;
}
