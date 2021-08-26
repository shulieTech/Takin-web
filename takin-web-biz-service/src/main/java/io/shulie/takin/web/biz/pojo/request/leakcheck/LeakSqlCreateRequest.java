package io.shulie.takin.web.biz.pojo.request.leakcheck;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/31 3:27 下午
 */
@Data
@ApiModel("漏数配置对象")
public class LeakSqlCreateRequest {

    @ApiModelProperty("业务活动id")
    @NotNull
    private Long businessActivityId;

    @ApiModelProperty("数据源id")
    @NotNull
    private Long datasourceId;

    @ApiModelProperty("sql集合")
    @NotEmpty
    @Size(max = 100)
    private List<String> sqls;
}
