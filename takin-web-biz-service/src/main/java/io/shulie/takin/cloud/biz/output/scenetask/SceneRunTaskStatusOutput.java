package io.shulie.takin.cloud.biz.output.scenetask;

import java.io.Serializable;

import lombok.Data;

/**
 * @author xr.l
 */
@Data
public class SceneRunTaskStatusOutput implements Serializable {
    private Integer taskStatus;
    private String errorMsg;
}
