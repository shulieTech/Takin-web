package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务链路管理表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_business_link_manage_table")
public class BusinessLinkManageTableEntity extends UserBaseEntity {
    /**
     * 主键
     */
    @TableId(value = "LINK_ID", type = IdType.AUTO)
    private Long linkId;

    /**
     * 链路名称
     */
    @TableField(value = "LINK_NAME")
    private String linkName;

    /**
     * 链路入口，将之前拼接进去的应用名拆成一个字段
     */
    @TableField(value = "ENTRACE")
    private String entrace;

    /**
     * 入口应用名称
     */
    @TableField(value = "application_name")
    private String applicationName;

    /**
     * 入口应用名称
     */
    @TableField(value = "application_id")
    private Long applicationId;

    /**
     * 业务链路绑定的技术链路
     */
    @TableField(value = "RELATED_TECH_LINK")
    private String relatedTechLink;

    /**
     * 业务链路级别: p0/p1/p2/p3
     */
    @TableField(value = "LINK_LEVEL", updateStrategy = FieldStrategy.IGNORED)
    private String linkLevel;

    /**
     * 业务链路的上级业务链路id
     */
    @TableField(value = "PARENT_BUSINESS_ID")
    private String parentBusinessId;

    /**
     * 是否有变更 0:正常；1:已变更
     */
    @TableField(value = "IS_CHANGE")
    private Integer isChange;

    /**
     * 业务链路是否否核心链路 0:不是;1:是,null表示未知，更新时可能被设置为null
     */
    @TableField(value = "IS_CORE", updateStrategy = FieldStrategy.IGNORED)
    private Integer isCore;

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
     * 业务域: 0:订单域", "1:运单域", "2:结算域
     */
    @TableField(value = "BUSINESS_DOMAIN", updateStrategy = FieldStrategy.IGNORED)
    private String businessDomain;

    /**
     * 是否可以删除 0:可以删除;1:不可以删除
     */
    @TableField(value = "CAN_DELETE")
    private Integer canDelete;

    /**
     * 类型：0：正常业务活动 1：虚拟业务活动
     */
    @TableField(value = "TYPE")
    private Integer type;

    /**
     * 绑定业务活动id
     */
    @TableField(value = "BIND_BUSINESS_ID")
    private Long bindBusinessId;

    /**
     * kafka,rabbitmq,http.....
     */
    @TableField(value = "SERVER_MIDDLEWARE_TYPE")
    private String serverMiddlewareType;

    @TableField(value = "persistence")
    private boolean persistence = true;

    /**
     * 部门id
     */
    @TableField(value = "dept_id")
    private Long deptId;
}
