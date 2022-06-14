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
     * 临时文件目录
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
            log.error("处理文件失败,{}", ExceptionUtils.getStackTrace(e));
        } finally {
            tempFile.delete();
        }
        return null;
    }


    /**
     * 创建业务流程
     */
    public BusinessFlowDetailResponse bizFlowCreator(List<UploadResponse> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("上传文件失败，takin-cloud没有响应数据.");
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
     * 自动匹配业务流程
     *
     * @param id
     * @return
     */
    public void activityAutoMatcher(Long id) {
        sceneService.autoMatchActivity(id);
    }

    /**
     * * 业务活动相关操作
     * * 上传文件到服务器
     * * 创建业务流程
     * * 自动匹配活动
     * * 返回业务流程的id
     */
    public void doBefore(PerformanceConfigCreateInput input) throws IOException {
        //上传jmx文件到服务器
        List<UploadResponse> uploadResult = upload(input.getId(), input.getName());
        //创建业务流程
        BusinessFlowDetailResponse flowInfo = bizFlowCreator(uploadResult);
        //自动匹配活动
        activityAutoMatcher(flowInfo.getId());
        //设置为虚拟业务活动 businessType设置为1(因为自动匹配可能匹配不上)
        // xpathMd5取业务流程返回的threadgroup的xpathmd5 。。。。这堆业务逻辑是真的混乱和恶心
        //先取xpathMd5 很麻烦才能取到。。。先获取业务流程详情，再遍历下面节点
        BusinessFlowDetailResponse dto = sceneService.getBusinessFlowDetail(flowInfo.getId());
        //根结点的xpathmd5
        String rootXpathMd5 = dto.getScriptJmxNodeList().get(0).getValue();

        BusinessFlowThreadResponse response = sceneService.getThreadGroupDetail(flowInfo.getId(), rootXpathMd5);
        ScriptJmxNode scriptJmxNode = response.getThreadScriptJmxNodes().get(0).getChildren().get(0);

        // 如果有找到入口的话,新增业务活动
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
            // 拼接一个RpcType
            sceneLinkRelateRequest.setEntrance(entrancePath + "|0");
            sceneLinkRelateRequest.setActivityName(input.getName());

            sceneService.matchActivity(sceneLinkRelateRequest);
        } else {
            SceneLinkRelateRequest sceneLinkRelateRequest = new SceneLinkRelateRequest();
            BeanUtils.copyProperties(scriptJmxNode, sceneLinkRelateRequest);
            sceneLinkRelateRequest.setBusinessType(BusinessTypeEnum.VIRTUAL_BUSINESS.getType());
            sceneLinkRelateRequest.setBusinessFlowId(flowInfo.getId());
            // 拼接ServerName
            String urlPath = UrlUtil.parseUrl(input.getRequestUrl());
            String entrance = input.getHttpMethod() + "|" + urlPath + "|0";
            sceneLinkRelateRequest.setEntrance(entrance);
            sceneService.matchActivity(sceneLinkRelateRequest);
        }

        //如果有历史数据文件，则绑定数据文件
        Long configId = input.getId();
        QueryWrapper bindFileIdsQuery = new QueryWrapper();
        bindFileIdsQuery.eq("config_id", configId);
        List<InterfacePerformanceParamEntity> paramEntityList = paramMapper.selectList(bindFileIdsQuery);
        if (!CollectionUtils.isEmpty(paramEntityList)) {
            Set<Long> fileIds = paramEntityList.stream().map(o -> o.getFileId()).collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(fileIds)) {
                List IdsIn = Lists.newArrayList();
                IdsIn.addAll(fileIds);
                //查filemanage表
                List<FileManageResult> fileManageResultList = fileManageDao.selectFileManageByIds(IdsIn);
                if (!CollectionUtils.isEmpty(fileManageResultList)) {

                    //过滤掉只有脚本文件的场景
                    boolean onlyJmx = false;
                    onlyJmx = fileManageResultList.size() == 1 && 0 == fileManageResultList.get(0).getFileType();
                    if (!onlyJmx) {
                        List<FileManageUpdateRequest> updateRequestArrayList = Lists.newArrayList();
                        BusinessFlowDataFileRequest dataFileRequest = new BusinessFlowDataFileRequest();
                        fileManageResultList.stream().forEach(
                                one -> {
                                    Integer fileType = one.getFileType();
                                    //过掉脚本文件
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

        //保存业务流程id到关系映射表
        InterfacePerformanceConfigSceneRelateShipEntity entity = new InterfacePerformanceConfigSceneRelateShipEntity();
        entity.setApiId(input.getId());
        entity.setFlowId(flowInfo.getId());
        upsert(entity);


        //回填创建压测场景的ID和名字
        input.getPressureConfigRequest().getBasicInfo().setName(flowInfo.getBusinessProcessName());
        input.getPressureConfigRequest().getBasicInfo().setBusinessFlowId(flowInfo.getId());

        // TODO: 2022/5/26 回写真实的压测目标
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

        //目标
        Map<String, SceneRequest.Goal> goal = Maps.newHashMap();
        String key = apiScriptNode.get(0).getXpathMd5();
        SceneRequest.Goal value = input.getPressureConfigRequest().getTargetGoal();
        goal.put(key, value);
        input.getPressureConfigRequest().setGoal(goal);
        //压测模式配置
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
        //置空停止条件
        input.getPressureConfigRequest().setDestroyMonitoringGoal(Collections.emptyList());
        //置空告警条件
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
            throw new RuntimeException("生成脚本异常.");
        }
        String result = JSON.parseObject(responseWrapper.getData()).getString("data");
        if (StringUtil.isBlank(result)) {
            log.error("生成脚本为空,{}" + JSON.toJSONString(reqParam));
            throw new RuntimeException("生成脚本为空");
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
                //查文件路径
                Long fileId = entry.getFileId();
                FileManageResult fileManageResult = fileManageDao.selectFileManageById(fileId);
                Assert.isTrue(fileManageResult != null, "数据文件为空.");
                String path = fileManageResult.getUploadPath();
                Map<String, String> $data = Maps.newHashMap();
                //fileName 文件名,数据源名， fieldName 变量名 path 文件路径
                $data.put("name", fileName);
                $data.put("format", formatName(fieldName));
                $data.put("path", path);
                builder.addDatas($data);
            });
        }
        //合并文件名相同的format
        List<Map<String, String>> datas = builder.datas;
        Map<String, Map<String, String>> combineFormatMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(datas)) {
            for (Map<String, String> one : datas) {
                String path = one.get("path");
                if (combineFormatMap.get(path) == null) {
                    combineFormatMap.put(path, one);
                } else {
                    Map<String, String> inner = combineFormatMap.get(path);
                    String needAddFormat = one.get("format");
                    String oldFormat = inner.get("format");
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
        // 放header
        buildHeader(record.getHeaders(), builder);

        // 处理Content-Type
        ContentTypeVO contentTypeVO = JsonHelper.json2Bean(record.getContentType(), ContentTypeVO.class);
        String type = performanceDebugUtil.getContentType(contentTypeVO);
        Map<String, String> toHeader = Maps.newHashMap();
        toHeader.put("key", "Content-Type");
        toHeader.put("value", type);
        //放data
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
        List<Map<String, String>> datas = Lists.newArrayList();


        /**
         * 构建请求体
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


        public ReqBuilder addDatas(Map<String, String> data) {
            this.datas.add(data);
            return this;
        }


    }
}
