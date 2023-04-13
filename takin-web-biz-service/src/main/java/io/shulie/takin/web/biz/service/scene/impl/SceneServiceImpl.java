package io.shulie.takin.web.biz.service.scene.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pamirs.takin.entity.domain.dto.linkmanage.ScriptJmxNode;
import io.shulie.takin.adapter.api.entrypoint.scene.mix.SceneMixApi;
import io.shulie.takin.adapter.api.model.request.filemanager.FileCreateByStringParamReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptAnalyzeRequest;
import io.shulie.takin.adapter.api.model.response.scenemanage.SynchronizeRequest;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.common.utils.JmxUtil;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.shulie.takin.web.biz.convert.linkmanage.LinkManageConvert;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.VirtualActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.*;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PluginConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptManageDeployCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptManageDeployUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowMatchResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowThreadResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.pts.PtsParseJmxToObjectTools;
import io.shulie.takin.web.biz.service.scene.ApplicationBusinessActivityService;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.biz.utils.FileEncoder;
import io.shulie.takin.web.common.constant.ScriptManageConstant;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.enums.scene.SceneTypeEnum;
import io.shulie.takin.web.common.enums.script.FileTypeEnum;
import io.shulie.takin.web.common.enums.script.ScriptMVersionEnum;
import io.shulie.takin.web.common.enums.script.ScriptTypeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.vo.WebOptionEntity;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.filemanage.FileManageDAO;
import io.shulie.takin.web.data.dao.linkmanage.SceneDAO;
import io.shulie.takin.web.data.dao.scene.SceneLinkRelateDAO;
import io.shulie.takin.web.data.dao.scriptmanage.ScriptManageDAO;
import io.shulie.takin.web.data.mapper.mysql.SceneMapper;
import io.shulie.takin.web.data.model.mysql.SceneEntity;
import io.shulie.takin.web.data.param.activity.ActivityExistsQueryParam;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.data.param.linkmanage.SceneCreateParam;
import io.shulie.takin.web.data.param.linkmanage.SceneQueryParam;
import io.shulie.takin.web.data.param.linkmanage.SceneUpdateParam;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateParam;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateQuery;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateSaveParam;
import io.shulie.takin.web.data.param.scene.ScenePageQueryParam;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.filemanage.FileManageResult;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import io.shulie.takin.web.data.result.scriptmanage.ScriptManageDeployResult;
import io.shulie.takin.web.data.result.scriptmanage.ScriptManageResult;
import io.shulie.takin.web.diff.api.DiffFileApi;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liyuanba
 * @date 2021/10/27 10:36 上午
 */
@Slf4j
@Service
public class SceneServiceImpl implements SceneService {
    @Resource
    SceneMapper sceneMapper;
    @Resource
    private SceneDAO sceneDao;
    @Resource
    private DiffFileApi fileApi;
    @Resource
    private ActivityDAO activityDao;
    @Resource
    private SceneService sceneService;
    @Resource
    private SceneManageApi sceneManageApi;
    @Resource
    private ActivityService activityService;
    @Resource
    private ScriptManageDAO scriptManageDao;
    @Resource
    private SceneLinkRelateDAO sceneLinkRelateDao;
    @Resource
    private ScriptManageService scriptManageService;
    @Resource
    private FileManageDAO fileManageDAO;
    @Resource
    private SceneMixApi sceneMixApi;
    @Resource
    private SceneManageService sceneManageService;
    @Value("${file.upload.tmp.path:/tmp/takin/}")
    private String tmpFilePath;
    @Value("${allow.query.unMatched.businessFlow:false}")
    private Boolean allowQueryUnMatchedBusinessFlow;
    @Resource
    private SceneLinkRelateDAO sceneLinkRelateDAO;
    @Resource
    private ApplicationBusinessActivityService applicationBusinessActivityService;
    @Resource
    private ApplicationDAO applicationDAO;

