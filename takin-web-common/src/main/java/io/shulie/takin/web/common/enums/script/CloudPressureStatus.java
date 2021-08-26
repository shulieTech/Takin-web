package io.shulie.takin.web.common.enums.script;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 压测状态
 *
 * @author liuchuan
 * @date 2021/5/12 2:50 下午
 */
@AllArgsConstructor
@Getter
public enum CloudPressureStatus {

    /**
     * 启动中
     */
    STARTING(1, "starting"),
    /**
     * 启动成功
     */
    STARTED(2, "started"),
    /**
     * 启动失败
     */
    FAILED(3, "failed"),
    /**
     * 试跑完成
     */
    ENDED(4, "ended"),

    /**
     * 运行中
     */
    RUNNING(5, "running");

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 通过状态获得枚举
     *
     * @param status 状态
     * @return 枚举
     */
    public static CloudPressureStatus getByStatus(Integer status) {
        return status == null ? null
            : Arrays.stream(values()).filter(statusEnum -> statusEnum.getCode().equals(status))
                .findFirst().orElse(null);
    }

}
