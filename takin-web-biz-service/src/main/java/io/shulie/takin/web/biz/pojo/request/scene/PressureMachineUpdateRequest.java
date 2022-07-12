package io.shulie.takin.web.biz.pojo.request.scene;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class PressureMachineUpdateRequest implements Serializable {

    @NotNull(message = "更新id不能为空")
    private Long id;

//    private String machineName;

    private String userName;

    private String password;
}
