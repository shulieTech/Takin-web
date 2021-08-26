package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 * 查询所有流程，包含脚本发布id，名称和版本
 */
@Data
public class ScriptManageSceneManageResponse implements Serializable {

    private static final long serialVersionUID = 4240565404886509082L;

    @ApiModelProperty(name = "id", value = "场景id")
    private String id;

    @ApiModelProperty(name = "name", value = "场景名字")
    @JsonProperty("name")
    private String sceneName;

    @JsonProperty("scriptList")
    private List<ScriptManageDeployActivityResponse> scriptManageDeployResponses;
}
