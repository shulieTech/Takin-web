package com.pamirs.takin.entity.domain.entity;

import java.util.Date;

import lombok.Data;

/**
 * @author 慕白
 * @date 2020-03-05 10:06
 */

@Data
public class LinkGuardEntity {

    private Long id;
    private String applicationName;
    private Long applicationId;
    private String methodInfo;
    private String groovy;
    private Date createTime;
    private Date updateTime;
    private Boolean isDeleted;
    private Boolean isEnable;
    private Long customerId;
    private Long userId;
    private String remark;
}
