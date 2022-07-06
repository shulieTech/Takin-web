package com.pamirs.takin.entity.domain.vo.scenemanage;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zhaoyong
 */
@Data
@ApiModel(description = "复制场景入参")
public class SceneManageCopyVO extends ContextExt implements Serializable {

    @NotNull
    private Long id;
}
