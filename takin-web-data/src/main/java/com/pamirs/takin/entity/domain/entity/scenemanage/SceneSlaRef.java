package com.pamirs.takin.entity.domain.entity.scenemanage;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SceneSlaRef extends SceneRef implements Serializable {

    private static final long serialVersionUID = 7858797202756518503L;

    private Long id;

    private String slaName;

    private String businessActivityIds;

    private Integer targetType;

    private Integer status;

    private Integer isDeleted;

    private Date createTime;

    private String createName;

    private Date updateTime;

    private String updateName;

    private String condition;

}
