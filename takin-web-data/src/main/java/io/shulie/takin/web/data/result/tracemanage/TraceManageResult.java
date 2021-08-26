package io.shulie.takin.web.data.result.tracemanage;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zhaoyong
 */
@Data
public class TraceManageResult {

    private Long id;

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 拓展字段
     */
    private String feature;


    private TraceManageDeployResult traceManageDeployResult;
}
