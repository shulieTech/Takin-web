package io.shulie.takin.web.biz.service.scene;

import com.pamirs.takin.entity.domain.dto.linkmanage.ScriptJmxNode;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.web.biz.pojo.request.linkmanage.*;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowThreadResponse;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowMatchResponse;
import io.shulie.takin.web.data.model.mysql.SceneEntity;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;

import java.util.List;

/**
 * 业务流程
 *
 * @author liyuanba
 * @date 2021/10/27 9:50 上午
 */
public interface SceneService {
    /**
     * 解析结果节点匹配
     *
     * @param nodes   脚本解析结果
     * @param sceneId 当前业务流程ID
     * @return 节点和业务活动关联关系
     */
    List<SceneLinkRelateResult> nodeLinkToBusinessActivity(List<ScriptNode> nodes, Long sceneId);

    BusinessFlowDetailResponse parseScriptAndSave(BusinessFlowParseRequest businessFlowParseRequest);

    BusinessFlowDetailResponse getBusinessFlowDetail(Long id);

    /**
     * 通过id获取业务流程记录
     */
    SceneResult getScene(Long id);

    BusinessFlowDetailResponse uploadDataFile(BusinessFlowDataFileRequest businessFlowDataFileRequest);

    BusinessFlowThreadResponse getThreadGroupDetail(Long id, String xpathMd5);

    List<SceneLinkRelateResult> getSceneLinkRelates(Long sceneId);

    BusinessFlowMatchResponse autoMatchActivity(Long id);

    void matchActivity(SceneLinkRelateRequest sceneLinkRelateRequest);

    PagingList<BusinessFlowListResponse> getBusinessFlowList(BusinessFlowPageQueryRequest queryRequest);

    /**
     * 获取业务活动
     *
     * @return 业务流程列表
     */
    List<SceneEntity> businessActivityFlowList();

    void updateBusinessFlow(BusinessFlowUpdateRequest businessFlowUpdateRequest);
}
