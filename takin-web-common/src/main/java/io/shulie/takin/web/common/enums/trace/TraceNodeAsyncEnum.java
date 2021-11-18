package io.shulie.takin.web.common.enums.trace;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.common.enums.trace
 * @ClassName: TraceNodeAsyncEnum
 * @Description: TODO
 * @Date: 2021/10/27 14:49
 */
@AllArgsConstructor
@Getter
public enum TraceNodeAsyncEnum {
    ASYNC("异步"),
    SYNCHRONIZE("同步");
    private String desc;
}
