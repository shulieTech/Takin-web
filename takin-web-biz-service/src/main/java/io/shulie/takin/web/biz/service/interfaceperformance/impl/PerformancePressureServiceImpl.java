package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.util.HttpSupport;
import com.pamirs.takin.common.util.ResponseWrapper;
import com.pamirs.takin.entity.domain.dto.linkmanage.ScriptJmxNode;
import io.shulie.takin.adapter.api.model.request.file.UploadRequest;
import io.shulie.takin.adapter.api.model.response.file.UploadResponse;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.SceneLinkRelateRequest;
import io.shulie.takin.web.biz.pojo.request.scene.SceneDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowThreadResponse;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigSceneRelateShipEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceParamEntity;
import io.shulie.takin.web.data.model.mysql.SceneEntity;
import io.shulie.takin.web.data.result.filemanage.FileManageResult;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @Author: vernon
 * @Date: 2022/5/20 10:37
 * @Description:
 */
@Component
public class PerformancePressureServiceImpl extends AbstractPerformancePressureService {

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
        } finally {
            tempFile.delete();
        }
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
        //上传文件到服务器
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
        SceneLinkRelateRequest sceneLinkRelateRequest = new SceneLinkRelateRequest();
        BeanUtils.copyProperties(scriptJmxNode, sceneLinkRelateRequest);
        sceneLinkRelateRequest.setBusinessType(BusinessTypeEnum.VIRTUAL_BUSINESS.getType());
        sceneLinkRelateRequest.setBusinessFlowId(flowInfo.getId());
        sceneService.matchActivity(sceneLinkRelateRequest);

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
        // input.getPressureConfigRequest().setConfig();
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

        // TODO: 2022/5/20 调用cloud sdk 生成脚本
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
        return JSON.parseObject(responseWrapper.getData()).getString("data");
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
        List<InterfacePerformanceParamEntity> paramEntityList = paramMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(paramEntityList)) {
            paramEntityList.stream().forEach(entry -> {
                String name = entry.getParamName();
                String format = entry.getParamValue();
                //查文件路径
                Long fileId = entry.getFileId();
                FileManageResult fileManageResult = fileManageDao.selectFileManageById(fileId);
                Assert.isTrue(fileManageResult != null, "数据文件为空.");
                String path = fileManageResult.getUploadPath();
                Map<String, String> $data = Maps.newHashMap();
                $data.put("name", name);
                $data.put("format", formatName(name));
                $data.put("path", path);
                builder.addDatas(JSON.toJSONString($data));
            });
        }
        return builder;
    }

    private String formatName(String name) {
        if (StringUtil.isEmpty(name)) {
            return name;
        }
        return "${" + name + "}";
    }

    private ReqBuilder buildReq(Long id) {
        // TODO: 2022/5/20 查询基本信息
        InterfacePerformanceConfigEntity record = configMapper.selectById(id);

        ReqBuilder builder = new ReqBuilder();
        builder.setName(record.getName());
        builder.setUrl(record.getRequestUrl());
        builder.setBody(record.getBody());
        builder.setMethod(record.getHttpMethod());
        //放header
        buildHeader(record.getHeaders(), builder);


        //获取param表的id
        InterfacePerformanceParamEntity paramEntity = fetchParamEntryByApiId(id);
        if (!Objects.isNull(paramEntity)) {
            //放data
            buildData(id, builder);
        }


        return builder.build();
    }

    @Data
    public class ReqBuilder implements Serializable {
        String name;
        String url;
        String method;
        List<Map<String, String>> headers = Lists.newArrayList();
        String body;
        String datas[];
        List<String> dataList = Lists.newArrayList();


        /**
         * 构建请求体
         *
         * @return
         */
        public ReqBuilder build() {
            this.datas = dataList.toArray(new String[dataList.size()]);
            this.dataList = null;
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


        public ReqBuilder addDatas(String data) {
            this.dataList.add(data);
            return this;
        }


    }
}
