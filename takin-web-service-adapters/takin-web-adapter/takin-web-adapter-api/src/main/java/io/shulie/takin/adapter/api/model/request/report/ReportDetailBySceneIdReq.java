package io.shulie.takin.adapter.api.model.request.report;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2021/2/3 12:03 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportDetailBySceneIdReq extends ContextExt {
    private Long sceneId;
}
