package io.shulie.takin.web.biz.pojo.bo.agentupgradeonline;

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
     * 是否需要升级单信息
     */
    private Boolean needUpgradeInfo;
}
