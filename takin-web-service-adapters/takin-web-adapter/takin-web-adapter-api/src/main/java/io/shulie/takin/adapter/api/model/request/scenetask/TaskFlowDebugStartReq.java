package io.shulie.takin.adapter.api.model.request.scenetask;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.adapter.api.model.request.engine.EnginePluginsRefOpen;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneBusinessActivityRefOpen;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneScriptRefOpen;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaoyong
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskFlowDebugStartReq extends ContextExt {

    @ApiModelProperty(value = "业务活动配置")
    @NotEmpty(message = "业务活动配置不能为空")
    private List<SceneBusinessActivityRefOpen> businessActivityConfig;

    @ApiModelProperty(value = "脚本类型")
    @NotNull(message = "脚本类型不能为空")
    private Integer scriptType;

    @ApiModelProperty(name = "uploadFile", value = "压测脚本/文件")
    @NotEmpty(message = "压测脚本/文件不能为空")
    private List<SceneScriptRefOpen> uploadFile;

    /**
     * 关联到的插件id
     */
    private List<Long> enginePluginIds;

    @ApiModelProperty(value = "关联到的插件列表")
    private List<EnginePluginsRefOpen> enginePlugins;

    /**
     * 扩展字段
     */
    private String features;

    private Long scriptId;

    private Long scriptDeployId;

    @ApiModelProperty(value = "脚本节点信息")
    private String scriptAnalysisResult;

    /**
     * 操作人
     */
    private String operateName;

    private Long operateId;


}
