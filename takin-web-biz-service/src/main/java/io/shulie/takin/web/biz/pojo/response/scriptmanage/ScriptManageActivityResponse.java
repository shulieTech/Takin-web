package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 * 业务活动对象
 */
@Data
public class ScriptManageActivityResponse implements Serializable {
    private static final long serialVersionUID = 3865160039625320519L;

    @ApiModelProperty(name = "id", value = "业务活动id")
    private String id;

    @ApiModelProperty(name = "name", value = "业务活动名字")
    @JsonProperty("name")
    private String businessActiveName;

    @JsonProperty("scriptList")
    private List<ScriptManageDeployActivityResponse> scriptManageDeployResponses;
}
