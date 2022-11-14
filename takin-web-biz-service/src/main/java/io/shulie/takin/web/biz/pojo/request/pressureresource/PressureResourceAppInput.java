package io.shulie.takin.web.biz.pojo.request.pressureresource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author XINGCHEN
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
public class PressureResourceAppInput {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("隔离方式(0-未设置 1-影子库 2-影子库/影子表 3-影子表)")
    private Integer isolateType;

    @ApiModelProperty("节点数")
    private Integer nodeNum;

    @ApiModelProperty("是否加入压测范围(0-否 1-是)")
    private Integer joinPressure;
}
