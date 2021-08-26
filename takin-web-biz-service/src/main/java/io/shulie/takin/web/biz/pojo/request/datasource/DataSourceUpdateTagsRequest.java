package io.shulie.takin.web.biz.pojo.request.datasource;

import java.util.List;

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
@ApiModel("数据源更新标签模型")
public class DataSourceUpdateTagsRequest {

    @ApiModelProperty("数据源id")
    @NotNull
    private Long datasourceId;

    @ApiModelProperty("标签")
    @Size(max = 64)
    private List<String> tagNames;
}
