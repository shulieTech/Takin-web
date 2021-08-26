package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 进程基本数据
 *
 * @author qianshui
 * @date 2020/11/4 上午11:18
 */
@Data
@ApiModel("性能基础数据返回值")
public class ProcessBaseDataResponse implements Serializable {
    private static final long serialVersionUID = 9204151589450625059L;

    @ApiModelProperty(value = "进程id")
    private Long processId;

    @ApiModelProperty(value = "进程名称")
    private String processName;

    @ApiModelProperty(value = "agentId")
    private String agentId;

    @ApiModelProperty(value = "机器ip")
    private String appIp;

    @ApiModelProperty(value = "应用名称")
    private String appName;
}
