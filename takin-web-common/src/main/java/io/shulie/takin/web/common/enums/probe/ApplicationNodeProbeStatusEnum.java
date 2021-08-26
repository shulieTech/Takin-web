package io.shulie.takin.web.common.enums.probe;

import io.shulie.takin.web.common.constant.ProbeConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用节点探针状态枚举
 *
 * @author liuchuan
 * @date 2021/6/8 4:07 下午
 */
@Getter
@AllArgsConstructor
public enum ApplicationNodeProbeStatusEnum implements ProbeConstants {

    /**
     * 未安装
     */
    NOT_INSTALLED(0, "未安装"),

    INSTALLING(1, "安装中"),

    INSTALLED(2, "已安装"),

    UPGRADING(11, "升级中"),

    UNINSTALLING(21, "卸载中");

    private final Integer code;

    private final String desc;

}
