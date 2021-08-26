package io.shulie.takin.web.biz.pojo.request.scriptmanage.shell;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ShellScriptManageUpdateRequest extends ShellScriptManageRequest implements Serializable {
    private static final long serialVersionUID = 459472161282660909L;

    /**
     * 脚本实例id
     */
    @JsonProperty("scriptDeployId")
    @ApiModelProperty(name = "scriptDeployId", value = "脚本实例ID")
    private Long scriptDeployId;

    ///**
    // * 脚本实例id
    // */
    //@JsonProperty("oldScriptDeployId")
    //@ApiModelProperty(name = "oldScriptDeployId", value = "原脚本实例ID")
    //private Long oldScriptDeployId;


    /**
     * 脚本版本
     */
    @JsonProperty("scriptVersion")
    @ApiModelProperty(name = "scriptVersion", value = "脚本版本")
    private Integer scriptVersion;



}
