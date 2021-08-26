package io.shulie.takin.web.data.param.sceneManage;

import lombok.Data;

/**
 * @author mubai
 * @date 2020-12-02 19:12
 */

@Data
public class SceneSchedulerTaskQueryParam {

    /**
     * 执行时间大于时间
     */
    private String startTime ;

    /**
     * 执行时间小于时间
     */
    private String endTime ;

}
