package io.shulie.takin.web.biz.pojo.request.pts;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junshi
 * @ClassName PtsDebugRecordQueryRequest
 * @Description
 * @createTime 2023年03月15日 17:28
 */
@Data
@ApiModel("调试记录列表")
public class PtsDebugRecordQueryRequest implements Serializable {

    @ApiModelProperty(value = "业务流程id", required = true)
    @NotNull(message = "业务流程id不能为空")
    private Long id;
}
