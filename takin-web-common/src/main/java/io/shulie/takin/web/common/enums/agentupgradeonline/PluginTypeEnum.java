package io.shulie.takin.web.common.enums.agentupgradeonline;

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

    MIDDLEWARE(0, "中间件探针", "plugins/agent"),
    SIMULATOR(1, "simulator包", "plugins/simulator"),
    AGENT(2, "agent包", "plugins/middleware"),
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
}
