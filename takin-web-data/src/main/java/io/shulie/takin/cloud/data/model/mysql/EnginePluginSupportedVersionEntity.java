package io.shulie.takin.cloud.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 引擎插件支持版本信息
 *
 * @author lipeng
 * @date 2021-01-12 4:33 下午
 */
@Data
@TableName(value = "t_engine_plugin_supported_versions")
public class EnginePluginSupportedVersionEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 插件ID
     */
    @TableField(value = "plugin_id")
    private Long pluginId;

    /**
     * 支持的版本号
     */
    @TableField(value = "supported_version")
    private String supportedVersion;

    /**
     * 对应的refId
     */
    @TableField(value = "file_ref_id")
    private Long fileRefId;

}