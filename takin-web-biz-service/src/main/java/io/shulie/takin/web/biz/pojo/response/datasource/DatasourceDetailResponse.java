package io.shulie.takin.web.biz.pojo.response.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-28
 */
@Data
@ApiModel("数据源详情返回的对象")
public class DatasourceDetailResponse {

    @ApiModelProperty("数据源ID")
    private Long datasourceId;

    @ApiModelProperty("数据源名称")
    private String datasourceName;

    @ApiModelProperty("数据源类型")
    private Integer type;

    @ApiModelProperty("数据源地址")
    private String jdbcUrl;

    @ApiModelProperty("用户名")
    private String username;

}
