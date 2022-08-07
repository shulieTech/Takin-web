package io.shulie.takin.adapter.api.model.request.scenemanage;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.adapter.api.model.common.TimeBean;
import io.shulie.takin.web.ext.entity.tenant.EngineType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2020/10/22 7:49 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "场景保存入参")
public class SceneManageWrapperReq extends ContextExt {

    @ApiModelProperty(name = "id", value = "压测场景ID")
    private Long id;

    @ApiModelProperty(value = "压测场景名称")
    @NotNull(message = "压测场景名称不能为空")
    private String pressureTestSceneName;

    @ApiModelProperty(value = "业务活动配置")
    @NotEmpty(message = "业务活动配置不能为空")
    private List<SceneBusinessActivityRefOpen> businessActivityConfig;

    @ApiModelProperty(value = "施压类型,0:并发,1:tps,2:自定义;不填默认为0")
    private Integer pressureType;

    @ApiModelProperty(value = "并发数量")
    private Integer concurrenceNum;

    @ApiModelProperty(value = "指定IP数")
    @NotNull(message = "指定IP数不能为空")
    private Integer ipNum;

    @ApiModelProperty(value = "压测时长")
    @NotNull(message = "压测时长不能为空")
    private TimeBean pressureTestTime;

    @ApiModelProperty(value = "施压模式")
    @NotNull(message = "施压模式不能为空")
    private Integer pressureMode;

    @ApiModelProperty(value = "递增时长")
    private TimeBean increasingTime;

    @ApiModelProperty(value = "阶梯层数")
    private Integer step;

    @ApiModelProperty(value = "脚本类型")
    @NotNull(message = "脚本类型不能为空")
    private Integer scriptType;

    @ApiModelProperty(name = "uploadFile", value = "压测脚本/文件")
    @NotEmpty(message = "压测脚本/文件不能为空")
    private List<SceneScriptRefOpen> uploadFile;

    @ApiModelProperty(name = "stopCondition", value = "SLA终止配置")
    @NotEmpty(message = "SLA终止配置不能为空")
    private List<SceneSlaRefOpen> stopCondition;

    @ApiModelProperty(name = "warningCondition", value = "SLA警告配置")
    private List<SceneSlaRefOpen> warningCondition;

    /**
     * 扩展字段
     */
    private String features;

    private Long scriptId;

    private String machineId;
    private EngineType engineType;

}
