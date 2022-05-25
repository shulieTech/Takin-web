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
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.scene.SceneDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceParamEntity;
import io.shulie.takin.web.data.result.filemanage.FileManageResult;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
     * 上传文件
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
        //自动匹配活动
        activityAutoMatcher(flowInfo.getId());
        //回填创建压测场景的ID和名字
        input.getPressureConfigRequest().getBasicInfo().setSceneId(flowInfo.getId());
        input.getPressureConfigRequest().getBasicInfo().setName(flowInfo.getBusinessProcessName());
        return;
    }


    @Override
    public ResponseResult<Boolean> update(PerformanceConfigCreateInput request) throws Throwable {
        doBefore(request);
        return super.update(request);
    }

    @Override
    public ResponseResult delete(Long configId) {
        return super.delete(configId);
    }


    @Override
    public ResponseResult<SceneDetailResponse> query(PerformanceConfigQueryRequest input) throws Throwable {
        return super.query(input);
    }

    @Override
    public String scriptGenerator(Long id) {

        // TODO: 2022/5/20 调用cloud sdk 生成脚本
        //  ReqBuilder reqParam = buildReq(id);
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

    private ReqBuilder buildBody(Long id, ReqBuilder builder) {
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
                $data.put("format", format);
                $data.put("path", path);
                builder.addDatas(JSON.toJSONString($data));
            });
        }
        return builder;
    }

    private ReqBuilder buildReq(Long id) {
        // TODO: 2022/5/20 查询基本信息
        InterfacePerformanceConfigEntity record = configMapper.selectById(id);

        ReqBuilder builder = new ReqBuilder();
        builder.setName(record.getName()).setUrl(record.getRequestUrl()).setBody(record.getBody()).setMethod(record.getHttpMethod());
        //放header
        buildHeader(record.getHeaders(), builder);
        //放data
        buildBody(id, builder);

        return builder.build();
    }

    public class ReqBuilder {
        String name;
        String url;
        String method;
        String[] headers;
        List<String> headerList;
        String body;
        String datas[];
        List<String> dataList;


        /**
         * 构建请求体
         *
         * @return
         */
        public ReqBuilder build() {
            this.headers = headerList.toArray(new String[headerList.size()]);
            this.datas = dataList.toArray(new String[dataList.size()]);
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
