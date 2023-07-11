package io.shulie.takin.web.common.enums.trace;

import com.pamirs.pradar.Pradar;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务端 客户方
 *
 * @author hezhongqi
 * @date 2021/10/25 14:10
 */
@AllArgsConstructor
@Getter
public enum TraceNodeLogTypeEnum {
    //LOG_TYPE_RPC_CLIENT(0,"客户端"),
    //LOG_TYPE_RPC_SERVER(1,"服务端");
    LOG_TYPE_RPC_CLIENT(0, "被调用方"),
    LOG_TYPE_RPC_SERVER(1, "调用方");
    private Integer logType;
    private String desc;

    public static TraceNodeLogTypeEnum getByLogType(Integer logType) {
        for (TraceNodeLogTypeEnum logTypeEnum : TraceNodeLogTypeEnum.values()) {
            if (logType.equals(logTypeEnum.getLogType())) {
                return logTypeEnum;
            }
        }
        return null;
    }

    /**
     * 传参 amdb的logType
     *
     * @param amdbLogType amdb日志类型
     * @return amdb日志类型枚举
     */
    public static TraceNodeLogTypeEnum judgeTraceNodeLogType(Integer amdbLogType) {
        return amdbLogType == Pradar.LOG_TYPE_INVOKE_CLIENT ? TraceNodeLogTypeEnum.LOG_TYPE_RPC_CLIENT
            : TraceNodeLogTypeEnum.LOG_TYPE_RPC_SERVER;
    }

}
