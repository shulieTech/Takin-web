package io.shulie.takin.cloud.biz.output.scenetask;

import lombok.Data;

/**
 * @author xr.l
 */
@Data
public class SceneTryRunTaskStatusOutput {
    private Integer taskStatus;
    private String errorMsg;
}
