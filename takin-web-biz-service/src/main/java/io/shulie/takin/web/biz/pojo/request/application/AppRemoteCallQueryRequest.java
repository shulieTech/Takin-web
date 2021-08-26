package io.shulie.takin.web.biz.pojo.request.application;

import javax.validation.constraints.NotNull;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/5/29 12:18 上午
 */
@Data
public class AppRemoteCallQueryRequest extends PagingDevice {
    @ApiModelProperty(name = "type", value = "0:全部；1：已加入白名单 2：返回值mock 3:转发mock")
    private Integer type;
    @ApiModelProperty(name = "interfaceName", value = "接口名称 模糊查询")
    private String interfaceName;
    @ApiModelProperty(name = "status", value = "配置状态 0：全部 1：未配置 2：已配置")
    private Integer status;
    @ApiModelProperty(name = "applicationId", value = "应用id")
    @NotNull(message = "应用id不能为空")
    private Long applicationId;
}
