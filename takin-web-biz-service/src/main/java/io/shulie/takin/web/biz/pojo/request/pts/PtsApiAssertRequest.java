package io.shulie.takin.web.biz.pojo.request.pts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junshi
 * @ClassName PtsApiAssertRequest
 * @Description
 * @createTime 2023年03月15日 15:38
 */
@Data
@ApiModel("新增业务流程-串联链路-API-检查点（断言）入参")
public class PtsApiAssertRequest implements Serializable {

    @ApiModelProperty(value = "检查点（断言）定义")
    private List<AssertRequest> asserts = new ArrayList<>();
}
