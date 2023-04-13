package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author junshi
 * @ClassName PtsApiBaseRequest
 * @Description
 * @createTime 2023年03月15日 15:34
 */
@Data
@ApiModel("新增业务流程-串联链路-API-Body定义入参")
public class PtsApiBodyRequest implements Serializable {

    @ApiModelProperty(value = "form-data,x-www-form-urlencoded,JSON")
    private String contentType;

    @ApiModelProperty(value = "form数据")
    private List<KeyValueRequest> forms = new ArrayList<>();

    @ApiModelProperty(value = "raw数据")
    private String rawData;

}
