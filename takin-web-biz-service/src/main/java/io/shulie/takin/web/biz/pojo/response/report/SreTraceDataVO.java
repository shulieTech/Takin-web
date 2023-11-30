package io.shulie.takin.web.biz.pojo.response.report;

import lombok.Data;

/**
 * @author zhangz
 * Created on 2023/11/29 19:57
 * Email: zz052831@163.com
 */

@Data
public class SreTraceDataVO {
    private String chainCode;
    private String spanId;
    private String parentSpanId;
    private String appName;
    private String service;
    private String serviceCode;
    private String parentServiceCode;
    private String method;
    private String middlewareName;
    private String comment;
    private String startTime;
    private Double cost;
    private Object gap;
    private Double innerCost;
    private String detail;
    private String callType;
    private Integer async;
}
