package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 插件状态枚举
 *
 * @author ocean_wll
 * @date 2021/8/19 10:21 上午
 */
@Getter
@AllArgsConstructor
public enum PluginStatusEnum {
    /**
     * 加载成功
     */
    LOAD_SUCCESS("LOAD_SUCCESS", "加载成功"),
    /**
     * 加载失败
     */
    LOAD_FAILED("LOAD_FAILED", "加载失败"),
    /**
     * 禁用
     */
    LOAD_DISABLE("LOAD_DISABLE", "禁用"),
    /**
     * 未加载
     */
    UNLOAD("UNLOAD", "未加载"),
    ;

    private String val;
    private String desc;
}
