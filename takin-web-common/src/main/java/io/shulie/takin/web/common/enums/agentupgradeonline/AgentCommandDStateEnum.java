package io.shulie.takin.web.common.enums.agentupgradeonline;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 南风 agent指令数据状态枚举
 * @Date: 2021/11/11 5:05 下午
 */
@Getter
@AllArgsConstructor
public enum AgentCommandDStateEnum {

    SUCCESS(true, "success"),
    FAIL(false, "fail"),
          ;


    private Boolean state;
    private String convertStr;


    private static final Map<String, AgentCommandDStateEnum> convert_map = new HashMap<>();

    private static final Map<Boolean, AgentCommandDStateEnum> state_map = new HashMap<>();

    static {
        Arrays.stream(AgentCommandDStateEnum.values()).forEach(item -> convert_map.put(item.getConvertStr(), item));
        Arrays.stream(AgentCommandDStateEnum.values()).forEach(item -> state_map.put(item.getState(), item));
    }

    public static Boolean getState(String str) {
        if(state_map.containsKey(str)){
            return state_map.get(str).getState();
        }else{
            return null;
        }
    }

    public static String getConvert(Boolean state) {
        if(state_map.containsKey(state)){
            return state_map.get(state).getConvertStr();
        }else{
            return "";
        }
    }

}
