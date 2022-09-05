package io.shulie.takin.web.data.param.pressureresource;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 2:56 下午
 */
@Data
public class PressureResourceTableQueryParam extends PageBaseDTO {
    @ApiModelProperty("业务数据源,模糊查询")
    private String queryBusinessTableName;

    @ApiModelProperty("业务数据源,等值匹配")
    private String businessTableName;

    @ApiModelProperty("状态,等值查询")
    private Integer status;

    @ApiModelProperty("数据源Id")
    private Long dsId;
}
