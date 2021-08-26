package io.shulie.takin.web.biz.pojo.response.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2020/11/30 9:23 下午
 */
@Data
public class PressureListTotalResponse {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "name")
    private String name;
    @ApiModelProperty(value = "label")
    private String label;
    @ApiModelProperty(value = "gmtCreate")
    private String gmtCreate;
    @ApiModelProperty(value = "createName")
    private String createName;
    @ApiModelProperty(value = "count")
    private Integer count;
    @ApiModelProperty(value = "success")
    private Integer success;
    @ApiModelProperty(value = "fail")
    private Integer fail;
}
