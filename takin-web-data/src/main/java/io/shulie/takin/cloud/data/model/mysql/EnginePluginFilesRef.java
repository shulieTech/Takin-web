package io.shulie.takin.cloud.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 引擎插件文件信息
 *
 * @author lipeng
 * @date 2021-01-13 5:18 下午
 */
@Data
@TableName(value = "t_engine_plugin_files_ref")
public class EnginePluginFilesRef {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "plugin_id")
    private Long pluginId;

    @TableField(value = "file_path")
    private String filePath;

    @TableField(value = "file_name")
    private String fileName;

    @TableField(value = "gmt_create")
    private LocalDateTime gmtCreate;

}
