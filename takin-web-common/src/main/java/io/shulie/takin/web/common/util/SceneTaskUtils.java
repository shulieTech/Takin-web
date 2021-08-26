package io.shulie.takin.web.common.util;

import io.shulie.takin.web.common.constant.SceneTaskConstants;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.common.util
 * @ClassName: SceneTaskUtils
 * @Description: TODO
 * @Date: 2021/8/25 10:48
 */
public class SceneTaskUtils implements SceneTaskConstants {

    /**
     * 获取任务key
     * @param sceneId
     * @return
     */
    public static String getSceneTaskKey(Long sceneId) {
        return String.format(SCENE_TASK_KEY,sceneId);
    }
}
