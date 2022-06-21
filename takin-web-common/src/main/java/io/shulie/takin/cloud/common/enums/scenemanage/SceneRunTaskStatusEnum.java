package io.shulie.takin.cloud.common.enums.scenemanage;

import java.util.Arrays;

import lombok.Getter;

/**
 * @author xr.l
 * @date 2021-05-10
 */

public enum SceneRunTaskStatusEnum {
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

    @Getter
    private final int code;

    @Getter
    private final String text;

    SceneRunTaskStatusEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public static SceneRunTaskStatusEnum getTryRunTaskStatusEnumByCode(int code) {
        return Arrays.stream(values())
            .filter(statusEnum -> statusEnum.getCode() == code)
            .findFirst()
            .orElse(null);
    }

    public static SceneRunTaskStatusEnum getTryRunTaskStatusEnumByText(String status) {
        return Arrays.stream(values())
            .filter(statusEnum -> statusEnum.getText().equals(status))
            .findFirst()
            .orElse(null);
    }
}
