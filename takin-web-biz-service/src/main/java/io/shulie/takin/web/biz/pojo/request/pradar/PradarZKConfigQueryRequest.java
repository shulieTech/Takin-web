package io.shulie.takin.web.biz.pojo.request.pradar;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PradarZKConfigQueryRequest", description = "zk配置查询入参")
public class PradarZKConfigQueryRequest extends PagingDevice {
    @ApiModelProperty("配置ID")
    private Long id;

    @ApiModelProperty(value = "路径")
    private String zkPath;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "数值")
    private String value;

    @ApiModelProperty(value = "说明")
    private String remark;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
