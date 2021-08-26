package io.shulie.takin.web.biz.pojo.request.pradar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("pradar配置删除")
public class PradarZKConfigDeleteRequest {
    @ApiModelProperty("配置ID")
    private Long id;
}
