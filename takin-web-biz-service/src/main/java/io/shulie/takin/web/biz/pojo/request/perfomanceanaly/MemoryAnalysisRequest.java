package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-11 18:12
 */

@Data
@ApiModel("内存分析入参")
public class MemoryAnalysisRequest implements Serializable {
    private static final long serialVersionUID = -8097021911903550029L;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    /**
     * appIp | agentId
     */
    @ApiModelProperty(value = "进程名称")
    private String processName;

    /**
     * 根据报告id，确定起止时间
     */
    @ApiModelProperty(value = "报告ID")
    private Long reportId;

}
