package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zhaoyong
 */
@Data
public class ScriptManageDeployRollBackRequest implements Serializable {
    private static final long serialVersionUID = 825243273365255100L;

    @ApiParam(value = "脚本实例id")
    @JsonProperty("scriptDeployId")
    @NotNull(message = "脚本实例id不能为空")
    private Long scriptDeployId;
}
