package io.shulie.takin.web.biz.service.pressureresource.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 进度状态
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
@ToString(callSuper = true)
public class PressureResourceProcessStatusVO {
    @ApiModelProperty("类型(APP-应用检查，DS-影子隔离 REMORECALL-远程调用 )")
    private String type;

    @ApiModelProperty("状态(0-未开始 1-进行中 2-完成)")
    private Integer status;
}
