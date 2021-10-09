package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description 插件状态枚举
 * @Author ocean_wll
 * @Date 2021/8/19 10:21 上午
 */
@Getter
@AllArgsConstructor
public enum PluginStatusEnum {
    LOAD_SUCCESS("LOAD_SUCCESS", "加载成功"),
    LOAD_FAILED("LOAD_FAILED", "加载失败"),
    LOAD_DISABLE("LOAD_DISABLE", "禁用"),
    UNLOAD("UNLOAD", "未加载"),
    ;

    private String val;
    private String desc;
}
