package io.shulie.takin.web.data.dao.scene;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/11/16 10:22 上午
 */
public interface SceneBusinessActivityRefDAO {

    List<Long> getAppIdList(Long businessActivityId);
}
