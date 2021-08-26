package io.shulie.takin.web.data.param.sceneManage;

import java.util.Date;

import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-30 21:44
 */

@Data
public class SceneSchedulerTaskUpdateParam {

    private Long id;

    private Long sceneId;

    private String content;

    private Integer isExecuted;

    private Date executeTime;

    private Boolean isDeleted ;
}
