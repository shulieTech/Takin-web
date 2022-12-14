package io.shulie.takin.web.biz.pojo.request.scene;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class PressureMachineBaseRequest implements Serializable {

    @NotNull(message = "id不能为空")
    private Long id;

    private String benchmarkSuiteName;

    private String tag;
}
