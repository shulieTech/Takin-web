package com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 链路标记页面出参
 *
 * @author vernon
 * @date 2019/12/10 00:50
 */
@Data
public class LinkRemarkDto {
    @ApiModelProperty(name = "businessProcessCount", value = "接入业务情况")
    private String businessProcessCount;
    @ApiModelProperty(name = "businessActiveCount", value = "覆盖业务情况")
    private String businessActiveCount;
    @ApiModelProperty(name = "systemProcessCount", value = "涵盖系统流程")
    private String systemProcessCount;
    @ApiModelProperty(name = "systemChangeCount", value = "系统流程变更")
    private String systemChangeCount;
    @ApiModelProperty(name = "onLineApplicationCount", value = "应用接入情况")
    private String onLineApplicationCount;
    @ApiModelProperty(name = "linkGuardCount", value = "挡板数量")
    private String linkGuardCount;

   /* @ApiModelProperty(name = "linkRemarkmiddleWareDtos", value = "链路标记页面中间件信息出参")
    List<LinkRemarkmiddleWareDto> linkRemarkmiddleWareDtos;*/
}
