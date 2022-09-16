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
public class PressureResourceMockInput {
    @ApiModelProperty(value = "远程调用Id")
    private Long id;

    @ApiModelProperty("mock信息")
    private MockInfo mockInfo;

    @ApiModelProperty("是否放行")
    private Integer pass;
}

