package io.shulie.takin.web.biz.pojo.input;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author mubai
 * @date 2020-11-13 08:56
 */

@Data
public class PressureMachineInput {

    /**
     * id
     */
    private Long id;

    /**
     * 压力机名称
     */
    private String name;

    /**
     * 压力机IP
     */
    private String ip;

    /**
     * 标签
     */
    private String flag;

    /**
     * cpu核数
     */
    private Integer cpu;

    /**
     * 内存，单位字节
     */
    private Long memory;

    /**
     * 磁盘，单位字节
     */
    private Long disk;

    private BigDecimal machineUsage ;

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
    private List<Long> sceneId;

    /**
     * 状态 0：空闲 ；1：压测中  -1:离线
     */
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
