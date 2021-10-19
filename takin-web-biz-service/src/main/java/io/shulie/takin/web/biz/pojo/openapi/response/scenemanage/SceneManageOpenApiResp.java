package io.shulie.takin.web.biz.pojo.openapi.response.scenemanage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.common.bean.TimeBean;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.web.ext.entity.UserCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaoyong
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
public class SceneManageOpenApiResp extends UserCommonExt implements Serializable {

    @ApiModelProperty("压测场景ID")
    private Long id;
    /**
     * 做兼容，部分客户已经用了customId
     */
    @ApiModelProperty("客户ID")
    private Long customId;
    @ApiModelProperty("压测场景名称")
    private String pressureTestSceneName;
    @ApiModelProperty("业务活动配置")
    private List<SceneManageWrapperResp.SceneBusinessActivityRefResp> businessActivityConfig;
    @ApiModelProperty("并发数量")
    private Integer concurrenceNum;
    @ApiModelProperty("指定IP数")
    private Integer ipNum;
    @ApiModelProperty("压测时长(秒)")
    private Long pressureTestSecond;
    @ApiModelProperty("压测时长")
    private TimeBean pressureTestTime;
    @ApiModelProperty("施压模式")
    @NotNull(
        message = "施压模式不能为空"
    )
    private Integer pressureMode;
    @ApiModelProperty("递增时长(秒)")
    private Long increasingSecond;
    @ApiModelProperty("递增时长")
    private TimeBean increasingTime;
    @ApiModelProperty("阶梯层数")
    private Integer step;
    @ApiModelProperty("预计消耗流量")
    private BigDecimal estimateFlow;
    @ApiModelProperty(
        name = "scriptType",
        value = "脚本类型"
    )
    private Integer scriptType;
    @ApiModelProperty(
        name = "uploadFile",
        value = "压测脚本/文件"
    )
    private List<SceneManageWrapperResp.SceneScriptRefResp> uploadFile;
    @ApiModelProperty(
        name = "stopCondition",
        value = "SLA终止配置"
    )
    private List<SceneManageWrapperResp.SceneSlaRefResp> stopCondition;
    @ApiModelProperty(
        name = "warningCondition",
        value = "SLA警告配置"
    )
    private List<SceneManageWrapperResp.SceneSlaRefResp> warningCondition;
    @ApiModelProperty(
        name = "status",
        value = "压测状态"
    )
    private Integer status;
    @ApiModelProperty("总测试时长(压测时长+预热时长)")
    private transient Long totalTestTime;

    private transient String updateTime;
    private transient String lastPtTime;
    private String features;
    private Integer configType;

    @ApiModelProperty("脚本发布id")
    private Long scriptId;
    private String BusinessFlowId;
}
