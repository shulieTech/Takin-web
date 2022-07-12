package io.shulie.takin.web.biz.pojo.request.scene;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PressureMachineResponse implements Serializable {

    @ApiModelProperty("压力节点状态")
    private String engineStatus;

    @ApiModelProperty("压力节点cpu")
    private BigDecimal cpu;

    @ApiModelProperty("压力节点内存")
    private BigDecimal memory;

    private Long id;

    @ApiModelProperty("机器名称")
    private String machineName;

    @ApiModelProperty("机器ip")
    private String machineIp;

    @ApiModelProperty("用户名")
    private String userName;

    /**
     * '状态 0：未部署 ；1：部署中  2:已部署'
     */
    @ApiModelProperty("'状态 0：未部署 ； 2:已部署'")
    private Integer status;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
