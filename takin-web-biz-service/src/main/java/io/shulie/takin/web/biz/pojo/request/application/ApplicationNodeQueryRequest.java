package io.shulie.takin.web.biz.pojo.request.application;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.constant.AppConstants;
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
@ApiModel("入参类 --> 应用下的节点列表入参类")
public class ApplicationNodeQueryRequest extends PageBaseDTO {

    @ApiModelProperty(value = "应用id", required = true)
    @NotNull(message = "应用id" + AppConstants.MUST_BE_NOT_NULL)
    private Long applicationId;

    @ApiModelProperty("ip, 可按照ip搜索")
    private String ip;

    @ApiModelProperty("类型, 不传 全部, 1 已安装探针的节点, 0 未安装探针的节点, 默认不传")
    @Range(min = 0, max = 1, message = "错误的探针状态")
    private Integer type;

}
