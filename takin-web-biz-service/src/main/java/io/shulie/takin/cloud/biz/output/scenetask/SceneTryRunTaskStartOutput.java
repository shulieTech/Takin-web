package io.shulie.takin.cloud.biz.output.scenetask;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xr.l
 * @date 2021-05-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTryRunTaskStartOutput extends ContextExt {

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 报告ID
     */
    private Long reportId;

}
