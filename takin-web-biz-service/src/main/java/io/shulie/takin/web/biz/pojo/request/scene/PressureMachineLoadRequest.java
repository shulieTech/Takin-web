package io.shulie.takin.web.biz.pojo.request.scene;

import io.shulie.takin.web.data.model.mysql.MachineManageEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PressureMachineLoadRequest {
    @NotNull(message = "id不能为空")
    private Long id;

    private String benchmarkSuiteName;

    private String tag;

    private String baseIp;

    private String bashPass;

    private String baseUserName;

    private MachineManageEntity machineManageEntity;
}
