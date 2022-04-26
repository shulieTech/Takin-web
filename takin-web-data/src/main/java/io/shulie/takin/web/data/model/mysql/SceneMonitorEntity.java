package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 第三方登录服务表(SceneMonitor)实体类
 *
 * @author liuchuan
 * @date 2021-12-29 10:20:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_scene_monitor")
@ToString(callSuper = true)
public class SceneMonitorEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = 443558278387192357L;

    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 标题
     */
    private String title;

    /**
     * 跳转链接
     */
    private String url;

    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 环境编码
     */
    private String envCode;

}
