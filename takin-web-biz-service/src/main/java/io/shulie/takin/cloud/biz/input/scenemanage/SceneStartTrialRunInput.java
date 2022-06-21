package io.shulie.takin.cloud.biz.input.scenemanage;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaoyong
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneStartTrialRunInput extends ContextExt {

    private Long sceneId;

    /**
     * 压测时长(秒)
     */
    private Long pressureTestSecond;
}
