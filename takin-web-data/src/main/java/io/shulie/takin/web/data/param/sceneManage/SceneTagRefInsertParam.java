package io.shulie.takin.web.data.param.sceneManage;

import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-30 16:29
 */

@Data
public class SceneTagRefInsertParam {

    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 标签id
     */
    private Long tagId;

}
