package com.pamirs.takin.entity.domain.entity.settle;

import java.util.Date;

import lombok.Data;

@Data
public class Account {
    private Long id;

    private Long uid;

    private Integer status;

    private Integer isDeleted;

    private Long tags;

    private Date gmtCreate;

    private Date gmtUpdate;

    private String features;
}
