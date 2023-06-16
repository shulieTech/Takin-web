package io.shulie.takin.web.amdb.bean.query.trace;

import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("入口服务拓扑图查询对象")
public class ApplicationEntranceTopologyQueryRequest {

    @ApiModelProperty("应用名称")
    private String applicationName;

    @NotNull
    @ApiModelProperty("入口id")
    private String linkId;

    private String method;

    private String rpcType;

    private String extend;

    private String serviceName;

    private String nodeId;

    private EntranceTypeEnum type;

    // 原始的链路拓扑图
    private boolean isConvert;

    private String tenantAppKey;

    private String envCode;
}

