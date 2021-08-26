package io.shulie.takin.web.biz.pojo.request.datasource;

import javax.validation.constraints.NotBlank;
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
@ApiModel("数据源连接测试对象")
public class DataSourceTestRequest {

    @ApiModelProperty("数据源名称")
    @Size(max = 64)
    private String datasourceName;

    @ApiModelProperty("数据源类型")
    @NotNull
    private Integer type;

    @ApiModelProperty("数据源地址")
    @NotBlank
    private String jdbcUrl;

    @ApiModelProperty("用户名")
    @NotBlank
    private String username;

    @ApiModelProperty("密码")
    @NotBlank
    private String password;
}
