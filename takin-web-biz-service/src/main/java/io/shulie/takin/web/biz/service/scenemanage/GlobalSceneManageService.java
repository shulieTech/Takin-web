package io.shulie.takin.web.biz.service.scenemanage;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.response.scenemanage.GlobalSceneManageResponse;

public interface GlobalSceneManageService {

    void sceneToGlobal(Long sceneManageId);

    void globalToScene(Long id);

    PagingList<GlobalSceneManageResponse> list(Integer current, Integer pageSize, String name);

    void cancelSceneToGlobal(Long sceneManageId);
}
