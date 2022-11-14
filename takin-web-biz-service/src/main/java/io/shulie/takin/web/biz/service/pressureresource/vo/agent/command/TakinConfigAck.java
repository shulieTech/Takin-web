package io.shulie.takin.web.biz.service.pressureresource.vo.agent.command;

import java.io.Serializable;

/**
 * @author guann1n9
 * @date 2022/9/14 10:12 PM
 */
public class TakinConfigAck implements Serializable {

    /**
     * resource id 确保唯一
     */
    private String configId;


    private String configType;


    private boolean success;


    private String response;


    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