    /**
     * 返回SceneNode匹配个数
     * 1、根据sceneId，查询原来的业务流程关联活动表数据
     *  - 如果entrance在表中存在，且xpath_md5一致，则记录保留的id
     *  - 如果entrance在表中存在，但xpath_md5不一致，记录新增（业务活动字段从记录中拿）
     *  - 如果entrance在表中不存在，执行2
     * 2、根据entrance，查询业务活动数据
     *  - 优先如果entrance一致，且type（非虚拟）=0，记录新增
     *  - 其次如果entrance一致，且type（虚拟）=1，记录新增
     * 3、执行删除 - 原表ids去掉保留ids，
     * 4、执行新增
     * 5、返回匹配个数=不动个数+新增个数
     * @param scriptNodeList
     * @param sceneId
     * @return
     */
    private Integer bindNodeLinkToBusinessActivity(List<ScriptNode> scriptNodeList, Long sceneId) {
        Integer matchNum = 0;
        List<ScriptNode> nodeList = getNodes(scriptNodeList);
        if (CollectionUtils.isEmpty(nodeList)) {
            sceneLinkRelateDao.deleteBySceneId(String.valueOf(sceneId));
            return matchNum;
        }
        List<ScriptNode> samperNodeList = nodeList.stream().filter(Objects::nonNull).filter(node -> NodeTypeEnum.SAMPLER == node.getType()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(samperNodeList)) {
            sceneLinkRelateDao.deleteBySceneId(String.valueOf(sceneId));
            return matchNum;
        }
        List<SceneLinkRelateSaveParam> insertList = new ArrayList<>();
        List<String> entranceMatchList = new ArrayList<>();
        List<Long> keepIds = new ArrayList<>();
        List<SceneLinkRelateResult> relateList = sceneLinkRelateDao.selectBySceneId(sceneId);
        if(CollectionUtils.isNotEmpty(relateList)) {
            //存在一样的entrance，但xpath不一样的情况
            Map<String, Map<String, SceneLinkRelateResult>> relateMap = new HashMap<>();
            for(SceneLinkRelateResult relate : relateList) {
                Map<String, SceneLinkRelateResult> md5Map = relateMap.get(relate.getEntrance());
                if(MapUtils.isEmpty(md5Map)) {
                    md5Map = new HashMap<>();
                    relateMap.put(relate.getEntrance(), md5Map);
                }
                md5Map.put(relate.getScriptXpathMd5(), relate);
            }

            for(ScriptNode scriptNode : samperNodeList) {
                if(relateMap.containsKey(scriptNode.getIdentification())) {
                    Map<String, SceneLinkRelateResult> md5Map = relateMap.get(scriptNode.getIdentification());
                    if(md5Map.containsKey(scriptNode.getXpathMd5())) {
                        //记录保留的id
                        keepIds.add(md5Map.get(scriptNode.getXpathMd5()).getId());
                        matchNum++;
                    } else {
                        SceneLinkRelateSaveParam relateSaveParam = new SceneLinkRelateSaveParam();
                        relateSaveParam.setSceneId(String.valueOf(sceneId));
                        relateSaveParam.setEntrance(scriptNode.getIdentification());
                        relateSaveParam.setScriptIdentification(scriptNode.getIdentification());
                        relateSaveParam.setScriptXpathMd5(scriptNode.getXpathMd5());
                        SceneLinkRelateResult relateResult = md5Map.values().stream().findAny().get();
                        relateSaveParam.setBusinessLinkId(relateResult.getBusinessLinkId());
                        relateSaveParam.setTechLinkId(relateResult.getTechLinkId());
                        relateSaveParam.setParentBusinessLinkId(relateResult.getParentBusinessLinkId());
                        insertList.add(relateSaveParam);
                    }
                    entranceMatchList.add(scriptNode.getIdentification());
                }
            }

            List<Long> ids = relateList.stream().map(SceneLinkRelateResult::getId).collect(Collectors.toList());
            ids.removeAll(keepIds);
            if(CollectionUtils.isNotEmpty(ids)) {
                sceneLinkRelateDao.deleteByIds(ids);
            }
        }
        List<ScriptNode> nodeNotMatchList = samperNodeList.stream().filter(data -> !entranceMatchList.contains(data.getIdentification())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(nodeNotMatchList)) {
            //继续匹配
            List<String> entranceList = nodeNotMatchList.stream().map(ScriptNode::getIdentification).distinct().collect(Collectors.toList());
            ActivityQueryParam param = new ActivityQueryParam();
            param.setEntranceList(entranceList);
            param.setCurrent(0);
            param.setPageSize(200);
            WebPluginUtils.fillQueryParam(param);
            PagingList<ActivityListResult> pagingList = activityDao.pageActivities(param);
            if(pagingList != null && CollectionUtils.isNotEmpty(pagingList.getList())) {
                List<ActivityListResult> activityList = pagingList.getList();
                Map<String, Map<Integer, ActivityListResult>> activityMap = new HashMap<>();
                for(ActivityListResult activity : activityList) {
                    Map<Integer, ActivityListResult> typeMap = activityMap.get(activity.getEntrace());
                    if(MapUtils.isEmpty(typeMap)) {
                        typeMap = new HashMap<>();
                        activityMap.put(activity.getEntrace(), typeMap);
                    }
                    typeMap.put(activity.getBusinessType(), activity);
                }
                for(ScriptNode scriptNode : nodeNotMatchList) {
                    if(activityMap.containsKey(scriptNode.getIdentification())) {
                        Map<Integer, ActivityListResult> typeMap = activityMap.get(scriptNode.getIdentification());
                        if(typeMap.containsKey(0)) {
                            insertList.add(buildSceneLinkRelateSaveParam(sceneId, scriptNode, typeMap.get(0)));
                        } else if(typeMap.containsKey(1)) {
                            insertList.add(buildSceneLinkRelateSaveParam(sceneId, scriptNode, typeMap.get(1)));
                        }
                    }
                }
            }
        }

        if(CollectionUtils.isNotEmpty(insertList)) {
            sceneLinkRelateDao.batchInsert(insertList);
            matchNum += insertList.size();
        }
        return matchNum;
    }

