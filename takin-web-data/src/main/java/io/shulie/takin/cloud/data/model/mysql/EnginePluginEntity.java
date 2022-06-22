package io.shulie.takin.cloud.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 引擎插件实体
 *
 * @author lipeng
 * @date 2021-01-06 3:18 下午
 */
@Data
@TableName(value = "t_engine_plugin_info")
public class EnginePluginEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 插件名称
     */
    @TableField(value = "plugin_name")
    private String pluginName;

    /**
     * 插件类型
     */
    @TableField(value = "plugin_type")
    private String pluginType;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @TableField(value = "gmt_update")
    private Date gmtUpdate;

    /**
     * 状态  1 启用  0 禁用
     */
    @TableField
    private Boolean status;

}
