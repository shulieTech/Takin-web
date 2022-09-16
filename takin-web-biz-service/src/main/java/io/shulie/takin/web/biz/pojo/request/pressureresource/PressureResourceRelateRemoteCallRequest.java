package io.shulie.takin.web.biz.pojo.request.pressureresource;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
@ToString(callSuper = true)
public class PressureResourceRelateRemoteCallRequest extends PageBaseDTO {
    @ApiModelProperty("resourceId")
    private Long resourceId;

    @ApiModelProperty("远程调用服务Id")
    private Long id;

    @ApiModelProperty("查询的接口名")
    private String queryInterfaceName;

    @ApiModelProperty("接口类型")
    private String interfaceChildType;

    @ApiModelProperty("是否放行(0:是 1:否)")
    private Integer pass;

    @ApiModelProperty("状态(0-未检测 1-检测失败 2-检测成功)")
    private Integer status;
}

