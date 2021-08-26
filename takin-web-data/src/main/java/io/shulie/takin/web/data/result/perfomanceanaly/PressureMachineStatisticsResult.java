package io.shulie.takin.web.data.result.perfomanceanaly;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-13 11:51
 */

@Data
public class PressureMachineStatisticsResult {

    /**
     * id
     */
    private Long id;

    /**
     * 总数量
     */
    @JsonProperty("machine_total")
    private Integer machineTotal = 0;

    /**
     * 压测中数量
     */
    @JsonProperty("machine_pressured")
    private Integer machinePressured = 0;

    /**
     * 空闲机器数量
     */
    @JsonProperty("machine_free")
    private Integer machineFree = 0;

    /**
     * 离线机器数量
     */
    @JsonProperty("machine_offline")
    private Integer machineOffline = 0;

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

    private Long time ;
}
