package io.shulie.takin.web.biz.pojo.response.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-28
 */
@Data
@ApiModel("数据源绑定的业务活动对象")
public class DatasourceRefsResponse {

    @ApiModelProperty("数据源上面绑定的对象ID")
    private Long refId;

    @ApiModelProperty("数据源上面绑定的对象名称")
    private String refName;

}
