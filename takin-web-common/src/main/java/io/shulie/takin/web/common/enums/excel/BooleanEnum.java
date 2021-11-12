package io.shulie.takin.web.common.enums.excel;

import lombok.Getter;

/**
 * @author 无涯
 * @date 2021/4/16 1:46 上午
 */
@Getter
public enum BooleanEnum {

    TRUE(true, 1, "是"),
    FALSE(false, 0, "否");

    private Boolean value;

    private Integer code;

    private String desc;

    BooleanEnum(Boolean value, Integer code, String desc) {
        this.value = value;
        this.code = code;
        this.desc = desc;
    }

    public static Boolean getByDesc(String desc) {
        for (BooleanEnum booleanEnum : BooleanEnum.values()) {
            if (booleanEnum.getDesc().equals(desc)) {
                return booleanEnum.value;
            }
        }
        return BooleanEnum.TRUE.value;
    }

    public static String getByValue(Boolean value) {
        return value ? TRUE.getDesc() : FALSE.getDesc();
    }
}
