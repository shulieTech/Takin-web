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
public class PressureResourceRemoteCallQueryParam extends PageBaseDTO {
    @ApiModelProperty("查询的接口名")
    private String queryInterfaceName;

    @ApiModelProperty("接口类型")
    private String interfaceChildType;

    @ApiModelProperty("是否放行(0:是 1:否)")
    private Integer pass;

    @ApiModelProperty("状态")
    private Integer status;
}
