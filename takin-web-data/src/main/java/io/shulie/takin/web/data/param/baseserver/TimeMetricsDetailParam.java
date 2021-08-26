package io.shulie.takin.web.data.param.baseserver;

import lombok.Data;

/**
 * @author mubai
 * @date 2020-10-26 16:40
 */

@Data
public class TimeMetricsDetailParam {

    private String invokeApp;

    private String event;

    private String rpcType;

    private String appName;

    private long sTime;

    private long eTime;
}
