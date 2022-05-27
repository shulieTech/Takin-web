package io.shulie.takin.cloud.common.enums.scenemanage;

import java.util.Objects;

/**
 * @author moriarty
 */

public enum SceneQueryStatusEnum {
    /**
     * 未启动状态，包含待启动，已停止，强制停止，压测引擎停止压测
     */
    WAIT(0, "未启动"),
    /**
     * 压测中状态，包含压力节点工作中、压测中、压测引擎已启动、
     */
    RUNNING(1, "压测中");

    private Integer code;

    private String value;

    SceneQueryStatusEnum(Integer code,String value){
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static SceneQueryStatusEnum getStatusByCode(Integer code){
        if (Objects.isNull(code)){
            return null;
        }
        for (SceneQueryStatusEnum statusEnum : values()){
            if (statusEnum.getCode().equals(code)){
                return statusEnum;
            }
        }
        return null;
    }
}