package io.shulie.takin.web.data.result.scenemanage;

import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-30 11:47
 */

@Data
public class SceneTagRefResult {

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
