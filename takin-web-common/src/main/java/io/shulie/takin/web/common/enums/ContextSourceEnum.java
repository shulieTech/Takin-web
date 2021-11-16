package io.shulie.takin.web.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.common.enums
 * @ClassName: ContextSourceEnum
 * @Description: TODO
 * @Date: 2021/11/17 00:04
 */
@AllArgsConstructor
@Getter
public enum ContextSourceEnum {
    FRONT(0,"前端"),
    AGENT(1,"第三方应用，agent,amdb"),
    JOB(2,"定时任务"),
    ;
    private Integer code;
    private String source;
}
