package com.pamirs.takin.common.constant;

/**
 * 说明: 压测启动,停止,暂停枚举类
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2018/7/2 18:22
 */
public enum PressureOperateEnum {

    /**
     * 说明：启动压测
     */
    PRESSURE_START,

    /**
     * 说明：停止压测
     */
    PRESSURE_STOP,

    /**
     * 说明：暂停压测
     */
    PRESSURE_PAUSE,

    /**
     * 说明：数据构建调试开关
     */
    DATA_BUILD_DEBUG_SWITCH,

    /**
     * 说明：压测检测调试开关
     */
    PRESSURE_CHECK_DEBUG_SWITCH,
    ;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
