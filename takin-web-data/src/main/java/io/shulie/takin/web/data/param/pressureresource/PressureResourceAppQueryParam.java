package io.shulie.takin.web.data.param.pressureresource;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 2:56 下午
 */
@Data
public class PressureResourceAppQueryParam extends PageBaseDTO {
    @ApiModelProperty("配置资源ID")
    private Long resourceId;

    @ApiModelProperty("配置资源ID")
    private List<Long> resourceIds;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("入口")
    private String entranceUrl;

    @ApiModelProperty("状态(0 不正常-1正常)")
    private Integer status;

    @ApiModelProperty("是否加入压测范围(0-否 1-是)")
    private Integer joinPressure;

    @ApiModelProperty("配置详情Id")
    private Long detailId;

    private List<String> appNames;
}


