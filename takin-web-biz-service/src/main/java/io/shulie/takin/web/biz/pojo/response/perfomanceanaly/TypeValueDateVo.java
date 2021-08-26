package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-16 14:03
 */
@Data
@ApiModel(value = "type-value-date 前端模型")
public class TypeValueDateVo implements Serializable {
    private static final long serialVersionUID = 5588552828849352311L;

    @ApiModelProperty(name = "date", value = "时间")
    private String date;

    @ApiModelProperty(name = "value", value = "value")
    private Object value;

    @ApiModelProperty(name = "type", value = "type")
    private String type;
}
