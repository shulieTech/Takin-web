package com.pamirs.takin.entity.domain.vo.entracemanage;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2020/7/3 21:24
 */
@Data
@ApiModel(value = "EntranceApiVo", description = "入口api入参")

public class EntranceApiVo extends PagingDevice {
    @ApiModelProperty(name = "applicationName", value = "应用名")
    private String applicationName;
    @ApiModelProperty(name = "api", value = "入口地址")
    private String api;
}
