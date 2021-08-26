package io.shulie.takin.web.biz.pojo.request.middleware;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/2/24 5:08 下午
 */
@Data
public class AppMiddlewareScanSearchRequest extends PagingDevice {
    @ApiModelProperty("应用名")
    private String appName;
    @ApiModelProperty("支持状态")
    private Integer status;
    @ApiModelProperty("中间件类型")
    private String type;
}
