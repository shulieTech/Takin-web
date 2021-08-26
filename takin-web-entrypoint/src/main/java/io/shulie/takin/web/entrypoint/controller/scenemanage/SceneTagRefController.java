package io.shulie.takin.web.entrypoint.controller.scenemanage;

import java.util.List;

import io.shulie.takin.web.biz.service.scenemanage.SceneTagService;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneTagRefCreateRequest;
import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 场景标签
 *
 * @author mubai
 * @date 2020-11-30 14:06
 */

@RestController
@RequestMapping("/api/scenemanage/tag")
@Api(tags = "场景标签管理")
public class SceneTagRefController {

    @Autowired
    SceneTagService sceneTagService;
    //
    //@PostMapping
    //@ApiOperation(value = "新增场景标签")
    //public void addTag(@RequestBody SceneTagCreateRequest request) {
    //    sceneTagService.createSceneTag(request);
    //}

    @GetMapping
    @ApiOperation(value = "获取所有压测场景标签")
    public List<TagManageResponse> getAllSceneTag() {
        return sceneTagService.getAllSceneTags();
    }

    @PostMapping(value = "/ref")
    @ApiOperation(value = "场景绑定标签")
    public void addSceneTagRef(@RequestBody SceneTagRefCreateRequest sceneTagRefCreateRequest) {
        sceneTagService.createSceneTagRef(sceneTagRefCreateRequest);
    }

}
