package io.shulie.takin.web.biz.pojo.request.datasource;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.pamirs.takin.common.constant.DataSourceVerifyTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-28
 */
@Data
@ApiModel("数据源修改对象")
public class DataSourceUpdateRequest {

    @ApiModelProperty("数据源ID")
    @NotNull
    private Long datasourceId;

    @ApiModelProperty("数据源名称")
    @Size(max = 64)
    private String datasourceName;

    @ApiModelProperty("数据源类型")
    private DataSourceVerifyTypeEnum type;

    @ApiModelProperty("数据源地址")
    private String jdbcUrl;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;
}
