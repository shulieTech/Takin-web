package io.shulie.takin.web.data.result.opsscript;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

/**
 * @author caijianying
 */
@Data
@AllArgsConstructor
public class OpsExecutionVO implements Serializable {
    @ApiModelProperty(value = "日志文本")
    private String content;
    @ApiModelProperty(value = "是否结束 true=执行结束 false=未结束")
    private Boolean end = false;
}
