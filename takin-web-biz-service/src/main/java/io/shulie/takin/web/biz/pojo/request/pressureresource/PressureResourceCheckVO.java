package io.shulie.takin.web.biz.pojo.request.pressureresource;

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
public class PressureResourceCheckVO {
    @ApiModelProperty("错误信息备注")
    private String remark;

    @ApiModelProperty("是否成功")
    private boolean success;
}

