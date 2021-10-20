package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description 探针状态枚举
 * @Author ocean_wll
 * @Date 2021/8/19 10:19 上午
 */
@Getter
@AllArgsConstructor
public enum ProbeStatusEnum {
    INSTALLED(0, "安装成功"),
    UNINSTALL(1, "未安装"),
    INSTALLING(2, "安装中"),
    UNINSTALLING(3, "卸载中"),
    INSTALL_FAILED(4, "安装失败"),
    UNINSTALL_FAILED(5, "卸载失败"),
    ;

    private Integer val;
    private String desc;
}
