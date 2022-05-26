package com.pamirs.takin.entity.domain.entity;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;

/**
 * @author 慕白
 * @date 2020-03-05 10:06
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class LinkGuardEntity extends UserBaseEntity {

    private Long id;
    private String applicationName;
    private Long applicationId;
    private String methodInfo;
    private String groovy;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;
    private Integer isEnable;

    private String remark;
}
