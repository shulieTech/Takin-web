package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 应用管理配置表
 */
@Data
@TableName(value = "t_application_mnt_config")
public class ApplicationMntConfigEntity {
    /**
     * 应用配置id
     */
    @TableId(value = "TAMC_ID", type = IdType.INPUT)
    private Long tamcId;

    /**
     * 应用id
     */
    @TableField(value = "APPLICATION_ID")
    private Long applicationId;

    /**
     * 是否防作弊检查 0不检查 1 检查
     */
    @TableField(value = "CHEAT_CHECK")
    private Integer cheatCheck;

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
}
