package io.shulie.takin.web.biz.pojo.request.pressureresource;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author XINGCHEN
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
public class PressureResourceAppRequest extends PageBaseDTO {
    @ApiModelProperty("配置资源ID")
    private Long resourceId;

    @ApiModelProperty("配置资源IDs")
    private List<Long> resourceIds;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("入口")
    private String entry;

    @ApiModelProperty("状态(0正常-1不正常)")
    private Integer status;

    @ApiModelProperty("是否加入压测范围(0-否 1-是)")
    private Integer joinPressure;
}
