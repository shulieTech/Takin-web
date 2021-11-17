package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.common.domain.WebRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/4/17 下午5:55
 */
@Data
@ApiModel(description = "场景保存入参")
public class SceneManageWrapperVO extends WebRequest implements Serializable {

    private static final long serialVersionUID = -7653146473491831687L;

    @ApiModelProperty(name = "id", value = "压测场景ID")
    private Long id;

    @ApiModelProperty(value = "压测场景名称")
    @NotNull(message = "压测场景名称不能为空")
    private String pressureTestSceneName;

    @ApiModelProperty(value = "施压类型,0:并发,1:tps,2:自定义;不填默认为0")
    private Integer pressureType;

    //1、业务活动格式；2、业务流程格式
    @ApiModelProperty(value = "场景类型")
    private Integer configType;

    @ApiModelProperty(value = "脚本id")
    private Long scriptId;

    @ApiModelProperty(value = "业务活动配置")
    @NotEmpty(message = "业务活动配置不能为空")
    private List<@Valid SceneBusinessActivityRefVO> businessActivityConfig;

    @ApiModelProperty(value = "并发数量")
    private Integer concurrenceNum;

    @ApiModelProperty(value = "指定IP数")
    @NotNull(message = "指定IP数不能为空")
    private Integer ipNum;

    @ApiModelProperty(value = "压测时长")
    @NotNull(message = "压测时长不能为空")
    @Valid
    private TimeVO pressureTestTime;

    @ApiModelProperty(value = "施压模式")
    @NotNull(message = "施压模式不能为空")
    private Integer pressureMode;

    @ApiModelProperty(value = "递增时长")
    private TimeVO increasingTime;

    @ApiModelProperty(value = "阶梯层数")
    private Integer step;

    /**
     * 压测引擎类型，jmx或者其他
     */
    @ApiModelProperty(value = "脚本类型")
    private Integer scriptType;

    @ApiModelProperty(name = "uploadFile", value = "压测脚本/文件")
    private List<SceneScriptRefVO> uploadFile;

    @ApiModelProperty(name = "stopCondition", value = "SLA终止配置")
    @NotEmpty(message = "SLA终止配置不能为空")
    private List<SceneSlaRefVO> stopCondition;

    @ApiModelProperty(name = "warningCondition", value = "SLA警告配置")
    private List<SceneSlaRefVO> warningCondition;

    //业务流程id
    @ApiModelProperty(name = "businessFlowId", value = "业务流程id")
    private Long businessFlowId;

    @ApiModelProperty(name = "isScheduler", value = "是否为定时压测场景")
    private Boolean isScheduler;

    @ApiModelProperty(name = "executeTime", value = "定时执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm", timezone = "GMT+8")
    private Date executeTime;
    /**
     * 单位是小时
     */
    @ApiModelProperty(name = "scheduleInterval", value = "漏数时间间隔")
    private Integer scheduleInterval;

    @ApiModelProperty("脚本节点信息")
    private String scriptAnalysisResult;

    public SceneManageWrapperVO() {
    }
}
