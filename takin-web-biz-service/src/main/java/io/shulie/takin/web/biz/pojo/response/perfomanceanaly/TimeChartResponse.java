package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 横坐标 time
 *
 * @author qianshui
 * @date 2020/11/4 上午11:43
 */
@Data
public class TimeChartResponse implements Serializable {

    @ApiModelProperty("时间横坐标")
    private String time;
}
