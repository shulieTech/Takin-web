package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 白名单管理
 */
@Data
@TableName(value = "t_white_list")
public class WhiteListEntity {
    /**
     * 主键id
     */
    @TableId(value = "WLIST_ID", type = IdType.AUTO)
    private Long wlistId;

    /**
     * 接口名称
     */
    @TableField(value = "INTERFACE_NAME")
    private String interfaceName;

    /**
     * 白名单类型
     */
    @TableField(value = "TYPE")
    private String type;

    /**
     * 字典分类
     */
    @TableField(value = "DICT_TYPE")
    private String dictType;

    @TableField(value = "APPLICATION_ID")
    private Long applicationId;

    /**
     * 负责人工号
     */
    @TableField(value = "PRINCIPAL_NO")
    private String principalNo;

    /**
     * 是否可用(0表示未启动,1表示启动,2表示启用未校验)
     */
    @TableField(value = "USE_YN")
    private Integer useYn;

    /**
     * 租户id
     */
    @TableField(value = "customer_id", fill = FieldFill.INSERT)
    private Long customerId;

    /**
     * 用户id
     */
    @TableField(value = "user_id", fill = FieldFill.INSERT)
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private Date gmtCreate;

    /**
     * 新版变更时间
     */
    @TableField(value = "gmt_modified")
    private Date gmtModified;

    /**
     * 队列名称，TYPE=5时该字段才会有值
     */
    @TableField(value = "QUEUE_NAME")
    private String queueName;

    /**
     * MQ类型, 1ESB 2IBM 3ROCKETMQ 4DPBOOT_ROCKETMQ
     */
    @TableField(value = "MQ_TYPE")
    private String mqType;

    /**
     * IP端口,如1.1.1.1:8080,集群时用逗号分隔;当且仅当TYPE=5,MQ_TYPE=(3,4)时才会有值
     */
    @TableField(value = "IP_PORT")
    private String ipPort;

    /**
     * HTTP类型：1页面 2接口
     */
    @TableField(value = "HTTP_TYPE")
    private Integer httpType;

    /**
     * 页面分类：1普通页面加载 2简单查询页面/复杂界面 3复杂查询页面
     */
    @TableField(value = "PAGE_LEVEL")
    private Integer pageLevel;

    /**
     * 接口类型：1简单操作/查询 2一般操作/查询 3复杂操作 4涉及级联嵌套调用多服务操作 5调用外网操作
     */
    @TableField(value = "INTERFACE_LEVEL")
    private Integer interfaceLevel;

    /**
     * JOB调度间隔：1调度间隔≤1分钟 2调度间隔≤5分钟 3调度间隔≤15分钟 4调度间隔≤60分钟
     */
    @TableField(value = "JOB_INTERVAL")
    private Integer jobInterval;

    /**
     * 逻辑删除, 1 已删除
     */
    @TableLogic
    private Integer isDeleted;


    /**
     * JOB调度间隔：1调度间隔≤1分钟 2调度间隔≤5分钟 3调度间隔≤15分钟 4调度间隔≤60分钟
     */
    @TableField(value = "IS_GLOBAL")
    private Boolean isGlobal;

    /**
     * 是否手工添加
     */
    @TableField(value = "IS_HANDWORK")
    private Boolean isHandwork;


}
