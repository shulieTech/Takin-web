package io.shulie.takin.web.biz.pojo.dto.machinemanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author xjz@io.shulie
 * @date 2022/3/10
 * @desc 压力机返回对象
 */
@Data
@ApiModel("压力机返回对象")
public class PressureMachineDTO {

    @ApiModelProperty("压测机器ID")
    private Long id;
    @ApiModelProperty("主机IP")
    private String ip;
    @ApiModelProperty("主机Mac")
    private String macAddr;
    @ApiModelProperty("CPU")
    private int cpuNum;
    @ApiModelProperty("内存")
    private String memory;
    @ApiModelProperty("交换内存")
    private String swap;
    @ApiModelProperty("压测机器状态{0:空闲;1:压测中(繁忙);2:压力机停止服务，不会出现在列表}")
    private Integer status;
    private String typeMachine;

}
