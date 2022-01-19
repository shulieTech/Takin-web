package io.shulie.takin.web.common.enums.agentupgradeonline;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description agent指令执行状态枚举
 * @Author ocean_wll
 * @Date 2021/11/29 4:32 下午
 */
@Getter
@AllArgsConstructor
public enum AgentCommandExecuteStatusEnum {
    FINISHED("finished", "成功"),
    FAILED("failed", "失败"),
    IN_EXECUTE("inExecute", "运行中"),
    ;
    private String code;
    private String desc;

    public static AgentCommandExecuteStatusEnum getByCode(String code) {
        for (AgentCommandExecuteStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}
