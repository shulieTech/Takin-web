package io.shulie.takin.web.biz.pojo.request.pts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junshi
 * @ClassName PtsApiHeaderRequest
 * @Description
 * @createTime 2023年03月15日 15:38
 */
@Data
@ApiModel("新增业务流程-串联链路-API-Header定义入参")
public class PtsApiHeaderRequest implements Serializable {

    @ApiModelProperty(value = "Header定义(key-value)")
    private List<KeyValueRequest> headers = new ArrayList<>();
}
