package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.util.HttpSupport;
import com.pamirs.takin.common.util.ResponseWrapper;
import com.pamirs.takin.common.util.parse.UrlUtil;
import com.pamirs.takin.entity.domain.dto.linkmanage.ScriptJmxNode;
import io.shulie.takin.adapter.api.model.request.file.UploadRequest;
import io.shulie.takin.adapter.api.model.response.file.UploadResponse;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.enums.SamplerTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityResultQueryRequest;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.ContentTypeVO;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.SceneLinkRelateRequest;
import io.shulie.takin.web.biz.pojo.request.scene.NewSceneRequest;
import io.shulie.takin.web.biz.pojo.request.scene.SceneDetailResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityListResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowThreadResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigSceneRelateShipEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceParamEntity;
import io.shulie.takin.web.data.model.mysql.SceneEntity;
import io.shulie.takin.web.data.result.filemanage.FileManageResult;
import jdk.nashorn.internal.runtime.regexp.joni.ast.StringNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @Author: vernon
 * @Date: 2022/5/20 10:37
 * @Description:
 */
@Slf4j
@Component
public class PerformancePressureServiceImpl extends AbstractPerformancePressureService {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private PerformanceDebugUtil performanceDebugUtil;

    @Override
    public ResponseResult<Long> add(PerformanceConfigCreateInput request) throws Throwable {
        doBefore(request);
        return super.add(request);
    }

    @Override
    public Long querySceneId(Long apiId) {
        return super.querySceneId(apiId);
    }


    /**
     * ??????????????????
     *
     * @return
     */
    static String TEMP_DICTIONARY = "./temp";

    static {
        File dictionary = new File(TEMP_DICTIONARY);
        if (!dictionary.exists()) {
            dictionary.mkdir();
        }
        dictionary.deleteOnExit();

    }

