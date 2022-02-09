package io.shulie.takin.web.api.internal;

import java.time.LocalDateTime;

/**
 * 调用方信息
 * @author caijianying
 */
public class CallerInfo {
    /**
     * token
     */
    private String accessToken;
    /**
     * 失效时间
     */
    private LocalDateTime expireDateTime;

    /**
     * 调用方id
     */
    private Long callerUserId;

    /**
     * 调用方name
     */
    private String callerUserName;

    /**
     * 环境code
     */
    private String envCode;

    /**
     * 租户code
     */
    private String tenantCode;

    private Long tenantId;

    private String tenantAppKey;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantAppKey() {
        return tenantAppKey;
    }

    public void setTenantAppKey(String tenantAppKey) {
        this.tenantAppKey = tenantAppKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public LocalDateTime getExpireDateTime() {
        return expireDateTime;
    }

    public void setExpireDateTime(LocalDateTime expireDateTime) {
        this.expireDateTime = expireDateTime;
    }

    public Long getCallerUserId() {
        return callerUserId;
    }

    public void setCallerUserId(Long callerUserId) {
        this.callerUserId = callerUserId;
    }

    public String getCallerUserName() {
        return callerUserName;
    }

    public void setCallerUserName(String callerUserName) {
        this.callerUserName = callerUserName;
    }

    public String getEnvCode() {
        return envCode;
    }

    public void setEnvCode(String envCode) {
        this.envCode = envCode;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }
}
