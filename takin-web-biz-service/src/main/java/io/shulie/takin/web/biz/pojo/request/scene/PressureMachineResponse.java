package io.shulie.takin.web.biz.pojo.request.scene;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PressureMachineResponse implements Serializable {

    private String engineStatus;

    private BigDecimal cpu;

    private BigDecimal memory;

    private Long id;

    private String machineName;

    private String machineIp;

    private String userName;

    /**
     * '状态 0：未部署 ；1：部署中  2:已部署'
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;
}
