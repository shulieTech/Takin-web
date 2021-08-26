package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/4/17 下午9:51
 */
@Data
public class TimeVO implements Serializable {

    private static final long serialVersionUID = -4490980949244068326L;

    @ApiModelProperty(value = "时间")
    @NotNull(message = "时间不能为空")
    private Long time;

    @ApiModelProperty(value = "单位")
    @NotNull(message = "单位不能为空")
    private String unit;

    public TimeVO() {

    }

    public TimeVO(Long time, String unit) {
        this.time = time;
        this.unit = unit;
    }
}
