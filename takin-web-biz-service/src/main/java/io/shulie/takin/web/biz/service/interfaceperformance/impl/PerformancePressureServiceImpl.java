package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PressureConfigRequest;
import io.shulie.takin.web.biz.pojo.request.scene.SceneDetailResponse;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceParamEntity;
import io.shulie.takin.web.data.result.filemanage.FileManageResult;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

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
    public ResponseResult<Long> add(PressureConfigRequest request) {
        return super.add(request);
    }


    @Override
    public ResponseResult<Boolean> update(PressureConfigRequest request) {
        return super.update(request);
    }

    @Override
    public ResponseResult delete(Long configId) {
        return super.delete(configId);
    }

    @Override
    public ResponseResult<SceneDetailResponse> query(PerformanceConfigQueryRequest input) {
        return super.query(input);
    }

    @Override
    public ResponseResult<String> scriptGenerator(Long id) {
        ReqBuilder reqParam = buildReq(id);
        // TODO: 2022/5/20 调用cloud sdk 生成脚本
        return null;
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
        List<InterfacePerformanceParamEntity> paramEntityList
                = paramMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(paramEntityList)) {
            paramEntityList.stream()
                    .forEach(entry -> {
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
        InterfacePerformanceConfigEntity record =
                configMapper.selectById(id);

        ReqBuilder builder = new ReqBuilder();
        builder.setName(record.getName())
                .setUrl(record.getRequestUrl())
                .setBody(record.getBody())
                .setMethod(record.getHttpMethod());
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
