package io.shulie.takin.web.biz.pojo.request.application;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

/**
 * @author mubai
 * @date 2020-09-23 19:10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("入参类 --> 应用列表入参类")
public class ApplicationQueryRequestV2 extends PageBaseDTO {

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("接入状态")
    @Range(min = 0, max = 3, message = "错误的接入状态")
    private Integer accessStatus;

    @ApiModelProperty("更新时间开始")
    private String updateStartTime;

    @ApiModelProperty("更新时间结束")
    private String updateEndTime;

    @ApiModelProperty("部门id")
    private Long deptId;
}
