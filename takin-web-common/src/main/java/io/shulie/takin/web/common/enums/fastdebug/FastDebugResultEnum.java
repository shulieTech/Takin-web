package io.shulie.takin.web.common.enums.fastdebug;

import lombok.Getter;

/**
 * @author 无涯
 * @date 2020/12/28 4:03 下午
 */
@Getter
public enum FastDebugResultEnum {
    ERROR(false, "失败"),
    SUCCESS(true, "成功");
    private Boolean status;
    private String desc;

    FastDebugResultEnum(Boolean status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static String getDescByStatus(Boolean status) {
        return status?SUCCESS.desc:ERROR.desc;
    }

}
