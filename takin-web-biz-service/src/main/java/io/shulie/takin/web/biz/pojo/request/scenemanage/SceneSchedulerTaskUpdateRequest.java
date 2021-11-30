package io.shulie.takin.web.biz.pojo.request.scenemanage;


import java.util.Date;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-12-01 10:35
 */

@Data
public class SceneSchedulerTaskUpdateRequest extends TenantCommonExt {

    private Long id ;

    private Long userId ;

    private String content ;


    private Date executeTime ;

    /**
     * 0：待执行，1:执行中；2:已执行
     */
    private Integer isExecuted ;

    private Boolean isDeleted ;
}
