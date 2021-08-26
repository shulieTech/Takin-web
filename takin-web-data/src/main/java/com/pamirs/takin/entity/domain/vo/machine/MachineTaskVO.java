package com.pamirs.takin.entity.domain.vo.machine;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/5/13 上午11:02
 */
@Data
@ApiModel(description = "机器任务信息")
public class MachineTaskVO implements Serializable {
    private static final long serialVersionUID = 5493406100253526438L;
    @ApiModelProperty(value = "任务ID")
    private Long id;

    @ApiModelProperty(value = "机器任务类型")
    private Integer taskType;

    @ApiModelProperty(value = "机器数量")
    private Integer machineNum;

    @ApiModelProperty(value = "机器任务状态")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    private Date gmtUpdate;
}
