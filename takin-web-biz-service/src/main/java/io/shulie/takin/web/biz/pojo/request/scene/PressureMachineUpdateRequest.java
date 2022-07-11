package io.shulie.takin.web.biz.pojo.request.scene;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class PressureMachineUpdateRequest implements Serializable {

    @NotNull(message = "节点名称不能为空")
    private String name;

    private String updateName;
}
