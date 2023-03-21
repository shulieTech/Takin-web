package io.shulie.takin.web.biz.pojo.request.pts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junshi
 * @ClassName PtsLinkRequest
 * @Description
 * @createTime 2023年03月15日 15:25
 */
@Data
@ApiModel("新增业务流程-串联链路入参")
public class PtsLinkRequest implements Serializable {

    @ApiModelProperty(value = "串联链路名称")
    private String linkName;

    @ApiModelProperty(value = "API接口", required = true)
    @NotNull(message = "API接口不能为空")
    private List<PtsApiRequest> apis = new ArrayList<>();
}
