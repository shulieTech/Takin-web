package io.shulie.takin.web.data.param.sceneManage;

import java.util.Date;

import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-30 21:28
 */

@Data
public class SceneSchedulerTaskInsertParam {

    private Long sceneId ;

    private Long userId ;

    private String content ;

    private Integer isExecuted ;

    private Date executeTime ;

    private String executeCron ;

}
