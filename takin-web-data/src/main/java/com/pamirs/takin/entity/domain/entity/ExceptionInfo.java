package com.pamirs.takin.entity.domain.entity;

import lombok.Data;

/**
 * Agent上报应用异常状态实体类
 *
 * @author fanxx
 * @date 2020/8/14 下午5:20
 */
@Data
public class ExceptionInfo {
    /**
     * 异常编码
     */
    private String errorCode;
    /**
     * 异常简要
     */
    private String message;
    /**
     * 异常详情
     */
    private String detail;

    @Override
    public String toString() {
        return "->" +
            "异常编码:" + errorCode +
            ", 异常简要:" + message +
            ", 异常详情:" + detail;
    }
}
