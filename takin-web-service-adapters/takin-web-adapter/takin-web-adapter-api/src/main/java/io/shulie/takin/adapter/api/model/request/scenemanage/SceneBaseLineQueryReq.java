package io.shulie.takin.adapter.api.model.request.scenemanage;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;

/**
 * @author zhangz
 * Created on 2024/3/11 18:06
 * Email: zz052831@163.com
 */

@Data
public class SceneBaseLineQueryReq  extends ContextExt {
    private Long sceneId;
    private Long startTime;
    private Long endTime;
    private SceneBaseLineTypeEnum baseLineType;
}
