package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 场景表
 */
@Data
@TableName(value = "t_scene")
@EqualsAndHashCode(callSuper = true)
public class SceneEntity extends UserBaseEntity {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 场景名
     */
    @TableField(value = "SCENE_NAME")
    private String sceneName;

    /**
     * 场景所绑定的业务链路名集合
     */
    @TableField(value = "BUSINESS_LINK")
    private String businessLink;

    /**
     * 场景等级 :p0/p1/02/03
     */
    @TableField(value = "SCENE_LEVEL")
    private String sceneLevel;

    /**
     * 是否核心场景 0:不是;1:是
     */
    @TableField(value = "IS_CORE")
    private Integer isCore;

    /**
     * 是否有变更 0:没有变更，1有变更
     */
    @TableField(value = "IS_CHANGED")
    private Integer isChanged;

    /**
     * 是否有效 0:有效;1:无效
     */
    @TableField(value = "IS_DELETED")
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 场景类型，标识1为jmeter上传，默认0
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 存储树状结构
     */
    @TableField(value = "script_jmx_node")
    private String scriptJmxNode;

    /**
     * 脚本实例id
     */
    @TableField(value = "script_deploy_id")
    private Long scriptDeployId;

    /**
     * 关联节点数
     */
    @TableField(value = "link_relate_num")
    private Integer linkRelateNum;

    /**
     * 脚本总节点数
     */
    @TableField(value = "total_node_num")
    private Integer totalNodeNum;

    @TableField(value = "features")
    private String features;

    @TableField(value = "dept_id")
    private String deptId;
}
