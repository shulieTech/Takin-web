package io.shulie.takin.web.common.enums.agentupgradeonline;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description 插件类型
 * @Author ocean_wll
 * @Date 2021/11/10 4:38 下午
 */
@AllArgsConstructor
@Getter
public enum PluginTypeEnum {

    MIDDLEWARE(0, "中间件探针", "plugins/middleware"),
    SIMULATOR(1, "simulator包", "plugins/simulator"),
    AGENT(2, "agent包", "plugins/agent"),
    ;

    /**
     * value
     */
    private Integer code;

    /**
     * 描述
     */
    private String desc;

    /**
     * 包上传根目录
     */
    private String baseDir;

    private static final Map<Integer, PluginTypeEnum> PLUGIN_TYPE_ENUM_MAP = new HashMap<>();

    static {
        Arrays.stream(PluginTypeEnum.values()).forEach(item -> PLUGIN_TYPE_ENUM_MAP.put(item.getCode(), item));
    }

    public static PluginTypeEnum valueOf(Integer code) {
        return PLUGIN_TYPE_ENUM_MAP.get(code);
    }
}
