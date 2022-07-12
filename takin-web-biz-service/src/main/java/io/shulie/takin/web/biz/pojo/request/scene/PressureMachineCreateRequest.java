package io.shulie.takin.web.biz.pojo.request.scene;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "创建 压力机")
public class PressureMachineCreateRequest {

    private String machineName;

    private String machineIp;

    private String userName;

    private String password;

}
