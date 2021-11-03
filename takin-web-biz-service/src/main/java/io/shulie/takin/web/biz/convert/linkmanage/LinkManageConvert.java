package io.shulie.takin.web.biz.convert.linkmanage;

import com.pamirs.takin.entity.domain.dto.linkmanage.ScriptJmxNode;
import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.VirtualActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageCreateRequest;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.SceneLinkRelateRequest;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateSaveParam;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhaoyong
 */
@Mapper
public interface LinkManageConvert {
    LinkManageConvert INSTANCE = Mappers.getMapper(LinkManageConvert.class);


    List<ScriptJmxNode> ofScriptNodeList(List<ScriptNode> scriptNodes);

    List<SceneLinkRelateSaveParam> ofSceneLinkRelateResults(List<SceneLinkRelateResult> sceneLinkRelateResults);

    @Mapping(target = "id",ignore = true)
    BusinessFlowDetailResponse ofBusinessFlowDetailResponse(ScriptManageDeployDetailResponse scriptManageDeployDetail);

    FileManageResponse ofFileManageResponse(FileManageResponse fileManageResponse);

    FileManageCreateRequest ofFileManageCreateRequest(FileManageUpdateRequest fileManageCreateRequest);

    List<FileManageUpdateRequest> ofFileManageResponseList(List<FileManageResponse> fileManageResponseList);

    SceneLinkRelateSaveParam ofSceneLinkRelateRequest(SceneLinkRelateRequest sceneLinkRelateRequest);

    @Mapping(target = "type",ignore = true)
    ActivityCreateRequest ofActivityCreateRequest(SceneLinkRelateRequest sceneLinkRelateRequest);

    @Mapping(target = "type",ignore = true)
    VirtualActivityCreateRequest ofVirtualActivityCreateRequest(SceneLinkRelateRequest sceneLinkRelateRequest);

    List<BusinessFlowListResponse> ofSceneResultList(List<SceneResult> list);
}
