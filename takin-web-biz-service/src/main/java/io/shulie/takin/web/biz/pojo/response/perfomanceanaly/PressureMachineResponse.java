package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-16 15:43
 */

@Data
@ApiModel(value = "压力机列表视图模型")
public class PressureMachineResponse implements Serializable {
    private static final long serialVersionUID = 8801755107161467895L;
    /**
     * id
     */

    @ApiModelProperty(value = "机器id")
    private Long id;

    /**
     * 压力机名称
     */

    @ApiModelProperty(value = "机器名称")
    private String name;

    /**
     * 压力机IP
     */
    @ApiModelProperty(value = "ip")
    private String ip;

    /**
     * 标签
     */
    @ApiModelProperty(value = "标签")
    private String flag;

    @ApiModelProperty(value = "机器水位")
    private BigDecimal machineUsage;

    /**
     * cpu核数
     */
    @ApiModelProperty(value = "cpu核数")
    private Integer cpu;

    /**
     * 内存，单位字节
     */
    @ApiModelProperty(value = "内存大小")
    private String memory;

    /**
     * 磁盘，单位字节
     */
    @ApiModelProperty(value = "磁盘大小")
    private String disk;

    /**
     * cpu利用率
     */
    private BigDecimal cpuUsage;

    /**
     * cpu load
     */
    private BigDecimal cpuLoad;

    /**
     * 内存利用率
     */
    private BigDecimal memoryUsed;

    /**
     * 磁盘 IO 等待率
     */
    private BigDecimal diskIoWait;

    /**
     * 网络带宽入总大小
     */
    private Long transmittedTotal;

    /**
     * 网络带宽入大小
     */
    private Long transmittedIn;

    /**
     * 网络带宽入利用率
     */
    private BigDecimal transmittedInUsage;

    /**
     * 网络带宽出大小
     */
    private Long transmittedOut;

    /**
     * 网络带宽出利用率
     */
    private BigDecimal transmittedOutUsage;

    /**
     * 网络带宽利用率
     */
    private BigDecimal transmittedUsage;

    /**
     * 压测场景id
     */
    @ApiModelProperty(value = "压测场景")
    private String sceneNames;

    /**
     * 状态 0：空闲 ；1：压测中  -1:离线
     */
    @ApiModelProperty(value = "压力机状态")
    private Integer status;

    /**
     * 状态 0: 正常 1： 删除
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    private String gmtCreate;

    /**
     * 修改时间
     */
    private String gmtUpdate;

}
