package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.NewBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 探针心跳数据(AgentReport)实体类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:35:32
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_agent_report")
@ToString(callSuper = true)
public class AgentReportEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = -21607606727710342L;

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 应用名
     */
    private String applicationName;

    /**
     * agentId
     */
    private String agentId;

    /**
     * 节点ip
     */
    private String ipAddress;

    /**
     * 进程号
     */
    private String progressId;

    /**
     * agent版本
     */
    private String agentVersion;

    /**
     * 升级批次 根据升级内容生成MD5
     */
    private String curUpgradeBatch;

    /**
     * 节点状态 0:未知,1:运行中,2:异常,3:待重启,4:休眠,5:卸载
     */
    private Integer status;

    /**
     * agent错误信息
     */
    private String agentErrorInfo;

    /**
     * simulator错误信息
     */
    private String simulatorErrorInfo;

    /**
     * 环境标识
     */
    @TableField(value = "env_code", fill = FieldFill.INSERT)
    private String envCode;

    /**
     * 租户Id
     */
    @TableField(value = "tenant_id", fill = FieldFill.INSERT)
    private Long tenantId;
    /**
     * 逻辑删除
     */
    private Integer isDeleted;

}
