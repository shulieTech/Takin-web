package com.pamirs.takin.entity.domain.entity.scenemanage;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class WarnDetail implements Serializable {

    private static final long serialVersionUID = -1912947106665624451L;

    private Long id;

    private Long ptId;

    private Long slaId;

    private String slaName;

    private Long businessActivityId;

    private String businessActivityName;

    private String warnContent;

    private Double realValue;

    private Date warnTime;

    private Date createTime;

}
