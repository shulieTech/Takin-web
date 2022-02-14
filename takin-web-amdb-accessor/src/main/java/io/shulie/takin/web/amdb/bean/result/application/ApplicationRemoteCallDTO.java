package io.shulie.takin.web.amdb.bean.result.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 无涯
 * @date 2021/6/2 3:22 下午
 */
@NoArgsConstructor
@Data
public class ApplicationRemoteCallDTO {
    @JsonProperty("appName")
    private String appName;
    @JsonProperty("extend")
    private String extend;
    @JsonProperty("methodName")
    private String methodName;
    @JsonProperty("middlewareName")
    private String middlewareName;
    @JsonProperty("middlewareDetail")
    private String middlewareDetail;
    @JsonProperty("rpcType")
    private String rpcType;
    @JsonProperty("serviceName")
    private String serviceName;
    @JsonProperty("upAppName")
    private String upAppName;
    @JsonProperty("defaultWhiteInfo")
    private String defaultWhiteInfo;

}