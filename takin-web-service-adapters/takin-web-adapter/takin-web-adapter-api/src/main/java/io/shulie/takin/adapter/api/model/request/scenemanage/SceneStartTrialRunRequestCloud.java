package io.shulie.takin.adapter.api.model.request.scenemanage;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaoyong
 * 试跑启动参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneStartTrialRunRequestCloud extends ContextExt {

    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 压测时长(秒)
     */
    private Long pressureTestSecond;
}
