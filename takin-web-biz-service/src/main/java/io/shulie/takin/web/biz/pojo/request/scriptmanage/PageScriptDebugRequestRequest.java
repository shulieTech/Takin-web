package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liuchuan
 * @date 2021/5/12 10:37 上午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("入参 --> 调试记录下的请求流量明细查询入参类")
public class PageScriptDebugRequestRequest extends PageBaseDTO {

    /**
     * 调试记录id
     */
    @ApiModelProperty(value = "调试记录id", required = true)
    @NotNull(message = "调试记录id" + AppConstants.MUST_BE_NOT_NULL)
    private Long scriptDebugId;

    /**
     * 业务活动id
     */
    @ApiModelProperty("业务活动id")
    private Long businessActivityId;

    /**
     * 请求流量明细类型, 不传 全部, 2 响应失败, 3 断言失败
     */
    @ApiModelProperty("请求流量明细类型, 不传 全部, 2 响应失败, 3 断言失败")
    private Integer type;

}
