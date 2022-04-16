package io.shulie.takin.adapter.api.model.request.report;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2020/12/17 1:25 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JtlDownloadReq extends ContextExt {
    private Long sceneId;
    private Long reportId;
}
