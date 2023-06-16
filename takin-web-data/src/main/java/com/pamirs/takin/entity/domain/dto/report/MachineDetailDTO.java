package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机器性能详情
 *
 * @author qianshui
 * @date 2020/7/22 下午2:59
 */
@ApiModel
@Data
public class MachineDetailDTO implements Serializable {

    private static final long serialVersionUID = 7813020434970519614L;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "主机ip")
    private String machineIp;

    private BigDecimal gcCost;

    private BigDecimal gcCount;

    @ApiModelProperty(value = "cpu")
    private Integer cpuNum;

    @ApiModelProperty(value = "内存")
    private BigDecimal memorySize;

    @ApiModelProperty(value = "磁盘")
    private BigDecimal diskSize;

    @ApiModelProperty(value = "带宽")
    private BigDecimal mbps;

    @ApiModelProperty(value = "是否风险机器")
    private Boolean riskFlag;

    @ApiModelProperty(value = "agentId")
    private String agentId;

    @ApiModelProperty(value = "应用名称")
    private String applicationName;

    @ApiModelProperty(value = "进程名称")
    private String processName;

    private MachineTPSTargetDTO tpsTarget;

    @ApiModel
    @Data
    public class MachineTPSTargetDTO implements Serializable {

        private static final long serialVersionUID = -7728660288117937429L;

        private String[] time;

        private Integer[] tps;

        private BigDecimal[] cpu;

        private BigDecimal[] load;

        private BigDecimal[] memory;

        private BigDecimal[] io;

        private BigDecimal[] mbps;

        private BigDecimal[] gcCost;

        private BigDecimal[] gcCount;

        private Long[] fullGcCount;

        private Integer[] fullGcCost;

        private Long[] youngGcCount;

        private Integer[] youngGcCost;
    }
}
