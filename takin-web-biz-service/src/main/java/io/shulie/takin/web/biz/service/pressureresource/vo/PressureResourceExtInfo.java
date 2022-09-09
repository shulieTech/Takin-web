package io.shulie.takin.web.biz.service.pressureresource.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
@ToString(callSuper = true)
public class PressureResourceExtInfo {
    @ApiModelProperty("识别总数")
    private Integer totalSize;

    @ApiModelProperty("正常个数")
    private Integer normalSize;

    @ApiModelProperty("异常个数")
    private Integer exceptionSize;

    @ApiModelProperty("最新检测时间")
    private Date checkTime;

    @ApiModelProperty("负责人")
    private String userName;
}
