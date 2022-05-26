package io.shulie.takin.cloud.ext.content.enums;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 压测报告、业务活动流量验证、脚本调试、巡检场景(暂时不做)
 *
 * @author caijianying
 */
@Getter
@AllArgsConstructor
public enum AssetTypeEnum {
    /**
     * 压测报告
     */
    PRESS_REPORT(1, "压测报告"),
    /**
     * 业务活动流量验证
     */
    ACTIVITY_CHECK(2, "业务活动流量验证"),
    /**
     * 脚本调试
     */
    SCRIPT_DEBUG(3, "脚本调试"),
    /**
     * 巡检场景
     */
    PATRO_SCENE(4, "巡检场景");

    private Integer code;
    private String name;

    /**
     * 通过 code 获得枚举
     *
     * @param code 状态
     * @return 枚举
     */
    public static AssetTypeEnum getByCode(Integer code) {
        return Arrays.stream(values())
            .filter(middlewareJarStatusEnum -> middlewareJarStatusEnum.code.equals(code))
            .findFirst().orElse(null);
    }
}
