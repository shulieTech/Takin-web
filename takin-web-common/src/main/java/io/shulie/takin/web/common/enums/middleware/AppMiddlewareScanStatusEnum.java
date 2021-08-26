package io.shulie.takin.web.common.enums.middleware;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 无涯
 * @date 2021/2/23 10:35 上午
 */
@Getter
@AllArgsConstructor
public enum AppMiddlewareScanStatusEnum {
    NOT_ENTERED(1, "未录入"),
    NO_SUPPORT_REQUIRED(2, "无需支持"),
    NOT_SUPPORTED(3, "未支持"),
    SUPPORTED(4, "已支持"),
    ;
    private Integer status;
    private String desc;

    public static String getDescByStatus(Integer status) {
        for (AppMiddlewareScanStatusEnum statusEnum : AppMiddlewareScanStatusEnum.values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum.getDesc();
            }
        }
        return "";
    }

}