    private SceneLinkRelateSaveParam buildSceneLinkRelateSaveParam(Long sceneId, ScriptNode scriptNode, ActivityListResult activityResult) {
        SceneLinkRelateSaveParam relateSaveParam = new SceneLinkRelateSaveParam();
        relateSaveParam.setSceneId(String.valueOf(sceneId));
        relateSaveParam.setEntrance(scriptNode.getIdentification());
        relateSaveParam.setScriptIdentification(scriptNode.getIdentification());
        relateSaveParam.setScriptXpathMd5(scriptNode.getXpathMd5());
        relateSaveParam.setBusinessLinkId(String.valueOf(activityResult.getActivityId()));
        relateSaveParam.setTechLinkId(activityResult.getTechLinkId());
        relateSaveParam.setParentBusinessLinkId(activityResult.getParentTechLinkId());
        return relateSaveParam;
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
            fileManageCreateRequest.setId(null);
            fileManageCreateRequest.setScriptContent(null);
        }
        boolean encoded = false;
        ScriptAnalyzeRequest analyzeRequest = new ScriptAnalyzeRequest();
        analyzeRequest.setScriptFile(tmpFilePath + "/" + fileManageCreateRequest.getUploadId() + "/" + fileManageCreateRequest.getFileName());
        if (businessFlowParseRequest.getScriptFile().getId() != null) {
            //已存在文件
            FileManageResult fileManageResult = fileManageDAO.selectFileManageById(businessFlowParseRequest.getScriptFile().getId());
            if (fileManageResult == null) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "没有根据脚本id找到对应脚本！");
            }
            analyzeRequest.setScriptFile(fileManageResult.getUploadPath());
            encoded = FileEncoder.fileEncoded(analyzeRequest.getScriptFile());
        }
        //解析脚本
        List<ScriptNode> data = new ArrayList<>();
        String testPlanName = "";
        if (!encoded) {
            //解析脚本
            data = sceneManageApi.scriptAnalyze(analyzeRequest);
            List<ScriptNode> testPlan = JmxUtil.getScriptNodeByType(NodeTypeEnum.TEST_PLAN, data);
            if (CollectionUtils.isEmpty(testPlan)) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "脚本文件没有解析到测试计划！");
            }
            testPlanName = testPlan.get(0).getTestName();
        }
        String businessFlowName = null;
        if (businessFlowParseRequest.getId() == null) {
            SceneCreateParam createParam = saveBusinessFlow(businessFlowParseRequest.getSource(), testPlanName, data, fileManageCreateRequest, businessFlowParseRequest.getPluginList());
            businessFlowParseRequest.setId(createParam.getId());
            businessFlowName = createParam.getSceneName();
        } else {
            SceneResult sceneResult = updateBusinessFlow(businessFlowParseRequest.getId(), businessFlowParseRequest.getScriptFile(), null, data, businessFlowParseRequest.getPluginList());
            businessFlowName = sceneResult.getSceneName();
        }

        BusinessFlowDetailResponse result = new BusinessFlowDetailResponse();
        result.setId(businessFlowParseRequest.getId());
        result.setBusinessProcessName(businessFlowName);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public SceneCreateParam saveBusinessFlow(Integer source, String testName, List<ScriptNode> data, FileManageUpdateRequest fileManageCreateRequest,
                                             List<PluginConfigCreateRequest> pluginList) {
        SceneQueryParam sceneQueryParam = new SceneQueryParam();
        sceneQueryParam.setSceneName(testName);
        List<SceneResult> sceneResultList = sceneDao.selectListByName(sceneQueryParam);
        if (CollectionUtils.isNotEmpty(sceneResultList)) {
            testName = testName + "_" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
        }
        //保存业务流程
        SceneCreateParam sceneCreateParam = new SceneCreateParam();
        sceneCreateParam.setSceneName(testName);
        sceneCreateParam.setLinkRelateNum(0);
        List<ScriptNode> saveNode = new ArrayList<>(data);
        PtsParseJmxToObjectTools.removeSamplerNodeChildren(saveNode);
        sceneCreateParam.setScriptJmxNode(JsonHelper.bean2Json(saveNode));
        sceneCreateParam.setTotalNodeNum(JmxUtil.getNodeNumByType(NodeTypeEnum.SAMPLER, data));
        if (source != null) {
            sceneCreateParam.setType(source);
        } else {
            sceneCreateParam.setType(SceneTypeEnum.JMETER_UPLOAD_SCENE.getType());
        }
        WebPluginUtils.fillCloudUserData(sceneCreateParam);
        sceneDao.insert(sceneCreateParam);

        //新增脚本文件
        ScriptManageDeployCreateRequest createRequest = new ScriptManageDeployCreateRequest();
        //脚本文件名称去重
        String scriptName = sceneCreateParam.getSceneName();
        List<ScriptManageResult> scriptManageResults = scriptManageDao.selectScriptManageByName(sceneCreateParam.getSceneName());
        if (CollectionUtils.isNotEmpty(scriptManageResults)) {
            scriptName = sceneCreateParam.getSceneName() + "_" + DateUtil.formatDateTime(new Date());
        }
        createRequest.setFileManageCreateRequests(Collections.singletonList(LinkManageConvert.INSTANCE.ofFileManageCreateRequest(fileManageCreateRequest)));
        createRequest.setName(scriptName);
        createRequest.setType(ScriptTypeEnum.JMETER.getCode());
        createRequest.setMVersion(ScriptMVersionEnum.SCRIPT_M_1.getCode());
        createRequest.setRefType(ScriptManageConstant.BUSINESS_PROCESS_REF_TYPE);
        createRequest.setRefValue(sceneCreateParam.getId().toString());
        // 设置插件信息
        if (CollectionUtils.isNotEmpty(pluginList)) {
            pluginList = pluginList.stream().filter(Objects::nonNull).collect(Collectors.toList());
            createRequest.setPluginConfigCreateRequests(pluginList);
        }
        Long scriptManageId = scriptManageService.createScriptManage(createRequest);

        //更新业务流程
        SceneUpdateParam sceneUpdateParam = new SceneUpdateParam();
        sceneUpdateParam.setId(sceneCreateParam.getId());
        sceneUpdateParam.setScriptDeployId(scriptManageId);
        sceneDao.update(sceneUpdateParam);
        return sceneCreateParam;
    }

    @Override
    public BusinessFlowDetailResponse getBusinessFlowDetail(Long id) {
        BusinessFlowDetailResponse result = new BusinessFlowDetailResponse();
        SceneResult sceneResult = sceneDao.getSceneDetail(id);
        if (sceneResult == null) {
            return result;
        }

        List<ScriptNode> scriptNodes = JsonHelper.json2List(sceneResult.getScriptJmxNode(), ScriptNode.class);
        //将节点树处理成线程组在最外层的形式
        List<ScriptNode> scriptNodeByType = JmxUtil.getScriptNodeByType(NodeTypeEnum.THREAD_GROUP, scriptNodes);
        List<ScriptJmxNode> scriptJmxNodes = LinkManageConvert.INSTANCE.ofScriptNodeList(scriptNodeByType);
        FileManageResponse scriptFile = new FileManageResponse();
        ScriptManageDeployDetailResponse scriptManageDeployDetail = scriptManageService.getScriptManageDeployDetail(sceneResult.getScriptDeployId());
        if (scriptManageDeployDetail != null) {
            //脚本文件单独存储
            if (CollectionUtils.isNotEmpty(scriptManageDeployDetail.getFileManageResponseList())) {
                List<FileManageResponse> fileManageResponses = scriptManageDeployDetail.getFileManageResponseList().stream()
                        .filter(o -> FileTypeEnum.SCRIPT.getCode().equals(o.getFileType())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(fileManageResponses)) {
                    scriptFile = LinkManageConvert.INSTANCE.ofFileManageResponse(fileManageResponses.get(0));
                    scriptManageDeployDetail.getFileManageResponseList().remove(fileManageResponses.get(0));
                    //将附件数据放入数据文件之中
                    if (CollectionUtils.isNotEmpty(scriptManageDeployDetail.getAttachmentManageResponseList())) {
                        scriptManageDeployDetail.getFileManageResponseList().addAll(scriptManageDeployDetail.getAttachmentManageResponseList());
                    }
                }
            }
            result = LinkManageConvert.INSTANCE.ofBusinessFlowDetailResponse(scriptManageDeployDetail);
            result.setScriptFile(scriptFile);
            int fileManageNum = result.getFileManageResponseList() == null ? 0 : result.getFileManageResponseList().size();
            result.setFileNum(fileManageNum);
        }
        if (CollectionUtils.isNotEmpty(scriptJmxNodes)) {
            List<WebOptionEntity> webOptionEntities = scriptJmxNodes.stream().map(scriptJmxNode -> {
                WebOptionEntity webOptionEntity = new WebOptionEntity();
                webOptionEntity.setLabel(scriptJmxNode.getTestName());
                webOptionEntity.setValue(scriptJmxNode.getXpathMd5());
                return webOptionEntity;
            }).collect(Collectors.toList());
            result.setScriptJmxNodeList(webOptionEntities);
        }

        result.setThreadGroupNum(scriptNodeByType.size());
        toBusinessFlowDetailResponse(sceneResult, result);
        return result;
    }

    @Override
    public SceneResult getScene(Long id) {
        if (null == id) {
            return null;
        }
        return sceneDao.getSceneDetail(id);
    }

    @Override
    public BusinessFlowDetailResponse uploadDataFile(BusinessFlowDataFileRequest businessFlowDataFileRequest) {
        updateBusinessFlow(businessFlowDataFileRequest.getId(), null, businessFlowDataFileRequest, null, businessFlowDataFileRequest.getPluginList());
        BusinessFlowDetailResponse result = new BusinessFlowDetailResponse();
        result.setId(businessFlowDataFileRequest.getId());
        return result;
    }

    @Override
    public BusinessFlowThreadResponse getThreadGroupDetail(Long id, String xpathMd5) {
        BusinessFlowThreadResponse response = new BusinessFlowThreadResponse();
        SceneResult sceneResult = sceneDao.getSceneDetail(id);
        if (sceneResult == null) {
            return response;
        }
        List<ScriptNode> scriptNodes = JsonHelper.json2List(sceneResult.getScriptJmxNode(), ScriptNode.class);
        //将节点树处理成线程组在最外层的形式
        List<ScriptNode> scriptNodeByType = JmxUtil.getScriptNodeByType(NodeTypeEnum.THREAD_GROUP, scriptNodes);
        List<ScriptNode> scriptNodeList = scriptNodeByType.stream().filter(o -> o.getXpathMd5().equals(xpathMd5)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(scriptNodeList)) {
            return response;
        }
        List<ScriptNode> samplerScriptNodeList = JmxUtil.getScriptNodeByType(NodeTypeEnum.SAMPLER, scriptNodeList);
        List<String> xpathMd5List = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(samplerScriptNodeList)) {
            xpathMd5List = samplerScriptNodeList.stream().map(ScriptNode::getXpathMd5).collect(Collectors.toList());
        }

        List<ScriptJmxNode> scriptJmxNodes = LinkManageConvert.INSTANCE.ofScriptNodeList(scriptNodeList);

        SceneLinkRelateParam sceneLinkRelateParam = new SceneLinkRelateParam();
        sceneLinkRelateParam.setSceneIds(Collections.singletonList(id.toString()));
        List<SceneLinkRelateResult> sceneLinkRelateList = sceneLinkRelateDao.getList(sceneLinkRelateParam);
        if (CollectionUtils.isNotEmpty(sceneLinkRelateList) && CollectionUtils.isNotEmpty(xpathMd5List)) {
            List<String> finalXpathMd5List = xpathMd5List;
            sceneLinkRelateList = sceneLinkRelateList.stream().filter(o -> finalXpathMd5List.contains(o.getScriptXpathMd5()))
                    .collect(Collectors.toList());
        }
        dealScriptJmxNodes(sceneLinkRelateList, scriptJmxNodes);
        response.setThreadScriptJmxNodes(scriptJmxNodes);
        response.setLinkRelateNum(CollectionUtils.isEmpty(sceneLinkRelateList) ? 0 : sceneLinkRelateList.size());
        int nodeNumByType = JmxUtil.getNodeNumByType(NodeTypeEnum.SAMPLER, scriptNodeList);
        response.setTotalNodeNum(nodeNumByType);
        return response;
    }

    /**
     * 脚本节点和业务活动关联关系
     *
     * @param sceneId 业务流程id
     * @return 节点也业务流程关联关系
     */
    @Override
    public List<SceneLinkRelateResult> getSceneLinkRelates(Long sceneId) {
        if (null == sceneId) {
            return null;
        }
        SceneLinkRelateParam sceneLinkRelateParam = new SceneLinkRelateParam();
        sceneLinkRelateParam.setSceneIds(Collections.singletonList(sceneId.toString()));
        return sceneLinkRelateDao.getList(sceneLinkRelateParam);
    }

    @Override
    public BusinessFlowMatchResponse autoMatchActivity(Long id) {
        BusinessFlowMatchResponse result = new BusinessFlowMatchResponse();
        SceneResult sceneResult = sceneDao.getSceneDetail(id);
        if (sceneResult == null) {
            return result;
        }
        result.setFinishDate(new Date());
        result.setId(id);
        result.setBusinessProcessName(sceneResult.getSceneName());
        List<ScriptNode> scriptNodes = JsonHelper.json2List(sceneResult.getScriptJmxNode(), ScriptNode.class);
        int nodeNumByType = JmxUtil.getNodeNumByType(NodeTypeEnum.SAMPLER, scriptNodes);
        int matchNum = this.bindNodeLinkToBusinessActivity(scriptNodes, id);
        result.setMatchNum(matchNum);
        result.setUnMatchNum(nodeNumByType - matchNum);
        //更新匹配数量
        SceneUpdateParam updateParam = new SceneUpdateParam();
        updateParam.setId(id);
        updateParam.setLinkRelateNum(matchNum);
        sceneDao.update(updateParam);
        //匹配数量符合，修改压测场景 注释掉，人工去修改压测场景（跨事务，api接口由1个变成2个时，导致保存异常）
//        if (matchNum == nodeNumByType) {
//            syncSceneManege(sceneResult);
//        }
        return result;
    }

    private void syncSceneManege(SceneResult sceneResult) {
        SynchronizeRequest synchronizeRequest = new SynchronizeRequest();
        synchronizeRequest.setScriptId(sceneResult.getScriptDeployId());
        synchronizeRequest.setAnalysisResult(JsonHelper.json2List(sceneResult.getScriptJmxNode(), ScriptNode.class));

        SceneLinkRelateParam sceneLinkRelateParam = new SceneLinkRelateParam();
        sceneLinkRelateParam.setSceneIds(Collections.singletonList(sceneResult.getId().toString()));
        List<SceneLinkRelateResult> sceneLinkRelateList = sceneLinkRelateDao.getList(sceneLinkRelateParam);
        if (CollectionUtils.isEmpty(sceneLinkRelateList)) {
            return;
        }
        Map<String, SynchronizeRequest.BusinessActivityInfoData> businessActivityInfo = sceneLinkRelateList.stream()
                .filter(o -> o.getBusinessLinkId() != null)
                .collect(Collectors.toMap(SceneLinkRelateResult::getScriptXpathMd5, o -> {
                    long activityId = NumberUtils.toLong(o.getBusinessLinkId());
                    List<String> applicationIdList = sceneManageService.getAppIdsByBusinessActivityId(activityId);
                    SynchronizeRequest.BusinessActivityInfoData activityInfoData = new SynchronizeRequest.BusinessActivityInfoData();
                    activityInfoData.setId(activityId);
                    activityInfoData.setApplicationIdList(applicationIdList);
                    return activityInfoData;
                }));
        synchronizeRequest.setBusinessActivityInfo(businessActivityInfo);
        WebPluginUtils.fillCloudUserData(synchronizeRequest);
        sceneMixApi.synchronize(synchronizeRequest);
    }

    @Override
    public void matchActivity(SceneLinkRelateRequest sceneLinkRelateRequest) {
        SceneResult sceneDetail = sceneDao.getSceneDetail(sceneLinkRelateRequest.getBusinessFlowId());
        if (sceneDetail == null) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_UPDATE_ERROR, "匹配业务活动，未找到对应业务流程！");
        }
        if (BusinessTypeEnum.NORMAL_BUSINESS.getType().equals(sceneLinkRelateRequest.getBusinessType())) {
            //普通业务活动
            if (sceneLinkRelateRequest.getBusinessActivityId() == null) {
                //业务活动id为空，新增业务活动
                ActivityCreateRequest request = LinkManageConvert.INSTANCE.ofActivityCreateRequest(sceneLinkRelateRequest);
                request.setType(EntranceTypeEnum.getEnumByType(sceneLinkRelateRequest.getSamplerType().getType()));
                ActivityUtil.EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertEntrance(sceneLinkRelateRequest.getEntrance());
                request.setServiceName(entranceJoinEntity.getServiceName());
                request.setMethod(entranceJoinEntity.getMethodName());
                request.setRpcType(entranceJoinEntity.getRpcType());
                Long activity = activityService.createActivity(request);
                sceneLinkRelateRequest.setBusinessActivityId(activity);
            }
        } else if (BusinessTypeEnum.VIRTUAL_BUSINESS.getType().equals(sceneLinkRelateRequest.getBusinessType())) {
            sceneLinkRelateRequest.setEntrance(sceneLinkRelateRequest.getIdentification());
            sceneLinkRelateRequest.setActivityName(sceneLinkRelateRequest.getTestName());
            //查询url是否能匹配
            ActivityQueryParam queryParam = new ActivityQueryParam();
            queryParam.setBusinessType(sceneLinkRelateRequest.getBusinessType());
            queryParam.setEntrance(sceneLinkRelateRequest.getEntrance());
            List<ActivityListResult> activityList = activityDao.getActivityList(queryParam);
            if (CollectionUtils.isNotEmpty(activityList)) {
                ActivityListResult activityListResult = activityList.get(0);
                sceneLinkRelateRequest.setBusinessActivityId(activityListResult.getActivityId());
            } else {
                VirtualActivityCreateRequest createRequest = LinkManageConvert.INSTANCE.ofVirtualActivityCreateRequest(sceneLinkRelateRequest);
                //是否存在名称重复
                ActivityExistsQueryParam queryNameParam = new ActivityExistsQueryParam();
                queryNameParam.setActivityName(createRequest.getActivityName());
                List<String> exists = activityDao.exists(queryNameParam);
                if (CollectionUtils.isNotEmpty(exists)) {
                    //如果名称长度过长，截取长度以匹配表字段长度
                    if (187 < createRequest.getActivityName().length()) {
                        createRequest.setActivityName(createRequest.getActivityName().substring(0, 187));
                    }
                    //如果业务活动名称重复，修改名称
                    createRequest.setActivityName(createRequest.getActivityName() + "_" + System.currentTimeMillis());
                }
                createRequest.setType(EntranceTypeEnum.getEnumByType(sceneLinkRelateRequest.getSamplerType().getType()));
                ActivityUtil.EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertVirtualEntrance(sceneLinkRelateRequest.getEntrance());
                createRequest.setVirtualEntrance(entranceJoinEntity.getVirtualEntrance());
                createRequest.setMethodName(entranceJoinEntity.getMethodName());
                Long virtualActivity = activityService.createVirtualActivity(createRequest);
                sceneLinkRelateRequest.setBusinessActivityId(virtualActivity);
            }
        } else {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_UPDATE_ERROR, "不是已知的业务活动类型！");
        }
        SceneLinkRelateQuery sceneLinkRelateQuery = new SceneLinkRelateQuery();
        sceneLinkRelateQuery.setSceneId(sceneLinkRelateRequest.getBusinessFlowId());
        sceneLinkRelateQuery.setXpathMd5(sceneLinkRelateRequest.getXpathMd5());
        SceneLinkRelateSaveParam saveParam = LinkManageConvert.INSTANCE.ofSceneLinkRelateRequest(sceneLinkRelateRequest);

        List<SceneLinkRelateResult> sceneLinkRelateResults = sceneLinkRelateDao.query(sceneLinkRelateQuery);
        if (CollectionUtils.isNotEmpty(sceneLinkRelateResults)) {
            //一个业务流程中只会有一个XpathMd5
            sceneLinkRelateDao.deleteByIds(Collections.singletonList(sceneLinkRelateResults.get(0).getId()));
        }
        saveParam.setEntrance(sceneLinkRelateRequest.getEntrance());
        saveParam.setSceneId(sceneLinkRelateRequest.getBusinessFlowId().toString());
        saveParam.setBusinessLinkId(sceneLinkRelateRequest.getBusinessActivityId().toString());
        saveParam.setScriptXpathMd5(sceneLinkRelateRequest.getXpathMd5());
        saveParam.setScriptIdentification(sceneLinkRelateRequest.getIdentification());
        sceneLinkRelateDao.batchInsert(Collections.singletonList(saveParam));

        int linkRelateNum = sceneDetail.getLinkRelateNum();
        //sceneLinkRelateResults为空，新增的匹配，更新匹配数量
        if (CollectionUtils.isEmpty(sceneLinkRelateResults)) {
            SceneUpdateParam updateParam = new SceneUpdateParam();
            updateParam.setId(sceneLinkRelateRequest.getBusinessFlowId());
            linkRelateNum = sceneDetail.getLinkRelateNum() + 1;
            updateParam.setLinkRelateNum(linkRelateNum);
            sceneDao.update(updateParam);
        }

        //匹配数量符合，修改压测场景 注释掉，人工去修改压测场景（跨事务，api接口由1个变成2个时，导致保存异常）
