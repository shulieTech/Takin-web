package io.shulie.takin.web.biz.pojo.request.leakcheck;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-28
 */
@Data
@ApiModel("更新漏数检查脚本")
public class LeakSqlUpdateRequest {

    @ApiModelProperty("业务活动id")
    @NotNull
    private Long businessActivityId;

    @ApiModelProperty("数据源id")
    @NotNull
    private Long datasourceId;

    @ApiModelProperty("sql语句")
    @NotEmpty
    @Size(max = 100)
    private List<String> sqls;
}
