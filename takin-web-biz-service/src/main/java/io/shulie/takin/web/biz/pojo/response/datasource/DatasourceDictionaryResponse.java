package io.shulie.takin.web.biz.pojo.response.datasource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/11 4:31 下午
 */
@Data
public class DatasourceDictionaryResponse {
    @ApiModelProperty("数据源ID")
    private Long datasourceId;

    @ApiModelProperty("数据源名称")
    private String datasourceName;

    @ApiModelProperty("数据源地址")
    private String jdbcUrl;
}
