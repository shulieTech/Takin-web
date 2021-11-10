package io.shulie.takin.web.biz.service.agentupgradeonline.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import io.shulie.takin.web.biz.pojo.request.file.FileUploadRequest;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.AgentPluginUploadResponse;
import io.shulie.takin.web.biz.pojo.response.common.FileUploadResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentUploadResponse;
import io.shulie.takin.web.biz.service.ApiService;
import io.shulie.takin.web.biz.service.agentupgradeonline.PluginLibraryService;
import io.shulie.takin.web.common.agent.IAgentZipResolver;
import io.shulie.takin.web.common.enums.agentupgradeonline.PluginTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 插件版本库(PluginLibrary)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:24:05
 */
@Service
public class PluginLibraryServiceImpl implements PluginLibraryService {

    @Resource
    private ApiService apiService;

    @Resource
    private Map<PluginTypeEnum, IAgentZipResolver> agentZipResolverMap;

    @Override
    public AgentPluginUploadResponse upload(MultipartFile file) {
        AgentPluginUploadResponse pluginUploadResponse = new AgentPluginUploadResponse();
        List<String> errorMessages = new ArrayList<>();
        pluginUploadResponse.setErrorMessages(errorMessages);

        // 识别上传的是哪种类型的包
        PluginTypeEnum typeEnum = getPluginType(file.getOriginalFilename());
        if (typeEnum == null) {
            pluginUploadResponse.setAllowSubmit(false);
            errorMessages.add("不支持的插件类型");
            return pluginUploadResponse;
        }

        // TODO 校验包中的文件是否合法

        // TODO module.properties 文件

        FileUploadResponse fileUploadResponse;
        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFile(file);
        //fileUploadRequest.setFolder(DIR_NAME);
        // 上传文件
        fileUploadResponse = apiService.uploadFile(fileUploadRequest);
        AgentUploadResponse agentUploadResponse = new AgentUploadResponse();
        BeanUtil.copyProperties(fileUploadResponse, agentUploadResponse);

        return null;
    }

    /**
     * 根据文件名获取插件类型
     *
     * @param fileName 文件名
     * @return 插件类型
     */
    private PluginTypeEnum getPluginType(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        switch (fileName.toLowerCase()) {
            case "simulator-agent":
                return PluginTypeEnum.AGENT;
            case "simulator":
                return PluginTypeEnum.SIMULATOR;
            case "modules":
                return PluginTypeEnum.MIDDLEWARE;
            default:
                return null;
        }
    }
}
