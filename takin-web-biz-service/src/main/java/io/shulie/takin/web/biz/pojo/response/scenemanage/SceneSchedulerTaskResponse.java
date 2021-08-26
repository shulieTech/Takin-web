package io.shulie.takin.web.biz.pojo.response.scenemanage;

import java.util.Date;

import lombok.Data;

/**
 * @author mubai
 * @date 2020-12-01 11:17
 */

@Data
public class SceneSchedulerTaskResponse {

    private Long id;

    private Long sceneId ;

    private Long userId;

    private String content ;


    /**
     * 0：待执行，1:执行中；2:已执行
     */
    private Integer isExecuted ;

    private Date executeTime ;

    private Boolean isDeleted ;

    private Date gmtCreate ;

    private Date gmtUpdate ;
}
