package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.domain.WebRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/4/17 下午5:55
 */
@Data
@ApiModel(description = "流量计算")
@EqualsAndHashCode(callSuper = true)
public class FlowVO extends WebRequest implements Serializable {

    @ApiModelProperty(value = "并发数量")
    private Integer concurrenceNum;

    @ApiModelProperty(value = "压测时长")
    @NotNull(message = "压测时长不能为空")
    private TimeVO pressureTestTime;

    @ApiModelProperty(value = "施压类型,0:并发,1:tps,2:自定义;不填默认为0")
    private Integer pressureType;

    @ApiModelProperty(value = "施压模式")
    @NotNull(message = "施压模式不能为空")
    private Integer pressureMode;

    @ApiModelProperty(value = "递增时长")
    private TimeVO increasingTime;

    @ApiModelProperty(value = "阶梯层数")
    private Integer step;

}
