package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 应用关注表
 */
@Data
@TableName(value = "t_application_focus")
public class ApplicationAttentionListEntity {

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 应用名称
     */
    @TableField(value = "app_name")
    private String applicationName;

    /**
     * 应用说明
     */
    @TableField(value = "interface_name")
    private String interfaceName;

    /**
     * 是否关注
     */
    @TableField(value = "focus")
    private boolean focus;
}
