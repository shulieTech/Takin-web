package io.shulie.takin.web.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 前端下拉列表通用数据结构
 *
 * @author liuchuan
 * @date 2021/5/11 1:34 下午
 */
@Data
@ApiModel("前端下拉列表通用数据结构")
public class LabelValueVO {

    @ApiModelProperty("展示标题")
    private String label;

    @ApiModelProperty("传参数据")
    private Object value;

}
