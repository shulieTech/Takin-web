package io.shulie.takin.web.amdb.bean.query.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 无涯
 * @date 2021/4/20 2:02 下午
 */
@NoArgsConstructor
@Data
public class ApplicationRemoteCallQueryDTO {
    @JsonProperty("appName")
    private String appName;
    @JsonProperty("currentPage")
    private Integer currentPage;
    @JsonProperty("linkId")
    private Integer linkId;
    @JsonProperty("methodName")
    private String methodName;
    @JsonProperty("middlewareName")
    private String middlewareName;
    @JsonProperty("pageSize")
    private Integer pageSize;
    //0是入口,1是出口
    @JsonProperty("queryTye")
    private String queryTye;
    @JsonProperty("rpcType")
    private String rpcType;
    @JsonProperty("serviceName")
    private String serviceName;
    @JsonProperty("tenant")
    private String tenant;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("fieldNames")
    private String fieldNames = "appName,serviceName,methodName,middlewareName,rpcType,extend,upAppName";
    /**
     * 客户端查询
     */
    @JsonProperty("upAppName")
    private String upAppName;
    /**
     * 租户标识
     */
    private String userAppKey;
    /**
     * 环境编码
     */
    private String envCode;

}
