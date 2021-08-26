package io.shulie.takin.web.data.result.application;

import lombok.Data;

/**
 * 节点信息
 *
 * @author mubai
 * @date 2020-09-23 19:01
 */

@Data
public class ApplicationNodeResult {

    private String appId;
    private String appName;
    private String nodeIp;
    private String processNo;
    private String agentLanguage;
    private String agentVersion;
    private String md5Value;
    private String createTime;
    private String updateTime;

    /**
     * agentId
     */
    private String agentId;

    /**
     * 探针版本
     */
    private String probeVersion;

    /**
     * 探针状态
     */
    private Integer probeStatus;

}
