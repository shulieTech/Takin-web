package io.shulie.takin.web.biz.pojo.request.scene;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PressureMachineResponse implements Serializable {

    private String name;
    private String status;
    private BigDecimal cpu;
    private BigDecimal memory;
    private String nodeIp;
}
