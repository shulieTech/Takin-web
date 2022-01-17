package com.pamirs.takin.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author mubai
 * @date 2021-02-25 10:13
 */
@Getter
@AllArgsConstructor
public enum AppConfigSheetEnum {
    /**
     * 影子库/表
     */
    DADABASE("影子库/表", 10, 0),
    /**
     * 挡板
     */
    GUARD("出口挡板", 3, 1),
    /**
     * job任务
     */
    JOB("Job任务", 5, 2),
    /**
     * 白名单
     */
    WHITE("白名单", 3, 3),
    /**
     * 影子消费者
     */
    CONSUMER("影子消费者", 2, 4),
    /**
     * 黑名单
     */
    BLACK("黑名单", 2, 5),
    /**
     * 插件管理
     */
    PLUGINS_CONFIG("插件管理", 2, 6),
    /**
     * 远程调用
     */
    REMOTE_CALL("远程调用", 5, 7);

    private final String desc;

    private final Integer columnNum;

    /**
     * sheet 下标
     */
    private final Integer sheetNumber;

}
