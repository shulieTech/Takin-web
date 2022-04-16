package com.pamirs.takin.cloud.entity.domain.vo.scenemanage;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2020/10/22 8:07 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManageQueryVO extends ContextExt {

    private Long sceneId;

    private String sceneName;

    private Integer status;

    private Integer type;

}
