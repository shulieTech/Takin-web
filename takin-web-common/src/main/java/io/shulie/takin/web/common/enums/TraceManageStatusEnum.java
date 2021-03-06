package io.shulie.takin.web.common.enums;

import lombok.Data;

/**
 * @author zhaoyong
 */
public enum TraceManageStatusEnum {

    /**
     * 枚举内容
     */
    TRACE_WAIT(0, "待采集"),
    TRACE_RUNNING(1, "正在采集"),
    TRACE_CLOSE(2, "采集完成"),
    TRACE_TIMEOUT(3, "追踪超时"),
    //TRACE_ERROR_1(4, "追踪失败,启动trace失败"),
    //TRACE_ERROR_2(5, "追踪失败,未获取到trace结果"),
    AGENT_TRACE_ERROR(4, "agent命令执行失败"),
    ;

    private Integer code;
    private String name;

    TraceManageStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }


    public static boolean isRunning(Integer status){
        return TRACE_RUNNING.getCode().equals(status);
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
