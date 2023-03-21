package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author junshi
 * @ClassName PtsApiReturnVarRequest
 * @Description
 * @createTime 2023年03月15日 15:38
 */
@Data
@ApiModel("新增业务流程-串联链路-API-出参定义入参")
public class PtsApiReturnVarRequest implements Serializable {

    @ApiModelProperty(value = "出参定义")
    private List<ReturnVarRequest> vars = new ArrayList<>();
}
