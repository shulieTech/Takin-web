package io.shulie.takin.web.biz.service.scene;

import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;

import java.util.List;

/**
 * 业务流程
 * @Author: liyuanba
 * @Date: 2021/10/27 9:50 上午
 */
public interface SceneService {
    /**
     * 解析结果节点匹配
     * @param nodes 脚本解析结果
     * @param sceneId 当前业务流程ID
     * @return  节点和业务活动关联关系
     */
    List<SceneLinkRelateResult> nodeLinkToBusinessActivity(List<ScriptNode> nodes, Long sceneId);
}
