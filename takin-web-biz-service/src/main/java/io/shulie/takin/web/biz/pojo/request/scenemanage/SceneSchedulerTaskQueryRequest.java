package io.shulie.takin.web.biz.pojo.request.scenemanage;

import lombok.Data;

/**
 * @author mubai
 * @date 2020-12-02 19:08
 */

@Data
public class SceneSchedulerTaskQueryRequest {

    /**
     * 执行时间大于时间
     */
    private String startTime ;

    /**
     * 执行时间小于时间
     */
    private String endTime ;


}
