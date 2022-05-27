package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;
import lombok.ToString;

/**
 * @Author: vernon
 * @Date: 2022/5/25 20:15
 * @Description:
 */
@Data
@TableName(value = "t_interface_performance_config_scene_relateship")
@ToString(callSuper = true)
public class InterfacePerformanceConfigSceneRelateShipEntity extends TenantBaseEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接口调试名称
     */
    @TableField(value = "api_id")
    private Long apiId;

    /**
     * 请求地址或者域名
     */
    @TableField(value = "scene_id")
    private Long sceneId;

    /**
     * 软删
     */
    @TableField(value = "is_deleted")
    private int isDeleted;

    /**
     * 业务流程Id
     */
    @TableField(value = "flow_id")
    private Long flowId;

}
