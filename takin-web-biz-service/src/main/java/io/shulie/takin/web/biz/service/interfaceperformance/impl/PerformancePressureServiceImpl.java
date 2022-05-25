package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import io.shulie.takin.cloud.sdk.model.request.file.UploadRequest;
import io.shulie.takin.cloud.sdk.model.response.file.UploadResponse;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.scene.SceneDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigSceneRelateShipEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceParamEntity;
import io.shulie.takin.web.data.result.filemanage.FileManageResult;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        File tempFile = new File(TEMP_DICTIONARY + fileName);
        try {
            FileWriter writer = new FileWriter(tempFile.getName());
            writer.write(script);
            writer.close();
            List files = Lists.newArrayList();
            files.add(tempFile);

            UploadRequest uploadRequest = new UploadRequest();
            uploadRequest.setFileList(files);
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

        //保存业务流程id到关系映射表
        InterfacePerformanceConfigSceneRelateShipEntity entity = new InterfacePerformanceConfigSceneRelateShipEntity();
        entity.setApiId(input.getId());
        entity.setFlowId(flowInfo.getId());
        upsert(entity);

        //自动匹配活动
        activityAutoMatcher(flowInfo.getId());
        //回填创建压测场景的ID和名字
        input.getPressureConfigRequest().getBasicInfo().setSceneId(flowInfo.getId());
        input.getPressureConfigRequest().getBasicInfo().setName(flowInfo.getBusinessProcessName());
        input.getPressureConfigRequest().getBasicInfo().setBusinessFlowId(flowInfo.getId());
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
    public ResponseResult uploadDataFile(BusinessFlowDataFileRequest request) {
        return super.uploadDataFile(request);
    }

    @Override
    public String scriptGenerator(Long id) {

        // TODO: 2022/5/20 调用cloud sdk 生成脚本
        ReqBuilder reqParam = buildReq(id);
        /*  return null;*/
        return TestConstant.default_jmx_str;
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
                        toHeader.put(keyValue[0], keyValue[1]);
                        builder.addHeaders(JSON.toJSONString(toHeader));
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
        builder.setName(record.getName()).setUrl(record.getRequestUrl()).setBody(record.getBody()).setMethod(record.getHttpMethod());
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

    public class ReqBuilder {
        String name;
        String url;
        String method;
        String[] headers;
        List<String> headerList = Lists.newArrayList();
        String body;
        String datas[];
        List<String> dataList = Lists.newArrayList();


        /**
         * 构建请求体
         *
         * @return
         */
        public ReqBuilder build() {
            if (!CollectionUtils.isEmpty(headerList)) {
                this.headers = headerList.toArray(new String[headerList.size()]);
            }
            if (!CollectionUtils.isEmpty(dataList)) {
                this.datas = dataList.toArray(new String[dataList.size()]);
            }
            this.headerList = null;
            this.dataList = null;
            return this;
        }

        public ReqBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ReqBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public ReqBuilder setMethod(String method) {
            this.method = method;
            return this;
        }

        public ReqBuilder setHeaders(String[] headers) {
            this.headers = headers;
            return this;
        }

        public ReqBuilder addHeaders(String header) {
            this.headerList.add(header);
            return this;
        }

        public ReqBuilder setBody(String body) {
            this.body = body;
            return this;
        }

        public ReqBuilder addDatas(String data) {
            this.dataList.add(data);
            return this;
        }

        public ReqBuilder setDatas(String[] datas) {
            this.datas = datas;
            return this;
        }
    }
}
