package io.shulie.takin.web.biz.service.pressureresource.vo.agent.command;

/**
 * @author guann1n9
 * @date 2022/9/14 11:56 PM
 */
public class TakinAck<T> {

    public static final String COMMAND = "COMMAND";

    public static final String CONFIG = "CONFIG";

    private String tenantCode;


    private String envCode;


    private String appName;


    private String ackType;

    private T ack;


    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getEnvCode() {
        return envCode;
    }

    public void setEnvCode(String envCode) {
        this.envCode = envCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAckType() {
        return ackType;
    }

    public void setAckType(String ackType) {
        this.ackType = ackType;
    }

    public T getAck() {
        return ack;
    }

    public void setAck(T ack) {
        this.ack = ack;
    }
}
