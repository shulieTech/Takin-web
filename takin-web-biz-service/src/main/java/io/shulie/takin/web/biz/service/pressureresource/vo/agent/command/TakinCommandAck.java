package io.shulie.takin.web.biz.service.pressureresource.vo.agent.command;

import java.io.Serializable;

/**
 * @author guann1n9
 * @date 2022/9/14 1:46 PM
 */
public class TakinCommandAck implements Serializable {

    /**
     * 命令id
     */
    private String commandId;

    /**
     * 命令类型
     */
    private String commandType;

    /**
     * 探针检查结果
     */
    private boolean success;

    /**
     * 命令响应
     */
    private String response;


    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
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
