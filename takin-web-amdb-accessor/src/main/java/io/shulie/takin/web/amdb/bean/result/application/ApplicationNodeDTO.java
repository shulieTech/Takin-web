package io.shulie.takin.web.amdb.bean.result.application;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class ApplicationNodeDTO implements Serializable  {

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
    private Date agentUpdateTime;

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

    /**
     * 探针状态, 0-已安装,1-未安装,2-安装中,3-卸载中,4-安装失败,99-未知状态
     */
    private Integer probeStatus;

}
