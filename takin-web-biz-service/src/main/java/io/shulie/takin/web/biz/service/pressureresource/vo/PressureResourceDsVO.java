package io.shulie.takin.web.biz.service.pressureresource.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
@ToString(callSuper = true)
public class PressureResourceDsVO {
    @ApiModelProperty("状态")
    private int status;

    @ApiModelProperty("业务数据源")
    private String businessDataBase;
}
