package io.shulie.takin.web.data.param.baseserver;

import lombok.Data;

/**
 * @author mubai
 * @date 2020-10-26 15:55
 */

@Data
public class BaseServerParam {

    private long startTime;

    private long endTime;

    private String applicationName;

    private String appIp;

    private String agentId;

    public BaseServerParam() {

    }

    public BaseServerParam(long startTime, long endTime, String applicationName) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.applicationName = applicationName;
    }
}
