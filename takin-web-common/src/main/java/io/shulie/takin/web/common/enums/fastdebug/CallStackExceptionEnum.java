package io.shulie.takin.web.common.enums.fastdebug;

import java.util.List;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 无涯
 * @date 2021/3/1 10:55 上午
 */
@Getter
@AllArgsConstructor
public enum CallStackExceptionEnum {
    /**
     * 流量标识异常 + 丢失节点 = 其他异常
     */
    NODE_OTHER_EXCEPTION(-1,"其他异常","", Lists.newArrayList()),
    NODE_LOST(0,"丢失节点","", Lists.newArrayList()),
    NODE_SIGN_EXCEPTION(1,"流量标识异常","", Lists.newArrayList()),
    NODE_UNKNOWN(2,"未知节点", "客户端有日志，服务端无日志",
        Lists.newArrayList("1. 先确定下游应用是否接入探针;","2. 请确认下游应用是否有未支持的中间件，前往确认")),
    NODE_EXCEPTION(3,"调用异常", "",Lists.newArrayList());
    private Integer type;
    private String exception;
    private String detail;
    private List<String> suggestion;

    public static CallStackExceptionEnum getExceptionEnumByType(Integer type){
        for(CallStackExceptionEnum exceptionEnum : CallStackExceptionEnum.values()) {
            if(exceptionEnum.type.equals(type)) {
                return  exceptionEnum;
            }
        }
        return null;
    }

}
