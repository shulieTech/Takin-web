package com.pamirs.takin.entity.domain.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 应用上传接入状态信息
 * @author mubai<chengjiacai @ shulie.io>
 * @date 2020-04-03 10:08
 */

@Data
public class ApplicationUploadDTO implements Serializable {

    private static final long serialVersionUID = 2907229827465846525L;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 接入状态： 0：正常 ； 1；待配置 ；2：待检测 ; 3：异常
     */
    private Integer accessStatus;

    /**
     * 异常信息
     */
    private String exceptionInfo;

    /**
     * 异常类型： 如mq 、 dataSource 、linkGuard ...
     */
    private String exceptionType;

    /**
     * 节点唯一键
     */
    private String nodeKey;

    /**
     * 节点ip
     */
    private String nodeIP;

    /**
     * 开关状态
     */
    private String switchStatus;

    /**
     * 开关异常信息
     */
    private Map<String, Object> switchErrorMap;

    /**
     * 节点列表信息
     */
    private List<ApplicationUploadDTO> nodeListInfo;

    @Override
    public String toString() {
        return "ApplicationUploadDTO{" +
            "applicationName='" + applicationName + '\'' +
            ", accessStatus=" + accessStatus +
            ", exceptionInfo='" + exceptionInfo + '\'' +
            ", exceptionType='" + exceptionType + '\'' +
            ", nodeKey='" + nodeKey + '\'' +
            ", nodeIP='" + nodeIP + '\'' +
            ", switchStatus='" + switchStatus + '\'' +
            '}';
    }
}
