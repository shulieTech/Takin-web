package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;

/**
 * 应用管理表
 */
@Data
@TableName(value = "t_application_mnt")
public class ApplicationMntEntity extends UserBaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 应用id
     */
    @TableField(value = "APPLICATION_ID")
    private Long applicationId;

    /**
     * 应用名称
     */
    @TableField(value = "APPLICATION_NAME")
    private String applicationName;

    /**
     * 应用说明
     */
    @TableField(value = "APPLICATION_DESC")
    private String applicationDesc;

    /**
     * 影子库表结构脚本路径
     */
    @TableField(value = "DDL_SCRIPT_PATH")
    private String ddlScriptPath;

    /**
     * 数据清理脚本路径
     */
    @TableField(value = "CLEAN_SCRIPT_PATH")
    private String cleanScriptPath;

    /**
     * 基础数据准备脚本路径
     */
    @TableField(value = "READY_SCRIPT_PATH")
    private String readyScriptPath;

    /**
     * 铺底数据脚本路径
     */
    @TableField(value = "BASIC_SCRIPT_PATH")
    private String basicScriptPath;

    /**
     * 缓存预热脚本地址
     */
    @TableField(value = "CACHE_SCRIPT_PATH")
    private String cacheScriptPath;

    /**
     * 缓存失效时间(单位秒)
     */
    @TableField(value = "CACHE_EXP_TIME")
    private Long cacheExpTime;

    /**
     * 是否可用(0表示启用,1表示未启用)
     */
    @TableField(value = "USE_YN")
    private Integer useYn;

    /**
     * java agent版本
     */
    @TableField(value = "AGENT_VERSION")
    private String agentVersion;

    /**
     * 节点数量
     */
    @TableField(value = "NODE_NUM")
    private Integer nodeNum;

    /**
     * 接入状态； 0：正常 ； 1；待配置 ；2：待检测 ; 3：异常
     */
    @TableField(value = "ACCESS_STATUS")
    private Integer accessStatus;

    /**
     * OPENED："已开启",OPENING："开启中",OPEN_FAILING："开启异常",CLOSED："已关闭",CLOSING："关闭中",CLOSE_FAILING："关闭异常"
     */
    @TableField(value = "SWITCH_STATUS")
    private String switchStatus;

    /**
     * 接入异常信息
     */
    @TableField(value = "EXCEPTION_INFO")
    private String exceptionInfo;

    /**
     * 插入时间
     */
    @TableField(value = "CREATE_TIME")
    private Date createTime;

    /**
     * 变更时间
     */
    @TableField(value = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 告警人
     */
    @TableField(value = "ALARM_PERSON")
    private String alarmPerson;

    /**
     * pradarAgent版本
     */
    @TableField(value = "PRADAR_VERSION")
    private String pradarVersion;

    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    private Long userId;
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;

}
