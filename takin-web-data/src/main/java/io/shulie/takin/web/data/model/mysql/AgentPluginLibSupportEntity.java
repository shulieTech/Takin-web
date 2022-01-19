package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * @author fanxx
 * @date 2020/10/13 上午10:12
 */
@Data
@TableName(value = "t_agent_plugin_lib_support")
public class AgentPluginLibSupportEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 插件id
     */
    @TableField(value = "plugin_id")
    private Long pluginId;

    /**
     * jar包名称
     */
    @TableField(value = "`lib_name`")
    private String libName;

    /**
     * agent支持的中间件版本的正则表达式
     */
    @TableField(value = "`lib_version_regexp`")
    private String libVersionRegexp;

    /**
     * 是否忽略 0: 不忽略 1： 忽略
     */
    @TableField(value = "is_ignore")
    private Boolean isIgnore;

    /**
     * 状态 0: 正常 1： 删除
     */
    @TableField(value = "is_delete")
    private Boolean isDelete;

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
}
