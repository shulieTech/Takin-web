package io.shulie.takin.cloud.ext.content.enginecall;

import lombok.Data;

/**
 * @author 莫问
 * @date 2020-05-12
 */
@Data
public class ScheduleRunRequest {

    /**
     * 调度ID
     */
    private Long scheduleId;

    /**
     * 调度参数
     */
    private ScheduleStartRequestExt request;

    /**
     * 策略配置
     */
    private StrategyConfigExt strategyConfig;

    /**
     * 容器中的java内存配置
     */
    private String memSetting;
    /**
     * 采样率
     */
    private Integer traceSampling;

    private String callbackUrl;
}
