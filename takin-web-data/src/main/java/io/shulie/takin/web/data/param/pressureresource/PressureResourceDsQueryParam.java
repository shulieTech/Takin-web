package io.shulie.takin.web.data.param.pressureresource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 2:56 下午
 */
@Data
public class PressureResourceDsQueryParam {
    @ApiModelProperty("业务数据源,模糊查询")
    private String queryBussinessDatabase;

    @ApiModelProperty("业务数据源,等值查询")
    private String bussinessDatabase;

    @ApiModelProperty("资源配置ID")
    private Long resourceId;

    @ApiModelProperty("应用名称,模糊查询")
    private String queryAppName;
}
