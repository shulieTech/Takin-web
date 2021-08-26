package com.pamirs.takin.entity.domain.entity.settle;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class AccountBook {
    private Long id;

    private Long uid;

    private Long accId;

    private Long parentBookId;

    private BigDecimal balance;

    private BigDecimal lockBalance;

    private BigDecimal totalBalance;

    private Integer subject;

    private Integer direct;

    private String rule;

    private BigDecimal ruleBalance;

    private Date startTime;

    private Date endTime;

    private Integer status;

    private Integer version;

    private Integer isDeleted;

    private Long tags;

    private Date gmtCreate;

    private Date gmtUpdate;

    private String features;
}
