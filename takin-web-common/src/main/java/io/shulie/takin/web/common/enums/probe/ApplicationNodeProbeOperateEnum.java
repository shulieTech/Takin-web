package io.shulie.takin.web.common.enums.probe;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liuchuan
 * @date 2021/6/7 11:26 上午
 */
@Getter
@AllArgsConstructor
public enum ApplicationNodeProbeOperateEnum {

    /**
     * 安装
     */
    INSTALL(1),

    /**
     * 升级
     */
    UPGRADE(3),

    /**
     * 卸载
     */
    UNINSTALL(2),

    /**
     * 无
     */
    NONE(0);

    private final Integer code;

    /**
     * 通过 操作类型 获得枚举
     *
     * @param operateType 操作类型
     * @return 操作类型枚举
     */
    public static ApplicationNodeProbeOperateEnum getByCode(Integer operateType) {
        return Arrays.stream(values()).filter(applicationNodeProbeOperateEnum ->
            applicationNodeProbeOperateEnum.getCode().equals(operateType)).findFirst().orElse(null);
    }

}
