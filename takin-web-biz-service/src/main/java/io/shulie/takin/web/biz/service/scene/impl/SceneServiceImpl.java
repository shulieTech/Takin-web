package io.shulie.takin.web.biz.service.scene.impl;

import io.shulie.takin.ext.content.emus.NodeTypeEnum;
import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.biz.utils.DataUtil;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.dao.scene.SceneLinkRelateDAO;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: liyuanba
 * @Date: 2021/10/27 10:36 上午
 */
@Slf4j
@Service
public class SceneServiceImpl implements SceneService {
    @Autowired
    private SceneLinkRelateDAO sceneLinkRelateDAO;
    @Autowired
    private ActivityDAO activityDAO;
    @Override
    public List<SceneLinkRelateResult> nodeLinkToBusinessActivity(List<ScriptNode> nodes, Long sceneId) {
        List<ScriptNode> nodeList = getNodes(nodes);
        if (CollectionUtils.isEmpty(nodeList)) {
            return null;
        }
        return nodeList.stream().filter(Objects::nonNull)
                .filter(node -> NodeTypeEnum.SAMPLER == node.getType())
                .map(node -> this.nodeLinkToBusinessActivity(node, sceneId))
                .collect(Collectors.toList());
    }

    /**
     * 节点和业务活动匹配
     */
    public SceneLinkRelateResult nodeLinkToBusinessActivity(ScriptNode node, Long sceneId) {
        List<SceneLinkRelateResult> links = sceneLinkRelateDAO.getByEntrance(node.getIdentification());
        SceneLinkRelateResult link = null;
        ActivityListResult activity = null;
        if (CollectionUtils.isEmpty(links)) {
            ActivityQueryParam param = new ActivityQueryParam();
            param.setEntrance(node.getIdentification());
            List<ActivityListResult> activities = activityDAO.getActivityList(param);
            //noinspection unchecked
            activity = DataUtil.getFirst(activities, Comparator.comparing(ActivityListResult::getActivityId).reversed());
        } else {
            Comparator<SceneLinkRelateResult> idDescComparator = Comparator.comparing(SceneLinkRelateResult::getId).reversed();
            if (null != sceneId) {
                //noinspection unchecked
                link = DataUtil.getFirst(links, idDescComparator, r -> String.valueOf(sceneId).equals(r.getSceneId()));
            }
            if (null == link) {
                //noinspection unchecked
                link = DataUtil.getFirst(links, idDescComparator);
            }
        }

        SceneLinkRelateResult r = new SceneLinkRelateResult();
        r.setScriptIdentification(node.getIdentification());
        r.setScriptXpathMd5(node.getXpathMd5());
        if (null != link) {
            r.setId(link.getId());
            r.setSceneId(link.getSceneId());
            r.setEntrance(link.getEntrance());
            r.setBusinessLinkId(link.getBusinessLinkId());
            r.setTechLinkId(link.getTechLinkId());
            r.setParentBusinessLinkId(link.getParentBusinessLinkId());
        } else if (null != activity) {
            r.setEntrance(node.getIdentification());
            r.setBusinessLinkId(activity.getActivityId().toString());
            r.setTechLinkId(activity.getTechLinkId());
            r.setParentBusinessLinkId(activity.getParentTechLinkId());
        }
        return r;
    }

    /**
     * 将子节点全部遍历出来
     */
    private List<ScriptNode> getNodes(List<ScriptNode> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        List<ScriptNode> result = Lists.newArrayList();
        for (ScriptNode node : nodes) {
            result.add(node);
            if (CollectionUtils.isEmpty(node.getChildren())) {
                continue;
            }
            List<ScriptNode> children = getNodes(node.getChildren());
            if (CollectionUtils.isEmpty(children)) {
                continue;
            }
            result.addAll(children);
        }
        return result;
    }
}
