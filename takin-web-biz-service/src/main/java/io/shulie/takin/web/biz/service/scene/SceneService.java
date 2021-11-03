package io.shulie.takin.web.biz.service.scene;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowPageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.SceneLinkRelateRequest;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowMatchResponse;
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


    BusinessFlowDetailResponse parseScriptAndSave(BusinessFlowParseRequest businessFlowParseRequest);

    BusinessFlowDetailResponse getBusinessFlowDetail(Long id);

    BusinessFlowDetailResponse uploadDataFile(BusinessFlowDataFileRequest businessFlowDataFileRequest);

    BusinessFlowDetailResponse getThreadGroupDetail(Long id, String xpathMd5);

    BusinessFlowMatchResponse autoMatchActivity(Long id);

    void matchActivity(SceneLinkRelateRequest sceneLinkRelateRequest);

    PagingList<BusinessFlowListResponse> getBusinessFlowList(BusinessFlowPageQueryRequest queryRequest);
}
