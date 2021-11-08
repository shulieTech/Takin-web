package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 探针状态枚举
 *
 * @author ocean_wll
 * @date 2021/8/19 10:19 上午
 */
@Getter
@AllArgsConstructor
public enum ProbeStatusEnum {
    /**
     * 安装成功
     */
    INSTALLED(0, "安装成功"),
    /**
     * 未安装
     */
    UNINSTALL(1, "未安装"),
    /**
     * 安装中
     */
    INSTALLING(2, "安装中"),
    /**
     * 卸载中
     */
    UNINSTALLING(3, "卸载中"),
    /**
     * 安装失败
     */
    INSTALL_FAILED(4, "安装失败"),
    /**
     * 卸载失败
     */
    UNINSTALL_FAILED(5, "卸载失败"),
    ;

    private Integer val;
    private String desc;
}
