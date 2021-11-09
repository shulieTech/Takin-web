package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
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
     * 升级批次 根据升级内容生成MD5
     */
    private String curUpgradeBatch;

    /**
     * 节点状态 0:未知,1:运行中,2:异常,3:待重启,4:休眠,5:卸载
     */
    private Integer status;

    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 用户id
     */
    private Long userId;

}
