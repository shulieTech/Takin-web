package com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 链路标记页面中间件下拉框出参数
 *
 * @author vernon
 * @date 2019/12/10 00:57
 */
@Data
public class LinkRemarkmiddleWareDto {
    private Long middleWareId;
    @ApiModelProperty(name = "middleWareType", value = "中间件类型")
    private String middleWareType;
    @ApiModelProperty(name = "middleWareName", value = "中间件名字")
    private String middleWareName;
    @ApiModelProperty(name = "version", value = "中间件版本")
    private String version;
    @ApiModelProperty(name = "bussinessProcessCount", value = "设计业务流程")
    private String bussinessProcessCount;
    @ApiModelProperty(name = "systemProcessCount", value = "涉及系统流程")
    private String systemProcessCount;
}
