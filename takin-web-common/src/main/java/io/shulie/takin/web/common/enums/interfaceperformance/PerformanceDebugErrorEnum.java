package io.shulie.takin.web.common.enums.interfaceperformance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PerformanceDebugErrorEnum {
    // 异常
    REQUEST_FAILED("请求失败-%s","error");

    private String errorName;
    private String type;
}
