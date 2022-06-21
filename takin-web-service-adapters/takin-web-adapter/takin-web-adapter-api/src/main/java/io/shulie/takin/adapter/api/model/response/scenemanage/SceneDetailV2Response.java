package io.shulie.takin.adapter.api.model.response.scenemanage;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.enginecall.PtConfigExt;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest.Content;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest.DataValidation;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest.Goal;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest.MonitoringGoal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 场景详情  -  响应
 *
 * @author 张天赐
 */
@Data
@ApiModel(value = "场景详情")
public class SceneDetailV2Response {
    @ApiModelProperty(value = "基础信息")
    @NotBlank(message = "场景基础信息不能为空")
    private SceneRequest.BasicInfo basicInfo;
    @ApiModelProperty(value = "脚本解析结果")
    @NotBlank(message = "脚本解析结果不能为空")
    private List<ScriptNode> analysisResult;
    @ApiModelProperty(value = "压测内容")
    @NotNull(message = "压测目标不能为空")
    private Map<String, Content> content;
    @ApiModelProperty(value = "施压配置")
    @NotNull(message = "施压配置不能为空")
    private PtConfigExt config;
    @ApiModelProperty(value = "压测目标")
    @NotNull(message = "业压测目标不能为空")
    private Map<String, Goal> goal;
    @ApiModelProperty(value = "SLA配置-销毁")
    private List<MonitoringGoal> destroyMonitoringGoal;
    @ApiModelProperty(value = "SLA配置-警告")
    private List<MonitoringGoal> warnMonitoringGoal;
    @ApiModelProperty(value = "数据验证配置")
    @NotNull(message = "数据验证配置不能为空")
    private DataValidation dataValidation;
}
