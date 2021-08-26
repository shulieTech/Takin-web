package io.shulie.takin.web.data.param.perfomanceanaly;

import lombok.Data;


/**
 * @author mubai
 * @date 2020-11-13 11:39
 */

@Data
public class PressureMachineStatisticsInsertParam  {

    /**
     * 总数量
     */
    private Integer machineTotal;

    /**
     * 压测中数量
     */
    private Integer machinePressured;

    /**
     * 空闲机器数量
     */
    private Integer machineFree;

    /**
     * 离线机器数量
     */
    private Integer machineOffline;

    /**
     * 状态 0: 正常 1： 删除
     */
    private Boolean isDeleted;

}
