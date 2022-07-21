package io.shulie.takin.web.biz.convert.linkmanage;

import java.util.Collection;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.ScriptJmxNode;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.VirtualActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageCreateRequest;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.SceneLinkRelateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PluginConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.PluginConfigDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.data.model.mysql.SceneLinkRelateEntity;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateSaveParam;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author zhaoyong
 */
@Mapper
public interface LinkManageConvert {
    LinkManageConvert INSTANCE = Mappers.getMapper(LinkManageConvert.class);

    List<ScriptJmxNode> ofScriptNodeList(List<ScriptNode> scriptNodes);

    List<SceneLinkRelateSaveParam> ofSceneLinkRelateResults(List<SceneLinkRelateResult> sceneLinkRelateResults);

    @Mapping(target = "id", ignore = true)
    BusinessFlowDetailResponse ofBusinessFlowDetailResponse(ScriptManageDeployDetailResponse scriptManageDeployDetail);

    FileManageResponse ofFileManageResponse(FileManageResponse fileManageResponse);

    FileManageCreateRequest ofFileManageCreateRequest(FileManageUpdateRequest fileManageCreateRequest);

    List<FileManageUpdateRequest> ofFileManageResponseList(List<FileManageResponse> fileManageResponseList);

    @Mapping(target = "id", ignore = true)
    SceneLinkRelateSaveParam ofSceneLinkRelateRequest(SceneLinkRelateRequest sceneLinkRelateRequest);

    @Mapping(target = "type", ignore = true)
    ActivityCreateRequest ofActivityCreateRequest(SceneLinkRelateRequest sceneLinkRelateRequest);

    @Mapping(target = "type", ignore = true)
    VirtualActivityCreateRequest ofVirtualActivityCreateRequest(SceneLinkRelateRequest sceneLinkRelateRequest);

    List<BusinessFlowListResponse> ofSceneResultList(List<SceneResult> list);

    @Mapping(target = "status", expression = "java(sceneResult.getLinkRelateNum() == sceneResult.getTotalNodeNum() ? 1 : 0)")
    BusinessFlowListResponse ofSceneResult(SceneResult sceneResult);

    List<SceneRequest.Content> ofSceneLinkRelateResult(List<SceneLinkRelateResult> links);

    default SceneRequest.Content of(SceneLinkRelateResult sceneLinkRelateResult) {
        SceneRequest.Content content = new SceneRequest.Content();
        content.setPathMd5(sceneLinkRelateResult.getScriptXpathMd5());
        content.setBusinessActivityId(NumberUtils.toLong(sceneLinkRelateResult.getBusinessLinkId()));
        return content;
    }

    ScriptNode ofScriptJmxNode(ScriptJmxNode scriptJmxNode);

    FileManageUpdateRequest ofFileManageUpdateRequestbyFileManageResponse(FileManageResponse scriptFile);

    List<PluginConfigCreateRequest> ofPluginConfigDetailResponseList(List<PluginConfigDetailResponse> pluginConfigDetailResponseList);

    List<SceneLinkRelateEntity> ofSceneLinkRelateEntity(List<SceneLinkRelateResult> sceneLinkRelateResults);
}
