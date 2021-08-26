package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 前端展示数据格式
 *
 * @author mubai
 * @date 2020-11-09 15:30
 */

@Data
@ApiModel(value = "内存返回值")
public class MemoryModelVo implements Serializable {

    private static final long serialVersionUID = 5274147602583429730L;

    @ApiModelProperty(name = "time", value = "时间")
    private String time;

    @ApiModelProperty(name = "value", value = "value")
    private Object value;

    @ApiModelProperty(name = "type", value = "type")
    private String type;

}
