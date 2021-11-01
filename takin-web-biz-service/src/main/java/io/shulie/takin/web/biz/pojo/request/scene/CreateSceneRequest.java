package io.shulie.takin.web.biz.pojo.request.scene;

import java.util.Map;
import java.util.List;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.shulie.takin.cloud.open.request.scene.manage.WriteSceneRequest;

/**
 * 操作压测场景入参 -新
 *
 * @author 张天赐
 */
@Data
@ApiModel(value = "创建/修改 场景 - 新")
public class CreateSceneRequest {
    @ApiModelProperty(value = "基础信息")
    @NotBlank(message = "场景基础信息不能为空")
    private WriteSceneRequest.BasicInfo basicInfo;
    @ApiModelProperty(value = "脚本解析结果")
    @NotBlank(message = "脚本解析结果不能为空")
    private List<?> analysisResult;
    @ApiModelProperty(value = "压测内容")
    @NotNull(message = "压测目标不能为空")
    private List<WriteSceneRequest.Content> content;
    @ApiModelProperty(value = "施压配置")
    @NotNull(message = "施压配置不能为空")
    private WriteSceneRequest.Config config;
    @ApiModelProperty(value = "压测目标")
    @NotNull(message = "业压测目标不能为空")
    private Map<String, WriteSceneRequest.Goal> goal;
    @ApiModelProperty(value = "SLA配置")
    @NotNull(message = "SLA配置不能为空")
    private List<WriteSceneRequest.MonitoringGoal> monitoringGoal;
    @ApiModelProperty(value = "数据验证配置")
    @NotNull(message = "数据验证配置不能为空")
    private WriteSceneRequest.DataValidation dataValidation;
}
