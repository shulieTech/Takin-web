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
 * @date 2021/1/13 10:09 上午
 */
@Data
@ApiModel("sql连接测试对象")
public class SqlTestRequest {

    @ApiModelProperty("数据源id")
    @NotNull
    private Long datasourceId;

    @ApiModelProperty("sql集合")
    @NotEmpty
    @Size(max = 100)
    private List<String> sqls;
}
