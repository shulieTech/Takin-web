package io.shulie.takin.web.biz.pojo.request.leakcheck;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-28
 */
@Data
@ApiModel("删除漏数检查脚本")
public class LeakSqlDeleteRequest {

    @ApiModelProperty("业务活动id")
    @NotNull
    private Long businessActivityId;

    @ApiModelProperty("数据源id")
    @NotNull
    private Long datasourceId;
}
