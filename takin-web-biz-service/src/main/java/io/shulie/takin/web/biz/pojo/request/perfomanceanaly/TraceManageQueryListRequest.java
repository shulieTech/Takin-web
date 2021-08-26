package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class TraceManageQueryListRequest implements Serializable {
    private static final long serialVersionUID = -8735572479422484403L;

    /**
     * 报告id
     */
    @ApiModelProperty(value = "报告id")
    private Long reportId;

    @ApiModelProperty(value = "agentId")
    private String agentId;

    /**
     * 服务器ip
     */
    @ApiModelProperty(value = "服务器ip")
    private String serverIp;

    /**
     * 进程id
     */
    @ApiModelProperty(value = "进程id")
    private Integer processId;
}
