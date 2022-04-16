package io.shulie.takin.cloud.common.enums.scenemanage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fanxx
 * @date 2020/4/20 下午2:36
 */
@Getter
@AllArgsConstructor
public enum TaskStatusEnum {
    /**
     * 待检测
     */
    NOT_START("待检测"),
    /**
     * 启动成功
     */
    STARTED("启动成功"),
    /**
     * 启动失败
     */
    FAILED("启动失败"),
    /**
     * 压测中
     */
    RUNNING("压测中"),
    /**
     * 压测结束
     */
    FINISHED("压测结束");
    private String status;

}
