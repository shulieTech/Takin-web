package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author junshi
 * @ClassName PtsApiRequest
 * @Description
 * @createTime 2023年03月15日 15:29
 */
@Data
@ApiModel("新增业务流程-串联链路-API入参")
public class PtsApiRequest implements Serializable {

    @ApiModelProperty(value = "API名称", required = true)
    @NotBlank(message = "API名称不能为空")
    private String apiName;

    @ApiModelProperty(value = "关联业务活动id")
    private Long businessActivityId;

    @ApiModelProperty(value = "基本请求信息", required = true)
    @NotNull(message = "基本请求信息不能为空")
    private PtsApiBaseRequest base = new PtsApiBaseRequest();

    @ApiModelProperty(value = "Header定义")
    private PtsApiHeaderRequest header = new PtsApiHeaderRequest();

    @ApiModelProperty(value = "body定义")
    private PtsApiBodyRequest body = new PtsApiBodyRequest();

    @ApiModelProperty(value = "出参定义")
    private PtsApiReturnVarRequest returnVar = new PtsApiReturnVarRequest();

    @ApiModelProperty(value = "检查点（断言）")
    private PtsApiAssertRequest checkAssert = new PtsApiAssertRequest();
}
