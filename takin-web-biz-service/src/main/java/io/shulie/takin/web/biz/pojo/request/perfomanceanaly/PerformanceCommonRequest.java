package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName PerformanceCommonRequest
 * @Description
 * @author qianshui
 * @Date 2020/11/9 下午5:08
 */
@Data
public class PerformanceCommonRequest implements Serializable {

    private static final long serialVersionUID = -4824698831230176544L;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    /**
     * appIp | agentId
     */
    @ApiModelProperty(value = "进程名称")
    private String processName;
}
