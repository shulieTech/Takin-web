package io.shulie.takin.web.biz.pojo.request.leakcheck;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/4 10:25 上午
 */
@Data
@ApiModel("漏数检查脚本详情")
public class LeakSqlDetailRequest {
    @ApiModelProperty("业务活动id")
    @NotNull
    private Long businessActivityId;

    @ApiModelProperty("数据源id")
    @NotNull
    private Long datasourceId;
}
