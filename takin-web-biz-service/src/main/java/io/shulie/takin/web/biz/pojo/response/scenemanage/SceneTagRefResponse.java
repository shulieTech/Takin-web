package io.shulie.takin.web.biz.pojo.response.scenemanage;

import lombok.Data;

/**
 * @author mubai
 * @date 2020-12-02 10:15
 */

@Data
public class SceneTagRefResponse {

    private Long id;

    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 标签id
     */
    private Long tagId;

    private String gmtCreate;

    private String gmtUpdate;
}
