package io.shulie.takin.web.data.result.scenemanage;

import java.util.Date;

import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-30 21:41
 */

@Data
public class SceneSchedulerTaskResult {

    private Long id;

    private Long sceneId ;

    private Long userId ;

    private String content ;

    /**
     * 0：待执行，1:执行中；2:已执行
     */
    private Integer isExecuted ;

    private Date executeTime ;

    private String executeCron ;

    private Boolean isDeleted ;

    private Date gmtCreate ;

    private Date gmtUpdate ;
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 用户id
     */
    private String envCode;
}
