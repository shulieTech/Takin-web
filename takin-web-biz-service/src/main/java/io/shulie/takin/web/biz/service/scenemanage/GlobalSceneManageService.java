package io.shulie.takin.web.biz.service.scenemanage;

public interface GlobalSceneManageService {

    void sceneToGlobal(Long sceneManageId);

    void globalToScene(Long id);
}
