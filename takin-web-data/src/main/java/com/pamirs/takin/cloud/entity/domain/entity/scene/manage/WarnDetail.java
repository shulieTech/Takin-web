package com.pamirs.takin.cloud.entity.domain.entity.scene.manage;

import java.util.Date;

import lombok.Data;

/**
 * @author -
 */
@Data
public class WarnDetail {

    private Long id;

    private Long ptId;

    private Long slaId;

    private String slaName;

    private String bindRef;

    private Long businessActivityId;

    private String businessActivityName;

    private String warnContent;

    private Double realValue;

    private Date warnTime;

    private Date createTime;

}
