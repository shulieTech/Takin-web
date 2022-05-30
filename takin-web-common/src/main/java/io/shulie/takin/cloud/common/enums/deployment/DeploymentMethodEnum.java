package io.shulie.takin.cloud.common.enums.deployment;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 无涯
 * @date 2021/4/25 1:58 下午
 */
@Getter
@AllArgsConstructor
public enum DeploymentMethodEnum {
    /**
     * 私有化
     */
    PRIVATE(0, "private"),
    /**
     * 公开的
     */
    PUBLIC(1, "public");
    public final Integer type;
    private final String desc;

    private static final Map<String, DeploymentMethodEnum> DESC_INSTANCES = new HashMap<>();
    private static final Map<Integer, DeploymentMethodEnum> CODE_INSTANCES = new HashMap<>();

    static {
        for (DeploymentMethodEnum e : DeploymentMethodEnum.values()) {
            DESC_INSTANCES.put(e.getDesc(), e);
            CODE_INSTANCES.put(e.getType(), e);
        }
    }

    public static DeploymentMethodEnum valueBy(String desc) {
        if (StrUtil.isBlank(desc)) {
            return null;
        }
        return DESC_INSTANCES.get(desc);
    }

    public static String getByType(Integer type) {
        DeploymentMethodEnum methodEnum = CODE_INSTANCES.get(type);
        // 默认私有化
        return methodEnum == null ? DeploymentMethodEnum.PRIVATE.getDesc() : methodEnum.getDesc();
    }
}
