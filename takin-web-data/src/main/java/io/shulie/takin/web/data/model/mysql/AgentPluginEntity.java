package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/13 上午10:12
 */
@Data
@TableName(value = "t_agent_plugin")
public class AgentPluginEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 中间件类型：0:DB、1:CACHE、2:MQ、3:SEARCH
     */
    @TableField(value = "plugin_type")
    private String pluginType;

    /**
     * 中间件名称：Redis、Mysql、Es
     */
    @TableField(value = "plugin_name")
    private String pluginName;

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
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    private Long userId;
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
