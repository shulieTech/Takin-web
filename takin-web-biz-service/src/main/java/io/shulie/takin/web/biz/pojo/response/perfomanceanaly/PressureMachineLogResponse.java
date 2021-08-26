package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-16 13:53
 */

@Data
@ApiModel(value = "压力机详情趋势返回模型")
public class PressureMachineLogResponse implements Serializable {

    private static final long serialVersionUID = -7026185022939992174L;

    @ApiModelProperty(value = "cpu利用率")
    private List<TypeValueDateVo> cpuUsageList;

    @ApiModelProperty(value = "cpu load 列表")
    private List<TypeValueDateVo> cpuLoadList;

    @ApiModelProperty(value = "内存利用率")
    private List<TypeValueDateVo> memoryUsageList;

    @ApiModelProperty(value = "io等待率")
    private List<TypeValueDateVo> ioWaitPerList;

    @ApiModelProperty(value = "网络带宽利用率")
    private List<TypeValueDateVo> transmittedUsageList;
}
