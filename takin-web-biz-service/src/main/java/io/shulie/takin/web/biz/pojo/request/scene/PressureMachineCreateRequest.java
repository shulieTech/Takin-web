package io.shulie.takin.web.biz.pojo.request.scene;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "创建 压力机")
public class PressureMachineCreateRequest {

    private String nodeIp;

    private String name;

    private String password;
}
