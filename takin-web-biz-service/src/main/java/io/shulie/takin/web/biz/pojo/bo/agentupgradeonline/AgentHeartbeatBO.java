package io.shulie.takin.web.biz.pojo.bo.agentupgradeonline;

import io.shulie.takin.web.common.enums.fastagentaccess.AgentReportStatusEnum;
import lombok.Data;

/**
 * @Description agent心跳数据
 * @Author ocean_wll
 * @Date 2021/11/17 5:13 下午
 */
@Data
public class AgentHeartbeatBO {

    /**
     * 应用Id
     */
    private Long applicationId;

    /**
     * 应用名
     */
    private String projectName;

    /**
     * agentId
     */
    private String agentId;

    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * 进程号
     */
    private String progressId;

    /**
     * 当前批次号
     */
    private String curUpgradeBatch;

    /**
     * agent状态
     */
    private String agentStatus;

    /**
     * agent错误信息
     */
    private String agentErrorInfo;

    /**
     * simulator状态
     */
    private String simulatorStatus;

    /**
     * simulator错误信息
     */
    private String simulatorErrorInfo;

    /**
     * 卸载状态
     */
    private Integer uninstallStatus;

    /**
     * 休眠状态
     */
    private Integer dormantStatus;

    /**
     * agent版本
     */
    private String agentVersion;

    /**
     * simulator版本
     */
    private String simulatorVersion;

    /**
     * agent依赖的模块版本信息
     */
    private String dependencyInfo;

    /**
     * 是否企业版，开源版就是个空的值，企业版会给个shulieEnterprise
     */
    private String flag;

    /**
     * 节点当前状态
     */
    private AgentReportStatusEnum curStatus;
}
