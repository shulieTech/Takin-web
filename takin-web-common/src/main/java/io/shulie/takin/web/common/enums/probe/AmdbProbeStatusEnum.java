package io.shulie.takin.web.common.enums.probe;

import io.shulie.takin.web.common.constant.ProbeConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * agent 那边对应的探针状态枚举
 *
 * @author liuchuan
 * @date 2021/6/8 4:07 下午
 */
@Getter
@AllArgsConstructor
public enum AmdbProbeStatusEnum implements ProbeConstants {

    /**
     * 已安装
     */
    INSTALLED(0, "已安装"),

    NOT_INSTALLED(1, "未安装"),

    INSTALLING(2, "安装中"),

    UNINSTALLING(3, "卸载中"),

    INSTALL_FAILED(4, "安装失败"),

    UNINSTALL_FAILED(5, "卸载失败"),

    UNKNOWN(99, "未知状态");

    private final Integer code;

    private final String desc;

}
