package io.shulie.takin.web.biz.pojo.request.whitelist;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/12 5:58 下午
 */
@Data
public class WhitelistUpdatePartAppNameRequest {
    @ApiModelProperty(name = "effectiveAppName", value = "生效应用")
    private List<String> effectiveAppName;
    @ApiModelProperty(name = "wlistId", value = "白名单id")
    private Long wlistId;

}
