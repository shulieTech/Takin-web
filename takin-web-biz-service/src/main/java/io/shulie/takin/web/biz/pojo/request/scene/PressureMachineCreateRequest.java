package io.shulie.takin.web.biz.pojo.request.scene;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "创建 压力机")
public class PressureMachineCreateRequest {

    private String machineName;

    private String machineIp;

    private String userName;

    private String password;

    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("tag")
    private String tag;
}
