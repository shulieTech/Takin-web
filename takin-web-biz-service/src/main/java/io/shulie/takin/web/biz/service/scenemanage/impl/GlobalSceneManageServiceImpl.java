package io.shulie.takin.web.biz.service.scenemanage.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import io.shulie.takin.cloud.common.utils.JmxUtil;
import io.shulie.takin.cloud.entrypoint.file.CloudFileApi;
import io.shulie.takin.cloud.entrypoint.scene.mix.SceneMixApi;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.sdk.model.request.file.UploadRequest;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageQueryReq;
import io.shulie.takin.cloud.sdk.model.response.file.UploadResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneDetailV2Response;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneRequest;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.utils.file.FileManagerHelper;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.convert.placeholdermanage.PlaceholderManageConvert;
import io.shulie.takin.web.biz.convert.scenemanage.SceneManageConvert;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.SceneLinkRelateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PluginConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.placeholdermanage.PlaceholderManageResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.GlobalSceneManageResponse;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.biz.service.scenemanage.GlobalSceneManageService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.enums.script.FileTypeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.data.dao.filemanage.FileManageDAO;
import io.shulie.takin.web.data.dao.linkmanage.SceneDAO;
import io.shulie.takin.web.data.dao.scene.SceneLinkRelateDAO;
import io.shulie.takin.web.data.dao.scenemanage.GlobalSceneManageDAO;
import io.shulie.takin.web.data.dao.scriptmanage.ScriptFileRefDAO;
import io.shulie.takin.web.data.model.mysql.GlobalSceneManageEntity;
import io.shulie.takin.web.data.model.mysql.PlaceholderManageEntity;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateParam;
import io.shulie.takin.web.data.result.filemanage.FileManageResult;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import io.shulie.takin.web.data.result.scriptmanage.ScriptFileRefResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GlobalSceneManageServiceImpl implements GlobalSceneManageService {

    /**
     * 公共场景存储脚本在原来的脚本加上global
     */
    @Value("${file.upload.script.path:/nfs/takin/script/}")
    private String scriptFilePath;

    @Resource
    private SceneMixApi sceneMixApi;
    @Resource
    private GlobalSceneManageDAO globalSceneManageDAO;
    @Resource
    ScriptFileRefDAO scriptFileRefDao;
    @Resource
    FileManageDAO fileManageDao;
    @Resource
    private CloudFileApi cloudFileApi;
    @Resource
    private SceneService sceneService;
    @Resource
    private SceneDAO sceneDao;
    @Resource
    private SceneLinkRelateDAO sceneLinkRelateDao;
    @Resource
    private SceneManageService sceneManageService;

    @Override
    public void sceneToGlobal(Long sceneManageId) {
        SceneManageQueryReq request = new SceneManageQueryReq() {
            {
                setSceneId(sceneManageId);
            }
        };
        WebPluginUtils.fillCloudUserData(request);
        SceneDetailV2Response detail = sceneMixApi.detail(request);
        if (detail == null) {
            return;
        }

        GlobalSceneManageEntity globalSceneManageEntity = new GlobalSceneManageEntity();
        globalSceneManageEntity.setSceneManageId(sceneManageId);
        globalSceneManageEntity.setSceneName(detail.getBasicInfo().getName());
        globalSceneManageEntity.setSceneDetail(JSONObject.toJSONString(detail));

        // 根据脚本主键获取文件主键集合
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDao.selectFileIdsByScriptDeployId(detail.getBasicInfo().getScriptId());
        // 根据文件主键集合查询文件信息
        List<Long> fileIds = scriptFileRefResults.stream().map(ScriptFileRefResult::getFileId).collect(Collectors.toList());
        // 组装返回数据
        List<FileManageResult> fileManageResults = fileManageDao.selectFileManageByIds(fileIds);
        globalSceneManageDAO.save(globalSceneManageEntity);

        List<SceneRequest.File> fileList = fileManageResults.stream().map(t -> new SceneRequest.File() {{
            Map<String, Object> extend = JSONObject.parseObject(t.getFileExtend(), new TypeReference<Map<String, Object>>() {
            });
            setName(t.getFileName());
            //将原本的脚本复制到另外的位置
            String targetPath = scriptFilePath + File.separator + "global" + File.separator + globalSceneManageEntity.getId()
                    + File.separator + t.getFileName();
            FileUtil.copy(t.getUploadPath(), targetPath, true);
            setPath(targetPath);
            setType(t.getFileType());
            setExtend(extend);
        }}).collect(Collectors.toList());
        globalSceneManageEntity.setFileContent(JSONObject.toJSONString(fileList));
        globalSceneManageDAO.updateById(globalSceneManageEntity);
    }

    @Override
    @Transactional
    public void globalToScene(Long id) {
        GlobalSceneManageEntity globalSceneManageEntity = globalSceneManageDAO.getById(id);
        String sceneDetail = globalSceneManageEntity.getSceneDetail();
        SceneDetailV2Response detailResult = JSONObject.parseObject(sceneDetail, SceneDetailV2Response.class);
        //创建业务流程，同时获取业务流程id
        SceneResult businessFlow = createBusinessFlow(globalSceneManageEntity);
        if (businessFlow == null) {
            throw new TakinWebException(TakinErrorEnum.GOLBAL_SCENE_COPY_EXCEPTION, "场景业务流程失败");
        }
        //查询已有的匹配关系
        SceneLinkRelateParam sceneLinkRelateParam = new SceneLinkRelateParam();
        sceneLinkRelateParam.setSceneIds(Collections.singletonList(businessFlow.getId().toString()));
        List<SceneLinkRelateResult> sceneLinkRelateList = sceneLinkRelateDao.getList(sceneLinkRelateParam);
        Map<String, List<SceneLinkRelateResult>> stringListMap = sceneLinkRelateList.stream().collect(Collectors.groupingBy(SceneLinkRelateResult::getScriptXpathMd5));

        //创建压测场景
        SceneRequest createReq = new SceneRequest();
        SceneRequest.BasicInfo basicInfo = detailResult.getBasicInfo();
        basicInfo.setScriptId(businessFlow.getScriptDeployId());
        basicInfo.setBusinessFlowId(businessFlow.getId());
        basicInfo.setSceneId(null);
        basicInfo.setName(basicInfo.getName() + "_Global");
        createReq.setBasicInfo(basicInfo);
        createReq.setAnalysisResult(detailResult.getAnalysisResult());
        if (detailResult.getContent() != null) {
            ArrayList<SceneRequest.Content> contents = new ArrayList<>(detailResult.getContent().values());
            contents.forEach(o -> {
                List<SceneLinkRelateResult> sceneLinkRelateResults = stringListMap.get(o.getPathMd5());
                if (CollectionUtils.isNotEmpty(sceneLinkRelateResults)) {
                    o.setBusinessActivityId(Long.parseLong(sceneLinkRelateResults.get(0).getBusinessLinkId()));
                }
            });
            createReq.setContent(contents);
        }
        createReq.setConfig(detailResult.getConfig());
        createReq.setGoal(detailResult.getGoal());
        List<SceneRequest.MonitoringGoal> monitoringGoal = new ArrayList<>();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(detailResult.getDestroyMonitoringGoal())) {
            detailResult.getDestroyMonitoringGoal().forEach(o -> {
                o.setId(null);
            });
            monitoringGoal.addAll(detailResult.getDestroyMonitoringGoal());
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(detailResult.getWarnMonitoringGoal())) {
            detailResult.getWarnMonitoringGoal().forEach(o -> {
                o.setId(null);
            });
            monitoringGoal.addAll(detailResult.getWarnMonitoringGoal());
        }
        createReq.setMonitoringGoal(monitoringGoal);
        createReq.setDataValidation(detailResult.getDataValidation());
        createReq.setFile(sceneManageService.assembleFileList(businessFlow.getScriptDeployId()));
        WebPluginUtils.fillCloudUserData(createReq);
        sceneMixApi.create(createReq);
    }

    @Override
    public PagingList<GlobalSceneManageResponse> list(Integer current, Integer pageSize, String name) {
        if (current == null) {
            current = 0;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        Page<GlobalSceneManageEntity> page = new Page<>(current + 1, pageSize);
        QueryWrapper<GlobalSceneManageEntity> wrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(name)) {
            wrapper.lambda().like(GlobalSceneManageEntity::getSceneName, name);
        }

        Page<GlobalSceneManageEntity> globalSceneManageEntityPage = globalSceneManageDAO.page(page, wrapper);
        if (globalSceneManageEntityPage == null) {
            return PagingList.empty();
        }
        List<GlobalSceneManageEntity> records = globalSceneManageEntityPage.getRecords();
        List<GlobalSceneManageResponse> globalSceneManageResponses = SceneManageConvert.INSTANCE.ofGlobalSceneManageResponse(records);
        return PagingList.of(globalSceneManageResponses, globalSceneManageEntityPage.getTotal());
    }

    @Override
    public void cancelSceneToGlobal(Long sceneManageId) {
        QueryWrapper<GlobalSceneManageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(GlobalSceneManageEntity::getSceneManageId,sceneManageId);
        List<GlobalSceneManageEntity> sceneManageEntities = globalSceneManageDAO.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(sceneManageEntities)){
            List<Long> collect = sceneManageEntities.stream().map(GlobalSceneManageEntity::getId).collect(Collectors.toList());
            globalSceneManageDAO.removeByIds(collect);
        }
    }

    private SceneResult createBusinessFlow(GlobalSceneManageEntity globalSceneManageEntity) {
        //上传jmx脚本文件
        String fileContent = globalSceneManageEntity.getFileContent();
        List<SceneRequest.File> files = JSONObject.parseArray(fileContent, SceneRequest.File.class);
        //todo 后面考虑其他文件的存储,当前先满足最简单的逻辑
        List<SceneRequest.File> scriptFileList = files.stream().filter(o -> o.getType().equals(FileTypeEnum.SCRIPT.getCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(scriptFileList)) {
            log.warn("公共数据存在问题，没有对应的脚本文件");
            return null;
        }
        UploadRequest uploadRequest = new UploadRequest();
        uploadRequest.setFileList(Collections.singletonList(FileUtil.file(scriptFileList.get(0).getPath())));
        WebPluginUtils.fillCloudUserData(uploadRequest);
        List<UploadResponse> uploadResponses = cloudFileApi.upload(uploadRequest);
        UploadResponse uploadResponse = uploadResponses.get(0);

        //解析并保存jmx脚本文件
        BusinessFlowParseRequest businessFlowParseRequest = new BusinessFlowParseRequest();
        FileManageUpdateRequest scriptFile = new FileManageUpdateRequest();
        scriptFile.setUploadId(uploadResponse.getUploadId());
        scriptFile.setFileName(uploadResponse.getFileName());
        scriptFile.setFileType(FileTypeEnum.SCRIPT.getCode());
        scriptFile.setUploadTime(new Date());
        scriptFile.setDownloadUrl(uploadResponse.getDownloadUrl());
        scriptFile.setIsDeleted(0);
        businessFlowParseRequest.setScriptFile(scriptFile);

        //todo 后面可以把插件也加上
        List<PluginConfigCreateRequest> pluginList = new ArrayList<>();
        businessFlowParseRequest.setPluginList(pluginList);
        BusinessFlowDetailResponse sceneDetailDto = sceneService.parseScriptAndSave(businessFlowParseRequest);

        //字段匹配业务活动
        sceneService.autoMatchActivity(sceneDetailDto.getId());

        SceneResult sceneDetail = sceneDao.getSceneDetail(sceneDetailDto.getId());
        String scriptJmxNode = sceneDetail.getScriptJmxNode();
        List<ScriptNode> scriptNodes = JsonHelper.json2List(scriptJmxNode, ScriptNode.class);

        //查询已有的匹配关系
        SceneLinkRelateParam sceneLinkRelateParam = new SceneLinkRelateParam();
        sceneLinkRelateParam.setSceneIds(Collections.singletonList(sceneDetailDto.getId().toString()));
        List<SceneLinkRelateResult> sceneLinkRelateList = sceneLinkRelateDao.getList(sceneLinkRelateParam);
        Map<String, List<SceneLinkRelateResult>> stringListMap = sceneLinkRelateList.stream().collect(Collectors.groupingBy(SceneLinkRelateResult::getScriptXpathMd5));

        List<ScriptNode> samplerScriptNodeList = JmxUtil.getScriptNodeByType(NodeTypeEnum.SAMPLER, scriptNodes);

        if (CollectionUtils.isNotEmpty(samplerScriptNodeList)) {
            List<ScriptNode> nodeList = samplerScriptNodeList.stream().filter(o -> !stringListMap.containsKey(o.getXpathMd5()))
                    .collect(Collectors.toList());
            //找到没有自动匹配成功的数据，进行新增并匹配虚拟业务活动
            nodeList.forEach(scriptNode -> {
                SceneLinkRelateRequest sceneLinkRelateRequest = new SceneLinkRelateRequest();
                sceneLinkRelateRequest.setBusinessFlowId(sceneDetailDto.getId());
                sceneLinkRelateRequest.setIdentification(scriptNode.getIdentification());
                sceneLinkRelateRequest.setXpathMd5(scriptNode.getXpathMd5());
                sceneLinkRelateRequest.setTestName(scriptNode.getTestName());
                sceneLinkRelateRequest.setSamplerType(scriptNode.getSamplerType());
                sceneLinkRelateRequest.setBusinessType(BusinessTypeEnum.VIRTUAL_BUSINESS.getType());
                sceneService.matchActivity(sceneLinkRelateRequest);
            });
        }
        return sceneDetail;
    }


}
