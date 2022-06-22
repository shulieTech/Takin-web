package io.shulie.takin.cloud.biz.output.engine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author moriarty
 */
@Data
@ApiModel("引擎配置日志输出参数")
public class EngineLogPtlConfigOutput {

    @ApiModelProperty("是否输出ptl日志文件")
    private boolean ptlFileEnable;

    @ApiModelProperty("ptl日志文件是否只输出错误信息")
    private boolean ptlFileErrorOnly;

    @ApiModelProperty("ptl日志是否只输出接口调用时间较长信息")
    private boolean ptlFileTimeoutOnly;

    @ApiModelProperty("ptl日志接口超时阈值")
    private Long timeoutThreshold;

    @ApiModelProperty("ptl日志是否截断")
    private boolean logCutOff;

}
