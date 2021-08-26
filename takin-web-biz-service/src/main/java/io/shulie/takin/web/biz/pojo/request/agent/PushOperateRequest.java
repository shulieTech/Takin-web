package io.shulie.takin.web.biz.pojo.request.agent;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.common.constants.ValidConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * agent 推送操作结果
 *
 * @author liuchuan
 * @date 2021/6/7 2:30 下午
 */
@Data
@ApiModel("入参类-推送探针操作结果")
public class PushOperateRequest implements ValidConstants {

    @ApiModelProperty(value = "应用名称", required = true)
    @NotBlank(message = "应用名称" + MUST_NOT_BE_NULL)
    private String appName;

    @ApiModelProperty(value = "agentId", required = true)
    @NotBlank(message = "agentId" + MUST_NOT_BE_NULL)
    private String agentId;

    @ApiModelProperty(value = "操作结果", required = true)
    @NotNull(message = "操作结果" + MUST_NOT_BE_NULL)
    @Range(min = 0, max = 1, message = "操作结果类型错误")
    private Integer operateResult;

    @ApiModelProperty("错误信息")
    private String errorMsg;

}
