package io.shulie.takin.web.data.param.perfomanceanaly;

import lombok.Data;

/**
* @author qianshui
 * @date 2020/11/4 下午2:35
 */
@Data
public class PerformanceBaseQueryParam {

    private String agentId;

    private String appName;

    private String appIp;

    private String startTime;

    private String endTime;
}
