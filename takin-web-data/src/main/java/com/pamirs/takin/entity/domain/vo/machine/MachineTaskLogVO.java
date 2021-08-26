package com.pamirs.takin.entity.domain.vo.machine;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/5/13 上午11:23
 */
@Data
@ApiModel(description = "机器任务日志")
public class MachineTaskLogVO implements Serializable {
    private static final long serialVersionUID = 8963893470487499218L;

    @ApiModelProperty(value = "机器IP")
    private String ip;

    @ApiModelProperty(value = "机器名称")
    private String hostname;

    @ApiModelProperty(value = "机器状态")
    private Byte status;

    @ApiModelProperty(value = "任务日志")
    private String log;
}
