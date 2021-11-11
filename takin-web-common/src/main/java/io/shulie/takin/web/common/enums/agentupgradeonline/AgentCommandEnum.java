package io.shulie.takin.web.common.enums.agentupgradeonline;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description agent指令枚举类
 * @Author ocean_wll
 * @Date 2021/11/11 2:25 下午
 */
@Getter
@AllArgsConstructor
public enum AgentCommandEnum {
    HEARTBEAT("100000", "心跳"),
    REPORT_AGENT_UPLOAD_PATH_STATUS("100100", "检测用户配置的存放插件地址是否有效"),
    DOWNLOAD_SIMULATOR_ZIP("110000", "下载simulator包"),
    ;

    private String command;
    private String desc;

    private static final Map<String, AgentCommandEnum> COMMAND_ENUM_MAP = new HashMap<>();

    static {
        Arrays.stream(AgentCommandEnum.values()).forEach(item -> COMMAND_ENUM_MAP.put(item.getCommand(), item));
    }

    public static AgentCommandEnum getEnum(String command) {
        return COMMAND_ENUM_MAP.get(command);
    }
}
