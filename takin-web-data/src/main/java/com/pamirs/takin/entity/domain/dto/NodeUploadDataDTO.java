package com.pamirs.takin.entity.domain.dto;

import java.io.Serializable;
import java.util.Map;

import com.pamirs.takin.common.util.DateUtils;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.Data;

/**
 * 应用上传接入状态信息
 *
 * @author mubai<chengjiacai @ shulie.io>
 * @date 2020-04-03 10:08
 */

@Data
public class NodeUploadDataDTO extends TenantCommonExt implements Serializable {

    private static final long serialVersionUID = 2907229827465846525L;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 节点唯一键
     */
    private String nodeKey;

    /**
     * agent id
     */
    private String agentId;

    /**
     * 开关异常信息
     */
    private Map<String, Object> switchErrorMap;

    /**
     * 异常时间
     */
    private String exceptionTime = DateUtils.getNowDateStr();

    @Override
    public String toString() {
        return "NodeUploadDataDTO{" +
            "applicationName='" + applicationName + '\'' +
            ", nodeKey='" + nodeKey + '\'' +
            ", switchErrorMap=" + switchErrorMap +
            '}';
    }

}
