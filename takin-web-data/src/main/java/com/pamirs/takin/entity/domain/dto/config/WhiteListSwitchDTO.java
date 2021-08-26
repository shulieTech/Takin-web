package com.pamirs.takin.entity.domain.dto.config;

import java.io.Serializable;

import lombok.Data;

/**
 * 白名单开启状态
 *
 * @author qianshui
 * @date 2020/8/18 上午11:50
 */
@Data
public class WhiteListSwitchDTO implements Serializable {

    private static final long serialVersionUID = -578646403705050337L;

    /**
     * 配置编码
     */
    private String configCode;

    /**
     * 开关状态
     * 0-关闭
     * 1-开启
     */
    private Integer switchFlag = 1;

    /**
     * OPENED("已开启",0),
     * OPENING("开启中",1),
     * OPEN_FAILING("开启异常",2),
     * CLOSED("已关闭",3),
     * CLOSING("关闭中",4),
     * CLOSE_FAILING("关闭异常",5)
     */
    private String switchStatus;

    @Deprecated
    public void setSwitchFlag(Integer switchFlag) {
        this.switchFlag = switchFlag;
        if (switchFlag != null && switchFlag == 1) {
            this.setSwitchStatus("OPENED");
        } else {
            this.setSwitchStatus("CLOSED");
        }
    }

    public void setSwitchFlagFix(Boolean value) {
        this.switchFlag = value ? 1 : 0;
        this.setSwitchStatus(value ? "OPENED" : "CLOSED");
    }
}
