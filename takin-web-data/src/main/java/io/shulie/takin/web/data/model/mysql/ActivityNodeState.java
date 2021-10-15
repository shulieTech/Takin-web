package io.shulie.takin.web.data.model.mysql;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_activity_node_service_state")
public class ActivityNodeState {
    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 业务ID
     */
    @TableField(value = "activity_id")
    private Long activityId;

    /**
     * 所属应用
     */
    @TableField(value = "owner_app")
    private String ownerApp;

    /**
     * 服务名称
     */
    @TableField(value = "service_name")
    private String serviceName;

    /**
     * 状态
     */
    @TableField(value = "state")
    private boolean state;
}
