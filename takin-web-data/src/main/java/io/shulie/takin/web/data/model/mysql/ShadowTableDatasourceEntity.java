package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 影子表数据源配置
 */
@Data
@TableName(value = "t_shadow_table_datasource")
public class ShadowTableDatasourceEntity {
    /**
     * 影子表数据源id
     */
    @TableId(value = "SHADOW_DATASOURCE_ID", type = IdType.AUTO)
    private Long shadowDatasourceId;

    /**
     * 关联app_mn主键id
     */
    @TableField(value = "APPLICATION_ID")
    private Long applicationId;

    /**
     * 数据库ip端口  xx.xx.xx.xx:xx
     */
    @TableField(value = "DATABASE_IPPORT")
    private String databaseIpport;

    /**
     * 数据库表明
     */
    @TableField(value = "DATABASE_NAME")
    private String databaseName;

    /**
     * 是否使用 影子表 1 使用 0 不使用
     */
    @TableField(value = "USE_SHADOW_TABLE")
    private Integer useShadowTable;

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
