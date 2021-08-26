package io.shulie.takin.web.biz.pojo.response.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "工作台展示")
public class UserWorkBenchResponse {
    /**
     * 开关状态
     */
    @ApiModelProperty(value = "开关状态")
    private String switchStatus;
    /**
     * 应用接入数量
     */
    @ApiModelProperty(value = "应用数量")
    private Integer applicationNum;
    /**
     * 接入异常数量
     */
    @ApiModelProperty(value = "应用接入异常数量")
    private Integer accessErrorNum;
    /**
     * 系统流程数量
     */
    @ApiModelProperty(value = "系统流程数量")
    private Integer systemProcessNum;
    /**
     * 系统流程变更数量
     */
    @ApiModelProperty(value = "系统流程变更数量")
    private Integer changedProcessNum;
}