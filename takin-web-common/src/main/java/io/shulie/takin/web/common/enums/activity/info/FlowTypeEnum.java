package io.shulie.takin.web.common.enums.activity.info;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlowTypeEnum {

    BLEND("BLEND"),
    PRESSURE_MEASUREMENT("PRESSURE_MEASUREMENT"),
    BUSINESS("BUSINESS"),
    ;

    private String code;
}