    public List<UploadResponse> upload(Long id, String name) throws IOException {
        String script = scriptGenerator(id);
        String fileName = name + ".jmx";
        File tempFile = new File(TEMP_DICTIONARY + File.separator + fileName);
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }
        try {
            FileWriter writer = new FileWriter(tempFile.getAbsoluteFile());
            writer.write(script);
            writer.close();
            List files = Lists.newArrayList();

            UploadRequest uploadRequest = new UploadRequest();
            uploadRequest.setFileList(files);
            MultipartFile multipartFile = new MockMultipartFile(tempFile.getName(), tempFile.getName(), "application/json",
                    new FileInputStream(tempFile));
            files.add(multipartFile);
            List<UploadResponse> uploadResponse = cloudFileApi.upload(uploadRequest);
            return uploadResponse;
        } catch (Throwable e) {
            log.error("??????????????????,{}", ExceptionUtils.getStackTrace(e));
        } finally {
            tempFile.delete();
        }
        return null;
    }


    /**
     * ??????????????????
     */
    public BusinessFlowDetailResponse bizFlowCreator(List<UploadResponse> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("?????????????????????takin-cloud??????????????????.");
        }
        UploadResponse source = list.get(0);
        BusinessFlowParseRequest request = new BusinessFlowParseRequest();

        FileManageUpdateRequest fileManageUpdateRequest = new FileManageUpdateRequest();
        fileManageUpdateRequest.setDownloadUrl(source.getDownloadUrl());
        fileManageUpdateRequest.setFileName(source.getFileName());
        fileManageUpdateRequest.setFileType(source.getFileType());
        fileManageUpdateRequest.setIsDeleted(source.getIsDeleted());
        fileManageUpdateRequest.setIsSplit(source.getIsSplit());
        fileManageUpdateRequest.setUploadId(source.getUploadId());

        request.setPluginList(Collections.emptyList());
        request.setScriptFile(fileManageUpdateRequest);

        return sceneService.parseScriptAndSave(request);
    }

    /**
     * ????????????????????????
     *
     * @param id
     * @return
     */
    public void activityAutoMatcher(Long id) {
        sceneService.autoMatchActivity(id);
    }

    /**
     * * ????????????????????????
     * * ????????????????????????
     * * ??????????????????
     * * ??????????????????
     * * ?????????????????????id
     */
    public void doBefore(PerformanceConfigCreateInput input) throws IOException {
        //??????jmx??????????????????
        List<UploadResponse> uploadResult = upload(input.getId(), input.getName());
        //??????????????????
        BusinessFlowDetailResponse flowInfo = bizFlowCreator(uploadResult);
        //??????????????????
        activityAutoMatcher(flowInfo.getId());
        //??????????????????????????? businessType?????????1(????????????????????????????????????)
        // xpathMd5????????????????????????threadgroup???xpathmd5 ??????????????????????????????????????????????????????
        //??????xpathMd5 ?????????????????????????????????????????????????????????????????????????????????
        BusinessFlowDetailResponse dto = sceneService.getBusinessFlowDetail(flowInfo.getId());
        //????????????xpathmd5
        String rootXpathMd5 = dto.getScriptJmxNodeList().get(0).getValue();

        BusinessFlowThreadResponse response = sceneService.getThreadGroupDetail(flowInfo.getId(), rootXpathMd5);
        ScriptJmxNode scriptJmxNode = response.getThreadScriptJmxNodes().get(0).getChildren().get(0);

        // ???????????????????????????,??????????????????
        if (StringUtil.isNotBlank(input.getEntranceAppName())) {
            ActivityResultQueryRequest activityRequest = new ActivityResultQueryRequest();
            // appName|path
            String entranceAppName = input.getEntranceAppName();
            String appName = entranceAppName.substring(0, input.getEntranceAppName().indexOf("|"));
            String entrancePath = entranceAppName.replace(appName, input.getHttpMethod());
            activityRequest.setApplicationName(appName);
            activityRequest.setEntrancePath(entrancePath + "|0");
            List<ActivityListResponse> list = activityService.queryNormalActivities(activityRequest);
            Long businessActivityId = null;
            if (!CollectionUtils.isEmpty(list)) {
                businessActivityId = list.stream().findFirst().orElse(new ActivityListResponse()).getActivityId();
            }
            SceneLinkRelateRequest sceneLinkRelateRequest = new SceneLinkRelateRequest();
            BeanUtils.copyProperties(scriptJmxNode, sceneLinkRelateRequest);
            sceneLinkRelateRequest.setBusinessType(BusinessTypeEnum.NORMAL_BUSINESS.getType());
            sceneLinkRelateRequest.setBusinessFlowId(flowInfo.getId());
            sceneLinkRelateRequest.setBusinessActivityId(businessActivityId);
            sceneLinkRelateRequest.setSamplerType(SamplerTypeEnum.HTTP);
            sceneLinkRelateRequest.setApplicationName(appName);
            // ????????????RpcType
            sceneLinkRelateRequest.setEntrance(entrancePath + "|0");
            sceneLinkRelateRequest.setActivityName(input.getName());

            sceneService.matchActivity(sceneLinkRelateRequest);
        } else {
            SceneLinkRelateRequest sceneLinkRelateRequest = new SceneLinkRelateRequest();
            BeanUtils.copyProperties(scriptJmxNode, sceneLinkRelateRequest);
            sceneLinkRelateRequest.setBusinessType(BusinessTypeEnum.VIRTUAL_BUSINESS.getType());
            sceneLinkRelateRequest.setBusinessFlowId(flowInfo.getId());
            // ??????ServerName
            String urlPath = UrlUtil.parseUrl(input.getRequestUrl());
            String entrance = input.getHttpMethod() + "|" + urlPath + "|0";
            sceneLinkRelateRequest.setEntrance(entrance);
            sceneService.matchActivity(sceneLinkRelateRequest);
        }

        //???????????????????????????????????????????????????
        Long configId = input.getId();
        QueryWrapper bindFileIdsQuery = new QueryWrapper();
        bindFileIdsQuery.eq("config_id", configId);
        List<InterfacePerformanceParamEntity> paramEntityList = paramMapper.selectList(bindFileIdsQuery);
        if (!CollectionUtils.isEmpty(paramEntityList)) {
            Set<Long> fileIds = paramEntityList.stream().map(o -> o.getFileId()).collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(fileIds)) {
                List IdsIn = Lists.newArrayList();
                IdsIn.addAll(fileIds);
                //???filemanage???
                List<FileManageResult> fileManageResultList = fileManageDao.selectFileManageByIds(IdsIn);
                if (!CollectionUtils.isEmpty(fileManageResultList)) {

                    //????????????????????????????????????
                    boolean onlyJmx = false;
                    onlyJmx = fileManageResultList.size() == 1 && 0 == fileManageResultList.get(0).getFileType();
                    if (!onlyJmx) {
                        List<FileManageUpdateRequest> updateRequestArrayList = Lists.newArrayList();
                        BusinessFlowDataFileRequest dataFileRequest = new BusinessFlowDataFileRequest();
                        fileManageResultList.stream().forEach(
                                one -> {
                                    Integer fileType = one.getFileType();
                                    //??????????????????
                                    if (0 != fileType) {
                                        FileManageUpdateRequest request = new FileManageUpdateRequest();
                                        request.setUploadTime(one.getUploadTime());
                                        request.setId(one.getId());
                                        request.setFileType(one.getFileType());
                                        request.setFileName(one.getFileName());
                                        request.setFileSize(one.getFileSize());
                                        request.setIsDeleted(one.getIsDeleted());
                                        request.setDownloadUrl(one.getUploadPath());
                                        updateRequestArrayList.add(request);

                                    }
                                }
                        );

                        dataFileRequest.setId(flowInfo.getId());
                        dataFileRequest.setFileManageUpdateRequests(updateRequestArrayList);
                        uploadDataFile(dataFileRequest);
                    }

                }
            }
        }

        //??????????????????id??????????????????
        InterfacePerformanceConfigSceneRelateShipEntity entity = new InterfacePerformanceConfigSceneRelateShipEntity();
        entity.setApiId(input.getId());
        entity.setFlowId(flowInfo.getId());
        upsert(entity);


        //???????????????????????????ID?????????
        input.getPressureConfigRequest().getBasicInfo().setName(flowInfo.getBusinessProcessName());
        input.getPressureConfigRequest().getBasicInfo().setBusinessFlowId(flowInfo.getId());

        // TODO: 2022/5/26 ???????????????????????????
        List<ScriptNode> scriptNodes = JsonHelper.json2List(bizFlowDetailByApiId(input.getId())
                .getScriptJmxNode(), ScriptNode.class);
        List<ScriptNode> threadGroup = Lists.newArrayList();
        List<ScriptNode> apiScriptNode = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(scriptNodes)) {
            for (ScriptNode root : scriptNodes) {
                List<ScriptNode> secondList = root.getChildren();
                if (!CollectionUtils.isEmpty(secondList)) {
                    for (ScriptNode second : secondList) {
                        if (second.getType() == NodeTypeEnum.THREAD_GROUP) {
                            threadGroup.add(second);
                        }
                    }
                    for (ScriptNode second : secondList) {
                        if (!CollectionUtils.isEmpty(second.getChildren())) {
                            for (ScriptNode node : second.getChildren()) {
                                if (node.getType() == NodeTypeEnum.SAMPLER) {
                                    apiScriptNode.add(node);
                                }
                            }
                        }
                    }
                }
            }
        }

        //??????
        Map<String, SceneRequest.Goal> goal = Maps.newHashMap();
        String key = apiScriptNode.get(0).getXpathMd5();
        SceneRequest.Goal value = input.getPressureConfigRequest().getTargetGoal();
        goal.put(key, value);
        input.getPressureConfigRequest().setGoal(goal);
        //??????????????????
        // TODO: 2022/5/26
        NewSceneRequest.PtConfig ptConfig = new NewSceneRequest.PtConfig();
        NewSceneRequest.ThreadGroup realPressureConfig = input.getPressureConfigRequest().getThreadConfig();
        ptConfig.setDuration(realPressureConfig.getDuration());
        ptConfig.setPodNum(realPressureConfig.getPodNum());
        ptConfig.setUnit(realPressureConfig.getUnit());
        ptConfig.setEstimateFlow(null);

        Map<String, NewSceneRequest.ThreadGroupConfig> threadGroupConfigMap = Maps.newHashMap();
        String keyOfThreadConfig = threadGroup.get(0).getXpathMd5();
        NewSceneRequest.ThreadGroupConfig threadGroupConfig = new NewSceneRequest.ThreadGroupConfig();
        threadGroupConfig.setThreadNum(realPressureConfig.getThreadNum());
        ;
        threadGroupConfig.setMode(realPressureConfig.getMode());
        threadGroupConfig.setType(realPressureConfig.getType());
        threadGroupConfig.setEstimateFlow(200d);
        threadGroupConfig.setSteps(realPressureConfig.getSteps());
        threadGroupConfig.setRampUp(realPressureConfig.getRampUp());
        threadGroupConfig.setRampUpUnit(realPressureConfig.getRampUpUnit());
        threadGroupConfigMap.put(keyOfThreadConfig, threadGroupConfig);
        ptConfig.setThreadGroupConfigMap(threadGroupConfigMap);

        input.getPressureConfigRequest().setConfig(ptConfig);
        //??????????????????
        input.getPressureConfigRequest().setDestroyMonitoringGoal(Collections.emptyList());
        //??????????????????
        input.getPressureConfigRequest().setWarnMonitoringGoal(Collections.emptyList());

        return;
    }

    @Override
    public ResponseResult<Boolean> update(PerformanceConfigCreateInput request) throws Throwable {
        //  doBefore(request);
        return super.update(request);
    }

    @Override
    public Boolean update(PerformanceDataFileRequest input) {
        return super.update(input);
    }

    @Override
    public ResponseResult delete(Long apiId) {
        return super.delete(apiId);
    }


    @Override
    public ResponseResult<SceneDetailResponse> query(Long apiId) throws Throwable {
        return super.query(apiId);
    }

    @Override
    public SceneEntity bizFlowDetailByApiId(Long apiId) {
        return super.bizFlowDetailByApiId(apiId);
    }

    @Override
    public ResponseResult uploadDataFile(BusinessFlowDataFileRequest request) {
        return super.uploadDataFile(request);
    }

    @Override
    public String scriptGenerator(Long id) {
        ReqBuilder reqParam = buildReq(id);
        Map<String, Object> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        ResponseWrapper responseWrapper = null;
        try {
            responseWrapper = HttpSupport.get().get("post").to
                    (urlOfCloud + uriOfScriptGenerator, JSON.toJSONString(reqParam), header);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        if (Objects.isNull(responseWrapper) || Objects.isNull(responseWrapper.getData())) {
            throw new RuntimeException("??????????????????.");
        }
        String result = JSON.parseObject(responseWrapper.getData()).getString("data");
        if (StringUtil.isBlank(result)) {
            log.error("??????????????????,{}" + JSON.toJSONString(reqParam));
            throw new RuntimeException("??????????????????");
        }
        return result;
    }


    private ReqBuilder buildHeader(String header, ReqBuilder builder) {
        if (StringUtil.isNotBlank(header)) {
            List<String> lines = null;
            if (header.contains("\n")) {
                lines = Splitter.on("\n").splitToList(header);
            } else if (header.contains("\n\t")) {
                lines = Splitter.on("\n\t").splitToList(header);
            } else {
                lines = Lists.newArrayList();
                lines.add(header);
            }

            if (!CollectionUtils.isEmpty(lines)) {
                for (String line : lines) {
                    if (line.contains(":")) {
                        String[] keyValue = line.trim().split(":");

                        Map<String, String> toHeader = Maps.newHashMap();

                        toHeader.put("key", keyValue[0]);
                        toHeader.put("value", keyValue[1]);
                        builder.addHeaders(toHeader);
                    }
                }
            }
        }
        return builder;
    }

    private ReqBuilder buildData(Long id, ReqBuilder builder) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("config_id", id);
        queryWrapper.orderByAsc("file_column_index");
        List<InterfacePerformanceParamEntity> paramEntityList = paramMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(paramEntityList)) {
            paramEntityList.stream().forEach(entry -> {
                String fieldName = entry.getParamName();
                String fileName = entry.getParamValue();
                //???????????????
                Long fileId = entry.getFileId();
                FileManageResult fileManageResult = fileManageDao.selectFileManageById(fileId);
                Assert.isTrue(fileManageResult != null, "??????????????????.");
                String path = fileManageResult.getUploadPath();
                Map<String, Object> $data = Maps.newHashMap();
                //fileName ?????????,??????????????? fieldName ????????? path ????????????
                $data.put("name", fileName);
                $data.put("format", formatName(fieldName));
                $data.put("path", path);
                $data.put("ignoreFirstLine", true);
                builder.addDatas($data);
            });
        }
        //????????????????????????format
        List<Map<String, Object>> datas = builder.datas;
        Map<String, Map<String, Object>> combineFormatMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(datas)) {
            for (Map<String, Object> one : datas) {
                String path = (String) one.get("path");
                if (combineFormatMap.get(path) == null) {
                    combineFormatMap.put(path, one);
                } else {
                    Map<String, Object> inner = combineFormatMap.get(path);
                    String needAddFormat = (String) one.get("format");
                    String oldFormat = (String) inner.get("format");
                    String newFormat = Joiner.on(",").join(oldFormat, needAddFormat);
                    inner.put("format", newFormat);
                    combineFormatMap.put(path, inner);
                }
            }
            builder.datas = Lists.newArrayList(combineFormatMap.values().iterator());
        }
        return builder;
    }

    private String formatName(String name) {
        if (!StringUtil.isEmpty(name)) {
            return name;
        }
        return null;
    }

    private ReqBuilder buildReq(Long id) {
        InterfacePerformanceConfigEntity record = configMapper.selectById(id);
        ReqBuilder builder = new ReqBuilder();
        builder.setName(record.getName());
        builder.setUrl(record.getRequestUrl());
        builder.setBody(record.getBody());
        builder.setMethod(record.getHttpMethod());
        // ???header
        buildHeader(record.getHeaders(), builder);

        // ??????Content-Type
        ContentTypeVO contentTypeVO = JsonHelper.json2Bean(record.getContentType(), ContentTypeVO.class);
        String type = performanceDebugUtil.getContentType(contentTypeVO);
        if (StringUtils.isNotBlank(type)) {
            Map<String, String> toHeader = Maps.newHashMap();
            toHeader.put("key", "Content-Type");
            toHeader.put("value", type);
            builder.addHeaders(toHeader);
        }

        //???data
        buildData(id, builder);
        return builder.build();
    }

    @Data
    public class ReqBuilder implements Serializable {
        String name;
        String url;
        String method;
        List<Map<String, String>> headers = Lists.newArrayList();
        String body;
        List<Map<String, Object>> datas = Lists.newArrayList();


        /**
         * ???????????????
         *
         * @return
         */
        public ReqBuilder build() {
            return this;
        }


        public ReqBuilder setMethod(String method) {
            this.method = method;
            return this;
        }

        public ReqBuilder setHeaders(List headers) {
            this.headers = headers;
            return this;
        }

        public ReqBuilder addHeaders(Map<String, String> header) {
            this.headers.add(header);
            return this;
        }


        public ReqBuilder addDatas(Map<String, Object> data) {
            this.datas.add(data);
            return this;
        }


    }
}
