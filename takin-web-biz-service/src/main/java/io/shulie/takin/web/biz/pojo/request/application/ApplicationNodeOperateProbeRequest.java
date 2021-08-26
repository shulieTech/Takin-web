package io.shulie.takin.web.biz.pojo.request.application;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.constant.AppConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @author liuchuan
 * @date 2021/6/3 4:01 下午
 */
@Data
@ApiModel("入参类 --> 应用下的节点探针操作入参类")
public class ApplicationNodeOperateProbeRequest {

    @ApiModelProperty(value = "操作类型, 1 安装, 3 升级, 2 卸载", required = true)
    @NotNull(message = "操作类型" + AppConstants.MUST_BE_NOT_NULL)
    @Range(min = 1, max = 3, message = "操作类型错误!")
    private Integer operateType;

    @ApiModelProperty(value = "应用id", required = true)
    @NotNull(message = "应用id" + AppConstants.MUST_BE_NOT_NULL)
    private Long applicationId;

    @ApiModelProperty(value = "agentId", required = true)
    @NotBlank(message = "agentId" + AppConstants.MUST_BE_NOT_NULL)
    private String agentId;

    @ApiModelProperty("探针包记录id, 如果是卸载操作, 不用传")
    private Long probeId;

}
