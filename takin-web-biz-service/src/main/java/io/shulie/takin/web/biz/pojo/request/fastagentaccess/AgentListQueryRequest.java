package io.shulie.takin.web.biz.pojo.request.fastagentaccess;

import java.util.Date;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 7:39 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("入参-探针列表查询类")
public class AgentListQueryRequest extends PageBaseDTO {

    private static final long serialVersionUID = -1071122907525146897L;

    @ApiModelProperty(value = "应用名")
    private String projectName;

    @ApiModelProperty(value = "agent状态，查询字典：agent_status")
    private Integer agentStatus;

    @ApiModelProperty(value = "探针状态，查询字典：agent_probe_status")
    private Integer probeStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间下线，用于新应用接入时检测，传的值为下载installAgent.sh的时间")
    private Date minUpdateDate;
}
