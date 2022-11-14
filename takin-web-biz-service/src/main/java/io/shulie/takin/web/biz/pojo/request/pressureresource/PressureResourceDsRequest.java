package io.shulie.takin.web.biz.pojo.request.pressureresource;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author XINGCHEN
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
public class PressureResourceDsRequest extends PageBaseDTO {
    @ApiModelProperty("配置资源ID")
    private Long resourceId;

    @ApiModelProperty("业务数据源")
    private String bussinessDataBase;
}
