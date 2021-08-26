package io.shulie.takin.web.biz.pojo.request.application;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-29
 */
@Data
@ApiModel("应用入口链路详情返回对象")
public class ApplicationEntranceDetailQueryRequest {

    @NotBlank
    @ApiModelProperty("应用名称")
    private String applicationName;

    @NotNull
    @ApiModelProperty("入口ID")
    private String linkId;

    private String method;

    private String rpcType;

    private String extend;

    private String serviceName;

    private EntranceTypeEnum type;

}
