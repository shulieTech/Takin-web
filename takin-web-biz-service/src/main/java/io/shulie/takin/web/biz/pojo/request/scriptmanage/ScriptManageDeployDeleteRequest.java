package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ScriptManageDeployDeleteRequest implements Serializable {
    private static final long serialVersionUID = 9207364066819470901L;

    @JsonProperty("scriptId")
    @NotNull(message = "脚本id不能为空")
    private Long scriptDeployId;
}
