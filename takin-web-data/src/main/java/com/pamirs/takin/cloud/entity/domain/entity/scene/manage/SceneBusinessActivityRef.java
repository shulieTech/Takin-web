package com.pamirs.takin.cloud.entity.domain.entity.scene.manage;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author -
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneBusinessActivityRef extends SceneRef {

    private Long id;

    private Long businessActivityId;

    private String businessActivityName;

    private String applicationIds;

    private String bindRef;

    private Integer isDeleted;

    private Date createTime;

    private String createName;

    private Date updateTime;

    private String updateName;

    private String goalValue;

    /**
     * 是否包含压测头
     */
    private Boolean hasPT;
}
