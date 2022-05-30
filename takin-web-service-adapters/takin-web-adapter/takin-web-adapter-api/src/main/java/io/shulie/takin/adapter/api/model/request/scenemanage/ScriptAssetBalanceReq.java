package io.shulie.takin.adapter.api.model.request.scenemanage;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author moriarty
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptAssetBalanceReq extends ContextExt {
    private Long sceneId;
    /**
     * 脚本调试主键
     */
    private Long scriptDebugId;
    /**
     * 报告主键
     */
    private Long cloudReportId;
}