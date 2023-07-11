package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.constant.AppConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @author liuchuan
 * @date 2021/5/13 4:44 下午
 */
@Data
@ApiModel("入参类 --> 调试入参类")
public class ScriptDebugDoDebugRequest implements AppConstants {

    @ApiModelProperty(value = "脚本发布id", required = true)
    @NotNull(message = "脚本发布id" + MUST_BE_NOT_NULL)
    private Long scriptDeployId;

    @ApiModelProperty(value = "请求条数", required = true)
    @NotNull(message = "请求条数" + MUST_BE_NOT_NULL)
    @Range(min = 1, max = 10000, message = "请求条数在 1-10000 条!")
    private Integer requestNum;

    @ApiModelProperty(value = "并发数",required = true)
    @NotNull(message = "请求并发数"+ MUST_BE_NOT_NULL)
    private Integer concurrencyNum = 1;

}