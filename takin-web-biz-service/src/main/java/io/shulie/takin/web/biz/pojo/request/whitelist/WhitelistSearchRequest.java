package io.shulie.takin.web.biz.pojo.request.whitelist;

import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import io.shulie.takin.common.beans.page.PagingDevice;
import io.shulie.takin.web.common.annocation.Trimmed;
import io.shulie.takin.web.common.annocation.Trimmed.TrimmerType;

/**
 * @author 无涯
 * @date 2021/4/13 3:46 下午
 */
@Data
public class WhitelistSearchRequest extends PagingDevice {

    @ApiModelProperty(name = "interfaceName", value = "接口名")
    @Trimmed(value = TrimmerType.SIMPLE)
    private String interfaceName;

    @ApiModelProperty(name = "appName", value = "应用名")
    @Trimmed(value = TrimmerType.SIMPLE)
    private String appName;
}
