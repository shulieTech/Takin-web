package io.shulie.takin.web.data.param.perfomanceanaly;

import lombok.Data;

import java.util.Date;

/**
 * @author mubai
 * @date 2020-11-09 11:37
 */

@Data
public class MemoryDumpQueryParam {

    private String appName;

    private String appIp;

    private String agentId;

    private Date startTime;

    private Date endTime;
}
