package io.shulie.takin.web.biz.pojo.response.pradar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("pradar配置")
@NoArgsConstructor
public class PradarZKConfigResponse {
    @ApiModelProperty("配置ID")
    Long id;
    @ApiModelProperty("ZK路径")
    String zkPath;
    @ApiModelProperty("值类型:[String,Int,Boolean]")
    String type;
    @ApiModelProperty("值")
    String value;
    @ApiModelProperty("说明")
    String remark;
    @ApiModelProperty("创建时间")
    String createTime;
    @ApiModelProperty("更新时间")
    String modifyTime;
    @ApiModelProperty("是否可编辑")
    private Boolean canEdit = true;

}
