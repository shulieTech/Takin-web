package io.shulie.takin.web.biz.service.pts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.dao.scene.SceneLinkRelateDAO;
import io.shulie.takin.web.data.mapper.mysql.BusinessLinkManageTableMapper;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateSaveParam;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author junshi
 * @ClassName PtsApiMatchServiceImpl
 * @Description
 * @createTime 2023年03月16日 18:12
 */
@Service
public class PtsApiMatchServiceImpl implements PtsApiMatchService {

    @Autowired
    private SceneService sceneService;

    @Autowired
    private ActivityDAO activityDAO;

    @Autowired
    private SceneLinkRelateDAO sceneLinkRelateDAO;

    @Resource
    private BusinessLinkManageTableMapper businessLinkManageTableMapper;

    @Override
    public Integer autoMatchBusinessActivity(Long processId) {
        Integer matchNum = 0;
        SceneResult sceneResult = sceneService.getScene(processId);
        if (null == sceneResult) {
            return matchNum;
        }
        List<ScriptNode> scriptNodes = JsonHelper.json2List(sceneResult.getScriptJmxNode(), ScriptNode.class);
        //返回所有节点数及关联的业务活动，类似left join 节点对应的有业务活动 节点无对应业务活动
        List<ScriptNode> samplerNodeList = getNodes(scriptNodes).stream().filter(Objects::nonNull)
            .filter(node -> NodeTypeEnum.SAMPLER == node.getType()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(samplerNodeList)) {
            return matchNum;
        }
        return nodeMatchBusinessActivity(samplerNodeList, processId);
    }

    @Override
    public Integer markMatchBusinessActivity(Long processId, ScriptNode node, Long activityId) {
        Integer count = 0;
        if(node == null) {
            return count;
        }
        BusinessLinkManageTableEntity businessLink = businessLinkManageTableMapper.selectById(
            activityId);
        if(null == businessLink) {
            return count;
        }
        SceneLinkRelateResult linkRelate = new SceneLinkRelateResult();
        linkRelate.setScriptIdentification(node.getIdentification());
        linkRelate.setScriptXpathMd5(node.getXpathMd5());
        linkRelate.setSceneId(String.valueOf(processId));
        linkRelate.setEntrance(businessLink.getEntrace());
        linkRelate.setBusinessLinkId(String.valueOf(businessLink.getLinkId()));
        linkRelate.setTechLinkId(businessLink.getRelatedTechLink());
        linkRelate.setParentBusinessLinkId(businessLink.getParentBusinessId());
        count++;
        return count;
    }

    /**
     * 将子节点全部遍历出来
     */
    private List<ScriptNode> getNodes(List<ScriptNode> nodes) {
        List<ScriptNode> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(nodes)) {
            return result;
        }
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

    /**
     * 节点和业务活动匹配
     */
    public Integer nodeMatchBusinessActivity(List<ScriptNode> nodeList, Long processId) {
        Integer count = 0;
        List<SceneLinkRelateResult> linkRelateList = new ArrayList<>();
        ActivityQueryParam queryParam = new ActivityQueryParam();
        queryParam.setEntranceList(new ArrayList<>(nodeList.stream().map(ScriptNode::getIdentification).distinct().collect(
            Collectors.toList())));
        List<ActivityListResult> activityList = activityDAO.getActivityList(queryParam);
        if(CollectionUtils.isEmpty(activityList)) {
            return count;
        }
        Map<String, ActivityListResult> activityMap = new HashMap<>();
        for(ActivityListResult activity : activityList) {
            activityMap.put(activity.getEntrace() + "_" + activity.getBusinessType(), activity);
        }
        //同一的入口，只保存一次
        Set<String> entranceSet = new HashSet<>();
        for(ScriptNode node : nodeList) {
            ActivityListResult link = activityMap.get(node.getIdentification() + "_" + BusinessTypeEnum.NORMAL_BUSINESS.getType());
            if(null == link) {
                link = activityMap.get(node.getIdentification() + "_" + BusinessTypeEnum.VIRTUAL_BUSINESS.getType());
            }
            if(null == link) {
                continue;
            }
            count++;
            if(entranceSet.contains(node.getIdentification())){
                continue;
            }
            entranceSet.add(node.getIdentification());
            SceneLinkRelateResult linkRelate = new SceneLinkRelateResult();
            linkRelate.setScriptIdentification(node.getIdentification());
            linkRelate.setScriptXpathMd5(node.getXpathMd5());
            linkRelate.setSceneId(String.valueOf(processId));
            linkRelate.setEntrance(link.getEntrace());
            linkRelate.setBusinessLinkId(String.valueOf(link.getActivityId()));
            linkRelate.setTechLinkId(link.getTechLinkId());
            linkRelate.setParentBusinessLinkId(link.getParentTechLinkId());
            linkRelateList.add(linkRelate);
        }
        sceneLinkRelateDAO.batchInsert(converts(linkRelateList));
        return count;
    }

    private SceneLinkRelateSaveParam convert(SceneLinkRelateResult linkRelate)  {
        SceneLinkRelateSaveParam saveParam = new SceneLinkRelateSaveParam();
        BeanUtil.copyProperties(linkRelate, saveParam);
        return saveParam;
    }

    private List<SceneLinkRelateSaveParam> converts(List<SceneLinkRelateResult> linkRelateList)  {
        List<SceneLinkRelateSaveParam> saveParamList = new ArrayList<>();
        for(SceneLinkRelateResult linkRelate : linkRelateList) {
            saveParamList.add(convert(linkRelate));
        }
        return saveParamList;
    }
}
