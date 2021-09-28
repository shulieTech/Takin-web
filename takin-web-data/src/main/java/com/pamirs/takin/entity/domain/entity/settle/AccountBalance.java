package com.pamirs.takin.entity.domain.entity.settle;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class AccountBalance {
    private Long id;

    private Long accId;

    private Long bookId;

    private BigDecimal amount;

    private BigDecimal balance;

    private BigDecimal lockBalance;

    private Integer subject;

    private Integer direct;

    private String remark;

    private Long parentBookId;

    private String sceneCode;

    private Integer status;

    private Date accTime;

    private String outerId;

    private Integer isDeleted;

    private Long tags;

    private Date gmtCreate;

    private Date gmtUpdate;

    private String features;
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
