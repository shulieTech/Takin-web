package com.pamirs.takin.entity.domain.entity.report;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/7/29 下午4:43
 */
@Data
public class TpsTargetArray {

    /**
     * 时间横坐标
     */
    private String[] time;

    private Integer[] tps;

    private BigDecimal[] cpu;

    private BigDecimal[] loading;

    private BigDecimal[] memory;

    private BigDecimal[] io;

    private BigDecimal[] network;
    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private long tenantId;
    /**
     * 用户id
     */
    private long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
