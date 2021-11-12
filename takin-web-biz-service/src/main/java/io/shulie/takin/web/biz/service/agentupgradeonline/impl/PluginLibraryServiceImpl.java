package io.shulie.takin.web.biz.service.agentupgradeonline.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.PluginLibraryListQueryRequest;
import io.shulie.takin.web.biz.pojo.request.file.FileUploadRequest;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.AgentPluginUploadResponse;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.PluginInfo;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.PluginLibraryListResponse;
import io.shulie.takin.web.biz.pojo.response.common.FileUploadResponse;
import io.shulie.takin.web.biz.service.ApiService;
import io.shulie.takin.web.biz.service.agentupgradeonline.PluginLibraryService;
import io.shulie.takin.web.common.agent.IAgentZipResolver;
import io.shulie.takin.web.common.agent.ModulePropertiesResolver;
import io.shulie.takin.web.common.enums.agentupgradeonline.PluginTypeEnum;
import io.shulie.takin.web.common.pojo.bo.agent.AgentModuleInfo;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.agentupgradeonline.PluginLibraryDAO;
import io.shulie.takin.web.data.param.agentupgradeonline.PluginLibraryListQueryParam;
import io.shulie.takin.web.data.param.agentupgradeonline.PluginLibraryQueryParam;
import io.shulie.takin.web.data.result.agentUpgradeOnline.PluginLibraryDetailResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
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
    private PluginLibraryDAO pluginLibraryDAO;

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

        FileUploadResponse fileUploadResponse;
        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFile(file);
        fileUploadRequest.setFolder(typeEnum.getBaseDir());
        // 上传文件
        fileUploadResponse = apiService.uploadFile(fileUploadRequest);

        // 校验包中的文件内容是否合法
        IAgentZipResolver agentZipResolver = agentZipResolverMap.get(typeEnum);
        List<String> checkFileMsg = agentZipResolver.checkFile(fileUploadResponse.getFilePath());
        if (CollectionUtils.isNotEmpty(checkFileMsg)) {
            pluginUploadResponse.setAllowSubmit(false);
            errorMessages.addAll(checkFileMsg);
            deleteFile(fileUploadResponse.getFilePath());
            return pluginUploadResponse;
        }

        // 解析 module.properties 文件
        List<AgentModuleInfo> moduleInfos = ModulePropertiesResolver.resolver(
            agentZipResolver.readModuleInfo(fileUploadResponse.getFilePath()));

        // 检查这些插件在数据库中是否已经存在，存在则不允许提交
        List<PluginLibraryQueryParam> queryParams = moduleInfos.stream().map(item -> {
            PluginLibraryQueryParam queryParam = new PluginLibraryQueryParam();
            queryParam.setPluginName(item.getModuleId());
            queryParam.setVersion(item.getModuleVersion());
            return queryParam;
        }).collect(Collectors.toList());

        List<PluginLibraryDetailResult> pluginLibraryDetailResults = pluginLibraryDAO.queryList(queryParams);
        if (CollectionUtils.isNotEmpty(pluginLibraryDetailResults)) {
            pluginUploadResponse.setAllowSubmit(false);
            pluginLibraryDetailResults.forEach(
                item -> errorMessages.add(item.getPluginName() + "-" + item.getVersion() + "已经存在，不允许重复提交。"));
            deleteFile(fileUploadResponse.getFilePath());
            return pluginUploadResponse;
        }

        // 组装结果返回
        pluginUploadResponse.setFileName(fileUploadResponse.getFileName());
        pluginUploadResponse.setFilePath(fileUploadResponse.getFilePath());
        pluginUploadResponse.setOriginalName(fileUploadResponse.getOriginalName());
        if (PluginTypeEnum.SIMULATOR.equals(typeEnum)) {
            pluginUploadResponse.setAllowAddConfig(true);
            pluginUploadResponse.setVersion(moduleInfos.get(0).getModuleVersion());
        } else {
            pluginUploadResponse.setAllowAddConfig(false);
        }
        pluginUploadResponse.setAllowSubmit(true);
        pluginUploadResponse.setPluginInfoList(moduleInfos.stream().map(item -> {
            PluginInfo pluginInfo = new PluginInfo();
            pluginInfo.setPluginName(item.getModuleId());
            pluginInfo.setPluginType(typeEnum.getCode());
            pluginInfo.setPluginVersion(item.getModuleVersion());
            pluginInfo.setIsCustomMode(item.getCustomized());
            pluginInfo.setUpdateInfo(item.getUpdateInfo());
            pluginInfo.setDependenciesInfo(item.getDependenciesInfoStr());
            return pluginInfo;
        }).collect(Collectors.toList()));

        return pluginUploadResponse;
    }

    @Override
    public File getPluginFile(Long pluginId) {
        PluginLibraryDetailResult pluginLibraryDetailResult = pluginLibraryDAO.queryById(pluginId);
        return pluginLibraryDetailResult == null ? null : new File(pluginLibraryDetailResult.getDownloadPath());
    }

    @Override
    public List<PluginInfo> queryByPluginName(String pluginName) {
        return CommonUtil.list2list(pluginLibraryDAO.queryByPluginName(pluginName), PluginInfo.class);
    }

    @Override
    public List<String> queryAllPluginNames() {
        return pluginLibraryDAO.queryAllPluginName();
    }

    @Override
    public PagingList<PluginLibraryListResponse> list(PluginLibraryListQueryRequest query) {
        PluginLibraryListQueryParam queryParam = new PluginLibraryListQueryParam();
        BeanUtils.copyProperties(query, queryParam);
        PagingList<PluginLibraryDetailResult> resultPage = pluginLibraryDAO.page(queryParam);
        List<PluginLibraryDetailResult> results = resultPage.getList();
        if (CollectionUtil.isEmpty(results)) {
            return PagingList.empty();
        }

        return PagingList.of(CommonUtil.list2list(results, PluginLibraryListResponse.class), resultPage.getTotal());
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
        if (fileName.toLowerCase().startsWith("simulator-agent")) {
            return PluginTypeEnum.AGENT;
        } else if (fileName.toLowerCase().startsWith("simulator")) {
            return PluginTypeEnum.SIMULATOR;
        } else if (fileName.toLowerCase().startsWith("modules")) {
            return PluginTypeEnum.MIDDLEWARE;
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     */
    private void deleteFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return;
        }
        File existFile = new File(filePath);
        try {
            existFile.delete();
        } catch (SecurityException exception) {
            existFile.deleteOnExit();
        }
    }

}
