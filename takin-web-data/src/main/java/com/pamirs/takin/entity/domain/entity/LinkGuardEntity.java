package com.pamirs.takin.entity.domain.entity;

import java.util.Date;

import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;

/**
 * @author 慕白
 * @date 2020-03-05 10:06
 */

@Data
public class LinkGuardEntity extends UserBaseEntity {

    private Long id;
    private String applicationName;
    private Long applicationId;
    private String methodInfo;
    private String groovy;
    private Date createTime;
    private Date updateTime;
    private Boolean isDeleted;
    private Boolean isEnable;

    private String remark;
}
