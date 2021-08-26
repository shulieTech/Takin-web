package io.shulie.takin.web.biz.service.scenemanage;

import java.util.List;

import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneTagCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneTagRefCreateRequest;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneTagRefResponse;
import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;

/**
 * @author mubai
 * @date 2020-11-30 14:26
 */
public interface SceneTagService {

    void createSceneTag(SceneTagCreateRequest request);

    List<TagManageResponse> getAllSceneTags();

    void createSceneTagRef(SceneTagRefCreateRequest refCreateRequests);

    List<SceneTagRefResponse> getSceneTagRefBySceneIds(List<Long> sceneIds);

    List<SceneTagRefResponse> getTagRefByTagIds(List<Long> tagIds);

}
