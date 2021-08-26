package io.shulie.takin.web.biz.pojo.response.common;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/11/11 上午10:55
 */
@Data
@ApiModel("下拉框返回值")
public class SelectListResponse implements Serializable {

    private static final long serialVersionUID = 2145551334532174259L;

    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "值")
    private String value;
}
