package io.shulie.takin.web.amdb.bean.result.application;

import java.io.Serializable;

import lombok.Data;

/**
 * ocean_wll 强调：由于大数据接口那边命名有些错误，探针前缀为agent，agent前缀为probe
 */
@Data
public class ApplicationNodeAgentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 多个应用集合，用,分割
     */
    private String appNames;

    /**
     * agentId
     */
    private String agentId;

    /**
     * 探针开发语言
     */
    private String agentLanguage;

    /**
     * 探针版本
     */
    private String agentVersion;

    /**
     * 探针md5值
     */
    private String agentMd5;

    /**
     * 探针最后修改时间
     */
    private String agentUpdateTime;

    /**
     * 进程号
     */
    private String progressId;

    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * 探针版本
     */
    private String probeVersion;

    //=============================接下来为探针部分配置信息=============================
    /**
     * 探针状态
     */
    private String agentStatus;

    /**
     * 探针错误码
     */
    private String agentErrorCode;

    /**
     * 探针错误信息
     */
    private String agentErrorMsg;

    //=============================接下来为agent部分配置信息============================
    /**
     * agent状态
     */
    private String probeStatus;

    /**
     * agent错误码
     */
    private String errorCode;

    /**
     * agent错误信息
     */
    private String errorMsg;

}
