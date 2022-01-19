package io.shulie.takin.web.biz.pojo.response.pradar;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;
    @ApiModelProperty("更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date modifyTime;
    @ApiModelProperty("是否可编辑")
    private Boolean canEdit = true;

}
