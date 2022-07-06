package io.shulie.takin.web.common.enums.script;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 脚本调试记录状态枚举
 *
 * @author liuchuan
 * @date 2021/5/11 3:24 下午
 */
@AllArgsConstructor
@Getter
public enum ScriptDebugStatusEnum {

    /**
     * 未启动
     */
    NOT_START(0,"未启动"),

    /**
     * 启动中
     */
    STARTING(1,"启动中"),

    /**
     * 请求中
     */
    REQUESTING(2,"请求中"),

    /**
     * 请求结束
     */
    REQUEST_END(3,"请求结束"),

    /**
     * 调试成功
     */
    SUCCESS(4,"调试成功"),

    /**
     * 调试失败
     */
    FAILED(5,"调试失败");

    private final Integer code;

    private final String desc;


    private static final Map<Integer, ScriptDebugStatusEnum> COMMAND_ENUM_MAP = new HashMap<>();

    static {
        Arrays.stream(ScriptDebugStatusEnum.values()).forEach(item -> COMMAND_ENUM_MAP.put(item.getCode(), item));
    }

    public static String getDesc(Integer code){
       return COMMAND_ENUM_MAP.get(code).getDesc();
    }


}