//        if (sceneDetail.getTotalNodeNum() == linkRelateNum) {
//            syncSceneManege(sceneDetail);
//        }
    }

    @Override
    public PagingList<BusinessFlowListResponse> getBusinessFlowList(BusinessFlowPageQueryRequest queryRequest) {
        ScenePageQueryParam queryParam = new ScenePageQueryParam();
        final String flowName = queryRequest.getBusinessFlowName();
        queryParam.setSceneName(flowName == null ? null : flowName.replace("%", "\\%").replace("-", "\\-").replace("_", "\\_"));
        queryParam.setCurrent(queryRequest.getCurrent());
        queryParam.setPageSize(queryRequest.getPageSize());
        WebPluginUtils.fillQueryParam(queryParam);
        queryParam.setIgnoreType(SceneTypeEnum.PERFORMANCE_AUTO_SCENE.getType());

        PagingList<SceneResult> pageList = sceneDao.selectPageList(queryParam);
        List<BusinessFlowListResponse> responses = LinkManageConvert.INSTANCE.ofSceneResultList(pageList.getList());
        List<Long> userIds = CommonUtil.getList(responses, BusinessFlowListResponse::getUserId);
        //用户信息Map key:userId  value:user对象
        Map<Long, UserExt> userMap = WebPluginUtils.getUserMapByIds(userIds);
        if (CollectionUtils.isNotEmpty(responses)) {
            responses.forEach(r -> {
                UserExt user = userMap.get(r.getUserId());
                if (user != null) {
                    r.setUserName(user.getName());
                }
            });
        }
        return PagingList.of(responses, pageList.getTotal());
    }

    /**
     * 获取业务流程列表 - 创建压测场景用
     *
     * @return 业务流程列表
     * <p>只有主键和名称</p>
     */
    @Override
    public List<SceneEntity> businessActivityFlowList() {
        ScenePageQueryParam queryParam = new ScenePageQueryParam();
        WebPluginUtils.fillQueryParam(queryParam);
        LambdaQueryWrapper<SceneEntity> wrapper = Wrappers.lambdaQuery();
        // 只返回主键和名称
        wrapper.select(SceneEntity::getId, SceneEntity::getSceneName);
        // 只返回Jmeter+PTS上传的
        wrapper.in(SceneEntity::getType, SceneTypeEnum.JMETER_UPLOAD_SCENE.getType(), SceneTypeEnum.PTS_UPLOAD_SCENE.getType());
        // 未逻辑删除
        wrapper.eq(SceneEntity::getIsDeleted, false);
        if(!allowQueryUnMatchedBusinessFlow) {
            // 节点匹配完成
            wrapper.apply("total_node_num = link_relate_num");
        }
        // 倒序排列
        wrapper.orderByDesc(SceneEntity::getId);
        if (CollectionUtils.isNotEmpty(queryParam.getUserIdList())) {
            wrapper.in(SceneEntity::getUserId, queryParam.getUserIdList());
        }
        return sceneMapper.selectList(wrapper);
    }

    /**
     * 获取业务活动详情
     * <p>主要返回脚本解析结果</p>
     *
     * @param id 业务活动主键
     * @return 业务流程详情
     */
    @Override
    public SceneEntity businessActivityFlowDetail(long id) {
        SceneEntity scene = sceneMapper.selectById(id);
        if (scene == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "未获取到业务流程");
        }
        return scene;
    }

    @Override
    public void updateBusinessFlow(BusinessFlowUpdateRequest businessFlowUpdateRequest) {
        SceneResult sceneResult = sceneDao.getSceneDetail(businessFlowUpdateRequest.getId());
        if (sceneResult == null) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, "没有找到对应的业务流程！");
        }
        SceneUpdateParam sceneUpdateParam = new SceneUpdateParam();
        if (StringUtils.isNotBlank(businessFlowUpdateRequest.getSceneName())) {
            sceneUpdateParam.setSceneName(businessFlowUpdateRequest.getSceneName());
            SceneQueryParam sceneQueryParam = new SceneQueryParam();
            sceneQueryParam.setSceneName(businessFlowUpdateRequest.getSceneName());
            List<SceneResult> sceneResultList = sceneDao.selectListByName(sceneQueryParam);
            if (CollectionUtils.isNotEmpty(sceneResultList)) {
                List<Long> collect = sceneResultList.stream().filter(o -> o.getSceneName().equals(businessFlowUpdateRequest.getSceneName()))
                        .map(SceneResult::getId).filter(o -> !o.equals(businessFlowUpdateRequest.getId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(collect)) {
                    throw new TakinWebException(TakinWebExceptionEnum.LINK_UPDATE_ERROR, "修改业务流程，业务流程名称已存在！");
                }
            }

        }

        //更新业务流程
        sceneUpdateParam.setId(businessFlowUpdateRequest.getId());
        sceneUpdateParam.setSceneLevel(businessFlowUpdateRequest.getSceneLevel());
        sceneUpdateParam.setIsCore(businessFlowUpdateRequest.getIsCore());
        sceneDao.update(sceneUpdateParam);
    }

    @Transactional(rollbackFor = Exception.class)
    public SceneResult updateBusinessFlow(Long businessFlowId, FileManageUpdateRequest scriptFile,
                                          BusinessFlowDataFileRequest businessFlowDataFileRequest, List<ScriptNode> data,
                                          List<PluginConfigCreateRequest> pluginList) {
        SceneResult sceneResult = sceneDao.getSceneDetail(businessFlowId);
        if (sceneResult == null) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, "没有找到对应的业务流程！");
        }
        //取之前脚本中关联的其他文件
        Long oldScriptDeployId = sceneResult.getScriptDeployId();
        ScriptManageDeployResult scriptManageDeployResult = scriptManageDao.selectScriptManageDeployById(oldScriptDeployId);
        if (scriptManageDeployResult == null) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, "没有找到业务流程对应的脚本！");
        }

        ScriptManageDeployDetailResponse result = scriptManageService.getScriptManageDeployDetail(oldScriptDeployId);
        List<FileManageResponse> fileManageResponseList = result.getFileManageResponseList();
        ScriptManageDeployUpdateRequest updateRequest = new ScriptManageDeployUpdateRequest();
        updateRequest.setId(oldScriptDeployId);
        updateRequest.setMVersion(ScriptMVersionEnum.SCRIPT_M_1.getCode());
        updateRequest.setType(ScriptTypeEnum.JMETER.getCode());
        updateRequest.setRefType(ScriptManageConstant.BUSINESS_PROCESS_REF_TYPE);
        updateRequest.setRefValue(businessFlowId.toString());
        updateRequest.setName(scriptManageDeployResult.getName());

        if (scriptFile == null) {
            List<FileManageResponse> dataFileManageResponseList = fileManageResponseList.stream().filter(o ->
                    FileTypeEnum.SCRIPT.getCode().equals(o.getFileType())).collect(Collectors.toList());
            //更新脚本
            if (CollectionUtils.isEmpty(businessFlowDataFileRequest.getFileManageUpdateRequests())) {
                businessFlowDataFileRequest.setFileManageUpdateRequests(new ArrayList<>());
            }
            businessFlowDataFileRequest.getFileManageUpdateRequests().addAll(LinkManageConvert.INSTANCE
                    .ofFileManageResponseList(dataFileManageResponseList));
            //脚本后续会把文件和附件放到一起，这里就不分出来了
            updateRequest.setFileManageUpdateRequests(businessFlowDataFileRequest.getFileManageUpdateRequests());
            if (CollectionUtils.isNotEmpty(result.getPluginConfigDetailResponseList())) {
                List<PluginConfigCreateRequest> pluginConfigCreateRequests = result.getPluginConfigDetailResponseList()
                        .stream().map(pluginConfigDetailResponse -> {
                            PluginConfigCreateRequest pluginConfigCreateRequest = new PluginConfigCreateRequest();
                            pluginConfigCreateRequest.setId(pluginConfigDetailResponse.getId());
                            pluginConfigCreateRequest.setName(pluginConfigDetailResponse.getName());
                            pluginConfigCreateRequest.setVersion(pluginConfigDetailResponse.getVersion());
                            pluginConfigCreateRequest.setType(pluginConfigDetailResponse.getType());
                            return pluginConfigCreateRequest;
                        }).collect(Collectors.toList());
                updateRequest.setPluginList(pluginConfigCreateRequests);
            }
        } else {
            List<FileManageResponse> dataFileManageResponseList = fileManageResponseList.stream().filter(o ->
                    !FileTypeEnum.SCRIPT.getCode().equals(o.getFileType())).collect(Collectors.toList());
            List<FileManageUpdateRequest> updateFileManageRequests = new ArrayList<>();
            updateFileManageRequests.add(scriptFile);
            updateFileManageRequests.addAll(LinkManageConvert.INSTANCE.ofFileManageResponseList(dataFileManageResponseList));
            // 将最新的附件信息处理合并到一起
            updateFileManageRequests.addAll(LinkManageConvert.INSTANCE.ofFileManageResponseList(result.getAttachmentManageResponseList()));
            updateRequest.setFileManageUpdateRequests(updateFileManageRequests);
        }
        // 设置插件信息
        if (CollectionUtils.isNotEmpty(pluginList)) {
            pluginList = pluginList.stream().filter(Objects::nonNull).collect(Collectors.toList());
            updateRequest.setPluginList(pluginList);
        }
        //更新脚本
        Long scriptDeployId = scriptManageService.updateScriptManage(updateRequest);
        SceneUpdateParam sceneUpdateParam = new SceneUpdateParam();
        if (CollectionUtils.isNotEmpty(data)) {
            List<ScriptNode> saveNode = new ArrayList<>(data);
            PtsParseJmxToObjectTools.removeSamplerNodeChildren(saveNode);
            sceneUpdateParam.setScriptJmxNode(JsonHelper.bean2Json(saveNode));
            List<ScriptNode> samplerNodes = JmxUtil.getScriptNodeByType(NodeTypeEnum.SAMPLER, data);
            sceneUpdateParam.setTotalNodeNum(null == samplerNodes ? 0 : samplerNodes.size());
        }
        //更新业务流程
        sceneUpdateParam.setScriptDeployId(scriptDeployId);
        sceneUpdateParam.setId(businessFlowId);
        sceneDao.update(sceneUpdateParam);
        //脚本节点有改动，重新自动匹配
        if (CollectionUtils.isNotEmpty(data)) {
            //自动匹配
            autoMatchActivity(businessFlowId);
        }
        return sceneResult;
    }

    private void toBusinessFlowDetailResponse(SceneResult sceneResult, BusinessFlowDetailResponse result) {
        result.setSceneLevel(sceneResult.getSceneLevel());
        result.setIsCore(sceneResult.getIsCore());
        result.setBusinessProcessName(sceneResult.getSceneName());
        result.setId(sceneResult.getId());
        result.setScriptDeployId(sceneResult.getScriptDeployId());
        result.setTotalNodeNum(sceneResult.getTotalNodeNum());
        result.setLinkRelateNum(sceneResult.getLinkRelateNum());
        result.setScriptType(sceneResult.getType());
    }

    private void dealScriptJmxNodes(List<SceneLinkRelateResult> sceneLinkRelateResults, List<ScriptJmxNode> scriptJmxNodes) {
        Map<String, String> xpathMd5Map = new HashMap<>();
        Map<String, ActivityListResult> collect = new HashMap<>();
        if (CollectionUtils.isNotEmpty(sceneLinkRelateResults)) {
            xpathMd5Map = sceneLinkRelateResults.stream().filter(Objects::nonNull)
                    .filter(o -> StringUtils.isNotBlank(o.getScriptXpathMd5()))
                    .collect(Collectors.toMap(SceneLinkRelateResult::getScriptXpathMd5, SceneLinkRelateResult::getBusinessLinkId));
            List<Long> businessLinkIds = sceneLinkRelateResults.stream().filter(Objects::nonNull)
                    .map(o -> Long.parseLong(o.getBusinessLinkId()))
                    .distinct()
                    .collect(Collectors.toList());
            ActivityQueryParam activityQueryParam = new ActivityQueryParam();
            activityQueryParam.setActivityIds(businessLinkIds);
            List<ActivityListResult> activityList = activityDao.getActivityList(activityQueryParam);
            if (CollectionUtils.isNotEmpty(activityList)) {
                collect = activityList.stream().collect(Collectors.toMap(o -> o.getActivityId().toString(), t -> t));
            }
        }
        dealScriptJmxNodes(scriptJmxNodes, xpathMd5Map, collect);
    }

    /**
     * 填充处理业务活动信息
     *
     * @param scriptJmxNodes jmx脚本解析结果
     * @param xpathMd5Map    节点路径MD5
     * @param activityMap    业务活动map
     */
    private void dealScriptJmxNodes(List<ScriptJmxNode> scriptJmxNodes, Map<String, String> xpathMd5Map, Map<String, ActivityListResult> activityMap) {
        if (CollectionUtils.isNotEmpty(scriptJmxNodes)) {
            for (ScriptJmxNode scriptJmxNode : scriptJmxNodes) {
                //默认不匹配
                scriptJmxNode.setStatus(0);
                // 支持beanshell,默认匹配
                if (scriptJmxNode.getName().equals("BeanShellSampler")) {
                    scriptJmxNode.setEntrace("|beanshell");
                    scriptJmxNode.setRequestPath("|beanshell");
                    scriptJmxNode.setIdentification("takin|beanshell");
                    scriptJmxNode.setBusinessType(BusinessTypeEnum.VIRTUAL_BUSINESS.getType());
                }
                if (xpathMd5Map.get(scriptJmxNode.getXpathMd5()) != null) {
                    ActivityListResult activityListResult = activityMap.get(xpathMd5Map.get(scriptJmxNode.getXpathMd5()));
                    if (activityListResult != null) {
                        scriptJmxNode.setBusinessApplicationName(activityListResult.getApplicationName());
                        ActivityUtil.EntranceJoinEntity entranceJoinEntity;
                        if (BusinessTypeEnum.VIRTUAL_BUSINESS.getType().equals(activityListResult.getBusinessType())) {
                            entranceJoinEntity = ActivityUtil.covertVirtualEntrance(activityListResult.getEntrace());
                        } else {
                            entranceJoinEntity = ActivityUtil.covertEntrance(activityListResult.getEntrace());
                        }
                        scriptJmxNode.setServiceName(entranceJoinEntity.getServiceName());
                        scriptJmxNode.setMethod(entranceJoinEntity.getMethodName());
                        scriptJmxNode.setRpcType(entranceJoinEntity.getRpcType());
                        scriptJmxNode.setIsChange(activityListResult.getIsChange());
                        scriptJmxNode.setIsCore(activityListResult.getIsCore());
                        scriptJmxNode.setBusinessActivityName(activityListResult.getActivityName());
                        scriptJmxNode.setBusinessDomain(activityListResult.getBusinessDomain());
                        scriptJmxNode.setActivityLevel(activityListResult.getActivityLevel());
                        scriptJmxNode.setBusinessType(activityListResult.getBusinessType());
                        scriptJmxNode.setBindBusinessId(activityListResult.getBindBusinessId());
                        scriptJmxNode.setTechLinkId(activityListResult.getTechLinkId());
                        scriptJmxNode.setEntrace(activityListResult.getEntrace());
                        scriptJmxNode.setEntracePath(StringUtils.isNotBlank(entranceJoinEntity.getMethodName()) ?
                                entranceJoinEntity.getMethodName() + "|" + entranceJoinEntity.getServiceName() : entranceJoinEntity.getServiceName());
                        scriptJmxNode.setStatus(1);
                    }
                }
                if (CollectionUtils.isNotEmpty(scriptJmxNode.getChildren())) {
                    dealScriptJmxNodes(scriptJmxNode.getChildren(), xpathMd5Map, activityMap);
                }
            }
        }
    }

    private void dealScriptNodeMd5(List<ScriptNode> testPlan) {
        for(ScriptNode node : testPlan) {
            if("JavaSampler".equals(node.getName())) {
                node.setMd5(node.getTestName());
                node.setXpathMd5(node.getTestName());
            }
            List<ScriptNode> children = node.getChildren();
            if(CollectionUtil.isNotEmpty(children)) {
                dealScriptNodeMd5(children);
            }
        }
    }

    @Override
    public List<ApplicationDetailResult> getAppsByFlowId(Long flowId) {
        // 根据业务流程ids获取业务活动ids
        List<Long> activityIds = sceneLinkRelateDAO.listBusinessLinkIdsByBusinessFlowIds(Arrays.asList(flowId));
        // 根据业务活动ids获取应用名称
        Set<String> applicationNames = activityIds.stream().map(businessActivityId -> {
            List<String> businessApplicationNames = applicationBusinessActivityService.processAppNameByBusinessActiveId(businessActivityId);
            return businessApplicationNames;
        }).filter(CollectionUtil::isNotEmpty).flatMap(Collection::stream).collect(Collectors.toSet());

        if (applicationNames.isEmpty()) {
            return null;
        }
        // 根据应用名称, 用户id, 获得应用列表
        List<ApplicationDetailResult> applicationPage = applicationDAO.getApplicationList(new ArrayList<>(applicationNames));
        return applicationPage;
    }

    @Override
    public boolean existsScene(Long tenantId, String envCode) {
        return sceneDao.existsScene(tenantId, envCode);
    }

    @Override
    public void update(SceneUpdateParam param) {
        sceneDao.update(param);
    }
}
