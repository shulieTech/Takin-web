package io.shulie.takin.cloud.common.enums.scenemanage;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author qianshui
 * @date 2020/4/27 上午11:20
 */
@Getter
@AllArgsConstructor
public enum SceneManageStatusEnum {
    /**
     * 待启动
     */
    WAIT(0, "待启动"),
    STARTING(1, "启动中"),
    RECYCLE(-1, "已回收"),
    PRESSURE_TESTING(2, "压测中"),
    JOB_CREATING(3, "job创建中"),
    PRESSURE_NODE_RUNNING(4, "压力节点工作中"),
    ENGINE_RUNNING(5, "压测引擎已启动"),
    STOP(6, "压测引擎停止压测"),
    //特殊情况
    FILE_SPLIT_RUNNING(7, "文件拆分中"),
    FILE_SPLIT_END(8, "文件拆分完成"),
    FAILED(9, "压测失败"),
    // 强制停止两个都停止
    FORCE_STOP(10, "强制停止"),
    RESOURCE_LOCKING(11, "资源锁定中"),
    ;

    private final Integer value;
    private final String desc;

    /**
     * 待启动
     *
     * @return -
     */
    public static SceneManageStatusEnum[] getAll() {
        return SceneManageStatusEnum.values();
    }

    /**
     * 获取可归纳为<strong>待启动</strong>的状态
     * <ul>
     *     <li> 0:待启动</li>
     *     <li> 9:压测失败</li>
     *     <li>10:强制停止</li>
     * </ul>
     *
     * @return 可归纳为<strong>待启动</strong>的状态
     */
    public static ArrayList<SceneManageStatusEnum> getFree() {
        return new ArrayList<SceneManageStatusEnum>(3) {{
            add(WAIT);
            add(FAILED);
            add(FORCE_STOP);
        }};
    }

    /**
     * 获取可归纳为<strong>启动中</strong>的状态
     * <ul>
     *     <li>0:启动中</li>
     *     <li>3:job创建中</li>
     *     <li>4:压力节点工作中</li>
     *     <li>7:文件拆分中</li>
     *     <li>8:文件拆分完成</li>
     * </ul>
     *
     * @return 可归纳为<strong>启动中</strong>的状态
     */
    public static ArrayList<SceneManageStatusEnum> getStarting() {
        return new ArrayList<SceneManageStatusEnum>(5) {{
            add(STARTING);
            add(JOB_CREATING);
            add(PRESSURE_NODE_RUNNING);
            add(FILE_SPLIT_RUNNING);
            add(FILE_SPLIT_END);
        }};
    }

    /**
     * 获取可归纳为<strong>压测中</strong>的状态
     * <ul>
     *     <li>4:压测引擎已启动</li>
     *     <li>2:压测中</li>
     *     <li>6:压测引擎停止压测</li>
     * </ul>
     *
     * @return 可归纳为<strong>压测中</strong>的状态
     */
    public static ArrayList<SceneManageStatusEnum> getWorking() {
        return new ArrayList<SceneManageStatusEnum>(3) {{
            add(ENGINE_RUNNING);
            add(PRESSURE_TESTING);
            add(STOP);
        }};
    }

    public static SceneManageStatusEnum getSceneManageStatusEnum(Integer status) {
        for (SceneManageStatusEnum statusEnum : values()) {
            if (status.equals(statusEnum.getValue())) {
                return statusEnum;
            }
        }
        return null;
    }

    public static Integer getAdaptStatus(Integer status) {
        SceneManageStatusEnum statusEnum = getSceneManageStatusEnum(status);
        if (SceneManageStatusEnum.getFree().contains(statusEnum)) {
            return WAIT.getValue();
        }
        if (SceneManageStatusEnum.getStarting().contains(statusEnum)) {
            return STARTING.getValue();
        }
        if (SceneManageStatusEnum.getWorking().contains(statusEnum)) {
            return PRESSURE_TESTING.getValue();
        }
        return statusEnum == null ? null : statusEnum.getValue();
    }

    public static Boolean ifFinished(Integer status) {
        SceneManageStatusEnum statusEnum = getSceneManageStatusEnum(status);
        return SceneManageStatusEnum.getFree().contains(statusEnum)
            || SceneManageStatusEnum.STOP == statusEnum;
    }

    public static Boolean ifFree(Integer status) {
        SceneManageStatusEnum statusEnum = getSceneManageStatusEnum(status);
        return SceneManageStatusEnum.getFree().contains(statusEnum);
    }
}
