package io.shulie.takin.web.biz.pojo.request.datasource;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-28
 */
@Data
@ApiModel("数据源删除对象")
public class DataSourceDeleteRequest {

    @ApiModelProperty("需要删除的DataSourceId集合")
    @NotEmpty
    private List<Long> datasourceIds;
}
