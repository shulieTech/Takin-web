package io.shulie.takin.web.biz.pojo.request.activity;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liuchuan
 * @date 2022/2/17 5:12 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("入参类 --> 获取业务关联的应用列表入参类")
public class ListApplicationRequest extends PageBaseDTO {

    @ApiModelProperty(value = "业务流程ids", required = true)
    @NotEmpty(message = "业务流程ids" + AppConstants.MUST_BE_NOT_NULL)
    @Size(min = 1, max = 100, message = "业务活动值的数量超出范围")
    private List<Long> businessFlowIds;

    @ApiModelProperty(value = "应用名称")
    private String applicationName;

}
