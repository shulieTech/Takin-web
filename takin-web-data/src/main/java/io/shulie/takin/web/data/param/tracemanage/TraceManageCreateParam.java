package io.shulie.takin.web.data.param.tracemanage;

import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class TraceManageCreateParam {


    /**
     * 追踪对象
     */
    private String traceObject;

    /**
     * 报告id
     */
    private Long reportId;

    private String agentId;

    /**
     * 服务器ip
     */
    private String serverIp;

    /**
     * 进程id
     */
    private Integer processId;
}
