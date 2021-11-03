package io.shulie.takin.web.biz.service.scene.impl;

import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.entity.domain.dto.linkmanage.ScriptJmxNode;
import io.shulie.takin.cloud.common.utils.JmxUtil;
import io.shulie.takin.cloud.open.req.filemanager.FileCreateByStringParamReq;
import io.shulie.takin.cloud.open.req.scenemanage.ScriptAnalyzeRequest;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.ext.content.emus.NodeTypeEnum;
import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.shulie.takin.web.amdb.util.EntranceTypeUtils;
import io.shulie.takin.web.biz.convert.linkmanage.LinkManageConvert;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.VirtualActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowPageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.SceneLinkRelateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptManageDeployCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptManageDeployUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowMatchResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.biz.utils.DataUtil;
import io.shulie.takin.web.common.constant.ScriptManageConstant;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.enums.scene.SceneTypeEnum;
import io.shulie.takin.web.common.enums.script.FileTypeEnum;
import io.shulie.takin.web.common.enums.script.ScriptMVersionEnum;
import io.shulie.takin.web.common.enums.script.ScriptTypeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.dao.linkmanage.SceneDAO;
import io.shulie.takin.web.data.dao.scene.SceneLinkRelateDAO;
import io.shulie.takin.web.data.dao.scriptmanage.ScriptManageDAO;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.data.param.linkmanage.SceneCreateParam;
import io.shulie.takin.web.data.param.linkmanage.SceneUpdateParam;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateParam;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateSaveParam;
import io.shulie.takin.web.data.param.scene.ScenePageQueryParam;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import io.shulie.takin.web.data.result.scriptmanage.ScriptManageResult;
import io.shulie.takin.web.diff.api.DiffFileApi;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    @Autowired
    private ScriptManageService scriptManageService;
    @Autowired
    private ScriptManageDAO scriptManageDAO;
    @Autowired
    private SceneManageApi sceneManageApi;
    @Value("${file.upload.tmp.path:/tmp/takin/}")
    private String tmpFilePath;
    @Autowired
    private SceneService sceneService;
    @Autowired
    private DiffFileApi fileApi;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private SceneDAO sceneDAO;


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



    @Override
    public BusinessFlowDetailResponse parseScriptAndSave(BusinessFlowParseRequest businessFlowParseRequest) {
        FileManageUpdateRequest fileManageCreateRequest = businessFlowParseRequest.getScriptFile();
        //如果文件内容不为空，使用文件内容新建脚本文件
        if (StringUtils.isNotBlank(fileManageCreateRequest.getScriptContent())) {
            UUID uuid = UUID.randomUUID();
            fileManageCreateRequest.setUploadId(uuid.toString());
            String tempFile = tmpFilePath + "/" + uuid + "/" + fileManageCreateRequest.getFileName();
            FileCreateByStringParamReq fileCreateByStringParamReq = new FileCreateByStringParamReq();
            fileCreateByStringParamReq.setFileContent(fileManageCreateRequest.getScriptContent());
            fileCreateByStringParamReq.setFilePath(tempFile);
            String fileMd5 = fileApi.createFileByPathAndString(fileCreateByStringParamReq);
            fileManageCreateRequest.setMd5(fileMd5);
        }

        //解析脚本
        ScriptAnalyzeRequest analyzeRequest = new ScriptAnalyzeRequest();
        analyzeRequest.setScriptFile(tmpFilePath + "/" + fileManageCreateRequest.getUploadId() + "/" + fileManageCreateRequest.getFileName());
        ResponseResult<List<ScriptNode>> listResponseResult = sceneManageApi.scriptAnalyze(analyzeRequest);
        if (!listResponseResult.getSuccess() || CollectionUtils.isEmpty(listResponseResult.getData())) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "脚本文件解析失败！" + listResponseResult.getError().getMsg());
        }
        List<ScriptNode> data = listResponseResult.getData();
        List<ScriptNode> testPlan = JmxUtil.getScriptNodeByType(NodeTypeEnum.TEST_PLAN, data);
        if (CollectionUtils.isEmpty(testPlan)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "脚本文件没有解析到测试计划！");
        }

        if (businessFlowParseRequest.getId() == null) {
            saveBusinessFlow(testPlan.get(0).getTestName(), data, fileManageCreateRequest);
        } else {
            updateBusinessFlow(businessFlowParseRequest.getId(), businessFlowParseRequest.getScriptFile(), null);
        }

        BusinessFlowDetailResponse result = new BusinessFlowDetailResponse();
        result.setId(businessFlowParseRequest.getId());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBusinessFlow(String testName, List<ScriptNode> data, FileManageUpdateRequest fileManageCreateRequest) {
        //保存业务流程
        SceneCreateParam sceneCreateParam = new SceneCreateParam();
        sceneCreateParam.setSceneName(testName);
        sceneCreateParam.setCustomerId(WebPluginUtils.getCustomerId());
        sceneCreateParam.setUserId(WebPluginUtils.getUserId());
        sceneCreateParam.setLinkRelateNum(0);
        sceneCreateParam.setScriptJmxNode(JsonHelper.bean2Json(data));
        sceneCreateParam.setTotalNodeNum(JmxUtil.getNodeNumByType(NodeTypeEnum.SAMPLER, data));
        sceneCreateParam.setType(SceneTypeEnum.JMETER_UPLOAD_SCENE.getType());
        sceneDAO.insert(sceneCreateParam);

        //新增脚本文件
        ScriptManageDeployCreateRequest createRequest = new ScriptManageDeployCreateRequest();
        //脚本文件名称去重
        String scriptName = sceneCreateParam.getSceneName();
        List<ScriptManageResult> scriptManageResults = scriptManageDAO.selectScriptManageByName(sceneCreateParam.getSceneName());
        if (CollectionUtils.isNotEmpty(scriptManageResults)) {
            scriptName = sceneCreateParam.getSceneName() + "_" + DateUtils.dateToString(new Date(), "yyyyMMddHHmmss");
        }
        createRequest.setFileManageCreateRequests(Collections.singletonList(LinkManageConvert.INSTANCE.ofFileManageCreateRequest(fileManageCreateRequest)));
        createRequest.setName(scriptName);
        createRequest.setType(ScriptTypeEnum.JMETER.getCode());
        createRequest.setMVersion(ScriptMVersionEnum.SCRIPT_M_1.getCode());
        createRequest.setRefType(ScriptManageConstant.BUSINESS_PROCESS_REF_TYPE);
        createRequest.setRefValue(sceneCreateParam.getId().toString());
        Long scriptManageId = scriptManageService.createScriptManage(createRequest);

        //更新业务流程
        SceneUpdateParam sceneUpdateParam = new SceneUpdateParam();
        sceneUpdateParam.setId(sceneCreateParam.getId());
        sceneCreateParam.setScriptDeployId(scriptManageId);
        sceneDAO.update(sceneUpdateParam);
    }


    @Override
    public BusinessFlowDetailResponse getBusinessFlowDetail(Long id) {
        BusinessFlowDetailResponse result = new BusinessFlowDetailResponse();
        SceneResult sceneResult = sceneDAO.getSceneDetail(id);
        if (sceneResult == null) {
            return result;
        }
        List<ScriptNode> scriptNodes = JsonHelper.json2List(sceneResult.getScriptJmxNode(), ScriptNode.class);
        //将节点树处理成线程组在最外层的形式
        List<ScriptNode> scriptNodeByType = JmxUtil.getScriptNodeByType(NodeTypeEnum.THREAD_GROUP, scriptNodes);
        List<ScriptJmxNode> scriptJmxNodes = LinkManageConvert.INSTANCE.ofScriptNodeList(scriptNodeByType);

        ScriptManageDeployDetailResponse scriptManageDeployDetail = scriptManageService.getScriptManageDeployDetail(sceneResult.getScriptDeployId());
        if (scriptManageDeployDetail != null) {
            //脚本文件单独存储
            if (CollectionUtils.isNotEmpty(scriptManageDeployDetail.getFileManageResponseList())) {
                List<FileManageResponse> fileManageResponses = scriptManageDeployDetail.getFileManageResponseList().stream()
                        .filter(o -> FileTypeEnum.SCRIPT.getCode().equals(o.getFileType())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(fileManageResponses)) {
                    result.setScriptFile(LinkManageConvert.INSTANCE.ofFileManageResponse(fileManageResponses.get(0)));
                    scriptManageDeployDetail.getFileManageResponseList().remove(fileManageResponses.get(0));
                }
            }
            result = LinkManageConvert.INSTANCE.ofBusinessFlowDetailResponse(scriptManageDeployDetail);
            int fileManageNum = result.getFileManageResponseList() == null ? 0 : result.getFileManageResponseList().size();
            int attachmentManageNum = result.getAttachmentManageResponseList() == null ? 0 : result.getAttachmentManageResponseList().size();
            result.setFileNum(fileManageNum + attachmentManageNum);
        }

        result.setScriptJmxNodeList(scriptJmxNodes);
        result.setThreadGroupNum(scriptNodeByType.size());
        toBusinessFlowDetailResponse(sceneResult, result);
        return result;
    }

    @Override
    public BusinessFlowDetailResponse uploadDataFile(BusinessFlowDataFileRequest businessFlowDataFileRequest) {
        updateBusinessFlow(businessFlowDataFileRequest.getId(), null, businessFlowDataFileRequest);
        BusinessFlowDetailResponse result = new BusinessFlowDetailResponse();
        result.setId(businessFlowDataFileRequest.getId());
        return result;
    }

    @Override
    public BusinessFlowDetailResponse getThreadGroupDetail(Long id, String xpathMd5) {
        BusinessFlowDetailResponse result = new BusinessFlowDetailResponse();
        SceneResult sceneResult = sceneDAO.getSceneDetail(id);
        if (sceneResult == null) {
            return result;
        }
        List<ScriptNode> scriptNodes = JsonHelper.json2List(sceneResult.getScriptJmxNode(), ScriptNode.class);
        //将节点树处理成线程组在最外层的形式
        List<ScriptNode> scriptNodeByType = JmxUtil.getScriptNodeByType(NodeTypeEnum.THREAD_GROUP, scriptNodes);
        List<ScriptJmxNode> scriptJmxNodes = LinkManageConvert.INSTANCE.ofScriptNodeList(scriptNodeByType);
        List<ScriptJmxNode> threadJmxNode = scriptJmxNodes.stream().filter(o -> o.getXpathMd5().equals(xpathMd5)).collect(Collectors.toList());
        SceneLinkRelateParam sceneLinkRelateParam = new SceneLinkRelateParam();
        sceneLinkRelateParam.setSceneIds(Collections.singletonList(id.toString()));
        List<SceneLinkRelateResult> sceneLinkRelateList = sceneLinkRelateDAO.getList(sceneLinkRelateParam);
        dealScriptJmxNodes(sceneLinkRelateList, threadJmxNode);

        result.setScriptJmxNodeList(threadJmxNode);
        result.setThreadGroupNum(scriptNodeByType.size());
        toBusinessFlowDetailResponse(sceneResult, result);
        return result;
    }

    @Override
    public BusinessFlowMatchResponse autoMatchActivity(Long id) {
        BusinessFlowMatchResponse result = new BusinessFlowMatchResponse();
        SceneResult sceneResult = sceneDAO.getSceneDetail(id);
        if (sceneResult == null) {
            return result;
        }
        result.setId(id);
        result.setBusinessProcessName(sceneResult.getSceneName());
        List<ScriptNode> scriptNodes = JsonHelper.json2List(sceneResult.getScriptJmxNode(), ScriptNode.class);
        int nodeNumByType = JmxUtil.getNodeNumByType(NodeTypeEnum.SAMPLER, scriptNodes);
        List<SceneLinkRelateResult> sceneLinkRelateResults = sceneService.nodeLinkToBusinessActivity(scriptNodes, id);
        //查询已有的匹配关系,删除现在没有关联的节点
        SceneLinkRelateParam sceneLinkRelateParam = new SceneLinkRelateParam();
        sceneLinkRelateParam.setSceneIds(Collections.singletonList(id.toString()));
        List<SceneLinkRelateResult> sceneLinkRelateList = sceneLinkRelateDAO.getList(sceneLinkRelateParam);
        if (CollectionUtils.isNotEmpty(sceneLinkRelateList)){
            List<Long> oldIds = sceneLinkRelateList.stream().map(SceneLinkRelateResult::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(sceneLinkRelateResults)){
                List<Long> longList = sceneLinkRelateResults.stream().map(SceneLinkRelateResult::getId)
                        .filter(Objects::nonNull).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(longList)){
                    oldIds = oldIds.stream().filter(o -> !longList.contains(o)).collect(Collectors.toList());
                }
            }
            sceneLinkRelateDAO.deleteByIds(oldIds);
        }

        if (CollectionUtils.isNotEmpty(sceneLinkRelateResults)){
            sceneLinkRelateDAO.batchInsertOrUpdate(LinkManageConvert.INSTANCE.ofSceneLinkRelateResults(sceneLinkRelateResults));
        }
        int matchNum = CollectionUtils.isEmpty(sceneLinkRelateResults) ? 0 : sceneLinkRelateResults.size();
        result.setMatchNum(matchNum);
        result.setUnMatchNum(nodeNumByType - matchNum);
        return result;
    }

    @Override
    public void matchActivity(SceneLinkRelateRequest sceneLinkRelateRequest) {
        String entrance;
        if (BusinessTypeEnum.NORMAL_BUSINESS.getType().equals(sceneLinkRelateRequest.getBusinessType())){
            //普通业务活动
            if (sceneLinkRelateRequest.getBusinessLinkId() == null) {
                //业务活动id为空，新增业务活动
                ActivityCreateRequest request = LinkManageConvert.INSTANCE.ofActivityCreateRequest(sceneLinkRelateRequest);
                request.setType(EntranceTypeEnum.getEnumByType(sceneLinkRelateRequest.getType().getType()));
                Long activity = activityService.createActivity(request);
                sceneLinkRelateRequest.setBusinessLinkId(activity);
            }
            entrance = ActivityUtil.buildEntrance(sceneLinkRelateRequest.getMethod(), JmxUtil.pathGuiYi(sceneLinkRelateRequest.getServiceName()),
                    sceneLinkRelateRequest.getRpcType());
        }else if (BusinessTypeEnum.VIRTUAL_BUSINESS.getType().equals(sceneLinkRelateRequest.getBusinessType())){
            if (sceneLinkRelateRequest.getBusinessLinkId() == null) {
                VirtualActivityCreateRequest createRequest = LinkManageConvert.INSTANCE.ofVirtualActivityCreateRequest(sceneLinkRelateRequest);
                createRequest.setType(EntranceTypeEnum.getEnumByType(sceneLinkRelateRequest.getType().getType()));
                Long virtualActivity = activityService.createVirtualActivity(createRequest);
                sceneLinkRelateRequest.setBusinessLinkId(virtualActivity);
            }
            entrance = ActivityUtil.buildVirtualEntrance(sceneLinkRelateRequest.getServiceName(),
                    EntranceTypeUtils.getRpcType(sceneLinkRelateRequest.getType().getType()).getRpcType());
        }else {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_UPDATE_ERROR,"不是已知的业务活动类型！");
        }

        SceneLinkRelateSaveParam saveParam = LinkManageConvert.INSTANCE.ofSceneLinkRelateRequest(sceneLinkRelateRequest);
        saveParam.setEntrance(entrance);
        sceneLinkRelateDAO.batchInsertOrUpdate(Collections.singletonList(saveParam));
    }

    @Override
    public PagingList<BusinessFlowListResponse> getBusinessFlowList(BusinessFlowPageQueryRequest queryRequest) {
        ScenePageQueryParam queryParam = new ScenePageQueryParam();
        queryParam.setSceneName(queryParam.getSceneName());
        queryParam.setCurrent(queryRequest.getCurrent());
        queryParam.setPageSize(queryRequest.getPageSize());
        PagingList<SceneResult> pageList = sceneDAO.selectPageList(queryParam);
        List<BusinessFlowListResponse> responses = LinkManageConvert.INSTANCE.ofSceneResultList(pageList.getList());
        return PagingList.of(responses, pageList.getTotal());
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateBusinessFlow(Long businessFlowId, FileManageUpdateRequest scriptFile, BusinessFlowDataFileRequest businessFlowDataFileRequest) {
        SceneResult sceneResult = sceneDAO.getSceneDetail(businessFlowId);
        if (sceneResult == null) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, "没有找到对应的业务流程！");
        }
        //取之前脚本中关联的其他文件
        Long oldScriptDeployId = sceneResult.getScriptDeployId();
        ScriptManageDeployDetailResponse result = new ScriptManageDeployDetailResponse();
        result.setId(oldScriptDeployId);
        scriptManageService.setFileList(result);
        List<FileManageResponse> fileManageResponseList = result.getFileManageResponseList();
        int unMatchNum = sceneResult.getTotalNodeNum() - sceneResult.getLinkRelateNum();
        ScriptManageDeployUpdateRequest updateRequest = new ScriptManageDeployUpdateRequest();
        if (scriptFile == null) {
            List<FileManageResponse> dataFileManageResponseList = fileManageResponseList.stream().filter(o ->
                    FileTypeEnum.SCRIPT.getCode().equals(o.getFileType())).collect(Collectors.toList());
            //更新脚本
            if (CollectionUtils.isEmpty(businessFlowDataFileRequest.getFileManageUpdateRequests())) {
                businessFlowDataFileRequest.setFileManageUpdateRequests(new ArrayList<>());
            }
            businessFlowDataFileRequest.getFileManageUpdateRequests().addAll(LinkManageConvert.INSTANCE
                    .ofFileManageResponseList(dataFileManageResponseList));

            updateRequest.setFileManageUpdateRequests(businessFlowDataFileRequest.getFileManageUpdateRequests());
            updateRequest.setAttachmentManageUpdateRequests(businessFlowDataFileRequest.getAttachmentManageUpdateRequests());
            updateRequest.setPluginConfigUpdateRequests(businessFlowDataFileRequest.getPluginConfigUpdateRequests());

            //自动匹配
            BusinessFlowMatchResponse businessFlowMatchResponse = autoMatchActivity(businessFlowId);
            if (businessFlowMatchResponse != null){
                unMatchNum = businessFlowMatchResponse.getUnMatchNum();
            }
        } else {
            List<FileManageResponse> dataFileManageResponseList = fileManageResponseList.stream().filter(o ->
                    !FileTypeEnum.SCRIPT.getCode().equals(o.getFileType())).collect(Collectors.toList());
            List<FileManageUpdateRequest> updateFileManageRequests = new ArrayList<>();
            updateFileManageRequests.add(scriptFile);
            updateFileManageRequests.addAll(LinkManageConvert.INSTANCE.ofFileManageResponseList(dataFileManageResponseList));
            updateRequest.setFileManageUpdateRequests(updateFileManageRequests);
        }

        //更新脚本
        Long scriptDeployId = scriptManageService.updateScriptManage(updateRequest);
        SceneUpdateParam sceneUpdateParam = new SceneUpdateParam();
        //更新业务流程
        sceneUpdateParam.setScriptDeployId(scriptDeployId);
        sceneDAO.update(sceneUpdateParam);

        if (unMatchNum == 0){
            //TODO 更新压测场景

        }

    }

    private void toBusinessFlowDetailResponse(SceneResult sceneResult, BusinessFlowDetailResponse result) {
        result.setSceneLevel(sceneResult.getSceneLevel());
        result.setIsCode(sceneResult.getIsCore());
        result.setBusinessProcessName(sceneResult.getSceneName());
        result.setId(sceneResult.getId());
    }

    private void dealScriptJmxNodes(List<SceneLinkRelateResult> sceneLinkRelateResults, List<ScriptJmxNode> scriptJmxNodes) {
        if (CollectionUtils.isNotEmpty(sceneLinkRelateResults)) {
            Map<String, String> xpathMd5Map = sceneLinkRelateResults.stream().filter(o -> StringUtils.isNotBlank(o.getScriptXpathMd5()))
                    .collect(Collectors.toMap(SceneLinkRelateResult::getScriptXpathMd5, SceneLinkRelateResult::getBusinessLinkId));
            List<Long> businessLinkIds = sceneLinkRelateResults.stream().map(o -> Long.parseLong(o.getBusinessLinkId())).collect(Collectors.toList());
            ActivityQueryParam activityQueryParam = new ActivityQueryParam();
            activityQueryParam.setActivityIds(businessLinkIds);
            List<ActivityListResult> activityList = activityDAO.getActivityList(activityQueryParam);
            if (CollectionUtils.isNotEmpty(activityList)) {
                Map<String, ActivityListResult> collect = activityList.stream().collect(Collectors.toMap(o -> o.getActivityId().toString(), t -> t));
                dealScriptJmxNodes(scriptJmxNodes, xpathMd5Map, collect);
            }
        }
    }

    /**
     * 填充处理业务活动信息
     *
     * @param scriptJmxNodes
     * @param xpathMd5Map
     * @param activityMap
     */
    private void dealScriptJmxNodes(List<ScriptJmxNode> scriptJmxNodes, Map<String, String> xpathMd5Map, Map<String, ActivityListResult> activityMap) {
        if (CollectionUtils.isNotEmpty(scriptJmxNodes)) {
            for (ScriptJmxNode scriptJmxNode : scriptJmxNodes) {
                if (xpathMd5Map.get(scriptJmxNode.getXpathMd5()) != null) {
                    ActivityListResult activityListResult = activityMap.get(xpathMd5Map.get(scriptJmxNode.getXpathMd5()));
                    if (activityListResult != null) {
                        scriptJmxNode.setBusinessApplicationName(activityListResult.getActivityName());
                        ActivityUtil.EntranceJoinEntity entranceJoinEntity;
                        if (BusinessTypeEnum.VIRTUAL_BUSINESS.getType().equals(activityListResult.getBusinessType())) {
                            entranceJoinEntity = ActivityUtil.covertVirtualEntrance(activityListResult.getEntrace());
                        } else {
                            entranceJoinEntity = ActivityUtil.covertEntrance(activityListResult.getEntrace());
                        }
                        scriptJmxNode.setBusinessServicePath(entranceJoinEntity.getServiceName());
                    }
                }
                if (CollectionUtils.isNotEmpty(scriptJmxNode.getChildren())) {
                    dealScriptJmxNodes(scriptJmxNode.getChildren(), xpathMd5Map, activityMap);
                }
            }
        }
    }
}
