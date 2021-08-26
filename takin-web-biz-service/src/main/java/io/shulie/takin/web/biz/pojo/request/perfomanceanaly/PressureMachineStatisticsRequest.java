package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import java.io.Serializable;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author mubai
 * @date 2020-11-13 19:57
 */

@Data
@ApiModel(value = "压力机趋势统计")
@EqualsAndHashCode(callSuper = true)
public class PressureMachineStatisticsRequest extends PagingDevice implements Serializable {
    private static final long serialVersionUID = 6549484360475776984L;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

}
