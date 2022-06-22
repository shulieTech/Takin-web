package com.pamirs.takin.cloud.entity.domain.entity.scene.manage;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author -
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneSlaRef extends SceneRef {

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
