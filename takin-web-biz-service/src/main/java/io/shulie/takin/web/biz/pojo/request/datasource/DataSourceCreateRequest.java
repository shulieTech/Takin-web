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
@ApiModel("数据源创建对象")
public class DataSourceCreateRequest {

    @ApiModelProperty("数据源名称")
    @Size(max = 64)
    @NotBlank
    private String datasourceName;

    @ApiModelProperty("数据源类型")
    @NotNull
    private Integer type;

    @ApiModelProperty("数据源地址")
    private String jdbcUrl;

    @ApiModelProperty("用户名")
    @NotBlank
    private String username;

    @ApiModelProperty("密码")
    @NotBlank
    private String password;
}
