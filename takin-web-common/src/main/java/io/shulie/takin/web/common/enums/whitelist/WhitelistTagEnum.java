package io.shulie.takin.web.common.enums.whitelist;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 无涯
 * @date 2021/4/14 9:50 上午
 */
@AllArgsConstructor
@Getter
public enum WhitelistTagEnum {
    DUPLICATE_NAME("重名白名单"),
    MANUALLY_ADD("手工添加");
    private String tagName;
}
