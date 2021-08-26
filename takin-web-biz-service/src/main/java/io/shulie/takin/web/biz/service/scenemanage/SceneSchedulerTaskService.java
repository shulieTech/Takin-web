package io.shulie.takin.web.biz.service.scenemanage;

import java.util.List;

import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskQueryRequest;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneSchedulerTaskResponse;

/**
 * @author mubai
 * @date 2020-12-01 10:30
 */
public interface SceneSchedulerTaskService {

    Long insert(SceneSchedulerTaskCreateRequest request);

    void delete(Long id);

    void update(SceneSchedulerTaskUpdateRequest updateRequest, Boolean needVerifyTime);

    SceneSchedulerTaskResponse selectBySceneId(Long sceneId);

    void deleteBySceneId(Long sceneId);

    List<SceneSchedulerTaskResponse> selectBySceneIds(List<Long> sceneIds);

    List<SceneSchedulerTaskResponse> selectByExample(SceneSchedulerTaskQueryRequest request);

    void executeSchedulerPressureTask();

}
