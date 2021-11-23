package io.shulie.takin.web.biz.service.agentupgradeonline.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.AgentLibraryCreateRequest;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.PluginAllowUpgradeLibraryListQueryRequest;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.PluginCreateRequest;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.PluginLibraryListQueryRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.file.FileUploadRequest;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.AgentPluginUploadResponse;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.PluginInfo;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.PluginLibraryListResponse;
import io.shulie.takin.web.biz.pojo.response.common.FileUploadResponse;
import io.shulie.takin.web.biz.service.ApiService;
import io.shulie.takin.web.biz.service.agentupgradeonline.AgentReportService;
import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeRefService;
import io.shulie.takin.web.biz.service.agentupgradeonline.PluginLibraryService;
import io.shulie.takin.web.biz.service.fastagentaccess.AgentConfigService;
import io.shulie.takin.web.biz.service.fastagentaccess.AgentVersionService;
import io.shulie.takin.web.biz.utils.agentupgradeonline.AgentPkgUtil;
import io.shulie.takin.web.biz.utils.fastagentaccess.AgentVersionUtil;
import io.shulie.takin.web.common.agent.IAgentZipResolver;
import io.shulie.takin.web.common.agent.ModulePropertiesResolver;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.enums.agentupgradeonline.PluginTypeEnum;
import io.shulie.takin.web.common.pojo.bo.agent.AgentModuleInfo;
import io.shulie.takin.web.common.pojo.bo.agent.PluginCreateBO;
import io.shulie.takin.web.common.util.AppCommonUtil;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.common.util.FileUtil;
import io.shulie.takin.web.data.dao.agentupgradeonline.PluginDependentDAO;
import io.shulie.takin.web.data.dao.agentupgradeonline.PluginLibraryDAO;
import io.shulie.takin.web.data.dao.agentupgradeonline.PluginTenantRefDAO;
import io.shulie.takin.web.data.dao.fastagentaccess.AgentVersionDAO;
import io.shulie.takin.web.data.param.agentupgradeonline.CreatePluginDependentParam;
import io.shulie.takin.web.data.param.agentupgradeonline.CreatePluginLibraryParam;
import io.shulie.takin.web.data.param.agentupgradeonline.PluginLibraryListQueryParam;
import io.shulie.takin.web.data.param.agentupgradeonline.PluginLibraryQueryParam;
import io.shulie.takin.web.data.param.fastagentaccess.CreateAgentVersionParam;
import io.shulie.takin.web.data.param.fastagentaccess.UpdateAgentVersionParam;
import io.shulie.takin.web.data.result.agentUpgradeOnline.PluginDependentDetailResult;
import io.shulie.takin.web.data.result.agentUpgradeOnline.PluginLibraryDetailResult;
import io.shulie.takin.web.data.result.application.AgentReportDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeRefDetailResult;
import io.shulie.takin.web.data.result.fastagentaccess.AgentVersionDetailResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 插件版本库(PluginLibrary)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:24:05
 */
@Service
public class PluginLibraryServiceImpl implements PluginLibraryService {

    /**
     * 上传文件的路径
     */
    @Value("${data.path}")
    protected String uploadPath;

    @Resource
    private ApiService apiService;

    @Resource
    private AgentConfigService agentConfigService;

    @Resource
    private PluginLibraryDAO pluginLibraryDAO;

    @Resource
    private PluginDependentDAO pluginDependentDAO;

    @Resource
    private PluginTenantRefDAO pluginTenantRefDAO;

    @Resource
    private AgentVersionDAO agentVersionDAO;

    @Resource
    private ThreadPoolExecutor agentAggregationThreadPool;

    @Autowired
    private AgentReportService agentReportService;

    @Autowired
    private AgentVersionService agentVersionService;

    @Autowired
    private ApplicationPluginUpgradeRefService upgradeRefService;



    private final Map<PluginTypeEnum, IAgentZipResolver> agentZipResolverMap = new HashMap<>();

    public PluginLibraryServiceImpl(List<IAgentZipResolver> resolverList) {
        resolverList.forEach(resolver -> agentZipResolverMap.put(resolver.getPluginType(), resolver));
    }

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
            FileUtil.deleteFile(fileUploadResponse.getFilePath());
            return pluginUploadResponse;
        }

        // 解析 module.properties 文件
        List<AgentModuleInfo> moduleInfos = ModulePropertiesResolver.resolver(
            fileUploadResponse.getFilePath(), agentZipResolver.getZipBaseDirName());

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
            FileUtil.deleteFile(fileUploadResponse.getFilePath());
            return pluginUploadResponse;
        }

        // 组装结果返回
        pluginUploadResponse.setFileName(fileUploadResponse.getFileName());
        pluginUploadResponse.setFilePath(fileUploadResponse.getFilePath());
        pluginUploadResponse.setOriginalName(fileUploadResponse.getOriginalName());
        pluginUploadResponse.setPluginType(typeEnum.getCode());
        pluginUploadResponse.setAllowAddConfig(PluginTypeEnum.SIMULATOR.equals(typeEnum));
        pluginUploadResponse.setAllowSubmit(true);
        pluginUploadResponse.setPluginInfoList(moduleInfos.stream().map(item -> {
            PluginInfo pluginInfo = new PluginInfo();
            pluginInfo.setPluginName(item.getModuleId());
            pluginInfo.setPluginType(typeEnum.getCode());
            pluginInfo.setVersion(item.getModuleVersion());
            pluginInfo.setIsCustomMode(item.getCustomized());
            pluginInfo.setUpdateDescription(item.getUpdateInfo());
            pluginInfo.setDependenciesInfo(item.getDependenciesInfoStr());
            return pluginInfo;
        }).collect(Collectors.toList()));

        return pluginUploadResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void release(AgentLibraryCreateRequest createRequest) {
        // 需要拆包，将文件放到指定目录，删除原文件
        IAgentZipResolver agentZipResolver = agentZipResolverMap.get(
            PluginTypeEnum.valueOf(createRequest.getPluginType()));
        if (agentZipResolver == null) {
            return;
        }

        List<PluginCreateBO> pluginCreateBOList = agentZipResolver.processFile(createRequest.getFilePath());
        if (CollectionUtils.isEmpty(pluginCreateBOList)) {
            return;
        }

        // 入库
        save(pluginCreateBOList, createRequest.getPluginList());

        // 如果是simulator包,还需要考虑配置数据
        if (PluginTypeEnum.SIMULATOR.getCode().equals(createRequest.getPluginType())) {
            dealAgentConfig(createRequest.getConfigList(), pluginCreateBOList.get(0).getPluginVersion());
        }

        // 异步，如果agent、simulator、modules都全了需要合成一个完整的agent包更新t_agent_version表记录
        agentAggregationThreadPool.execute(this::computeCompleteAgent);

        // 删除原有文件
        FileUtil.deleteFile(createRequest.getFilePath());
    }

    /**
     * 根据上传的内容，计算一个完整的agent包，并插入到t_agent_version表中
     */
    private void computeCompleteAgent() {
        // 查询每个插件包的数量
        Integer agentCount = pluginLibraryDAO.queryCountByPluginType(PluginTypeEnum.AGENT.getCode());
        Integer simulatorCount = pluginLibraryDAO.queryCountByPluginType(PluginTypeEnum.SIMULATOR.getCode());
        Integer middlewareCount = pluginLibraryDAO.queryCountByPluginType(PluginTypeEnum.MIDDLEWARE.getCode());

        // 其中如果有一个包的数量小于1，则不允许合成
        if (agentCount < 1 || simulatorCount < 1 || middlewareCount < 1) {
            return;
        }

        // 查询当前最高版本的agent、simulator、middleware的包
        PluginLibraryDetailResult agent = pluginLibraryDAO.queryMaxVersionPlugin(PluginTypeEnum.AGENT.getCode()).get(0);
        PluginLibraryDetailResult simulator = pluginLibraryDAO.queryMaxVersionPlugin(PluginTypeEnum.SIMULATOR.getCode())
            .get(0);
        List<PluginLibraryDetailResult> middlewareList = pluginLibraryDAO.queryMaxVersionPlugin(
            PluginTypeEnum.MIDDLEWARE.getCode());

        // 聚合包
        String completeAgentPath = AgentPkgUtil.aggregation(agent, simulator, middlewareList, uploadPath);
        if (StringUtils.isBlank(completeAgentPath)) {
            return;
        }

        // 如果当前版本数据库中已存在，则更新记录，并将原有的文件删除，反之则新增
        AgentVersionDetailResult agentVersionDetailResult = agentVersionDAO.selectByVersion(simulator.getVersion());
        if (agentVersionDetailResult == null) {
            CreateAgentVersionParam createParam = new CreateAgentVersionParam();
            createParam.setOperator("system");
            // 处理大版本号 完整的版本号为 5.0.0.3，则对应的大版本为 5.0
            String[] items = simulator.getVersion().split("\\.");
            if (items.length < 2) {
                throw AppCommonUtil.getCommonError("版本号格式错误！");
            }
            createParam.setFirstVersion(items[0] + "." + items[1]);
            createParam.setVersion(simulator.getVersion());
            createParam.setVersionNum(AgentVersionUtil.string2Int(createParam.getVersion()));
            createParam.setFilePath(completeAgentPath);
            createParam.setVersionFeatures(simulator.getUpdateDescription());
            agentVersionDAO.insert(createParam);
        } else {
            String oldFilePath = agentVersionDetailResult.getFilePath();
            UpdateAgentVersionParam updateParam = new UpdateAgentVersionParam();
            updateParam.setId(agentVersionDetailResult.getId());
            updateParam.setFilePath(completeAgentPath);
            agentVersionDAO.update(updateParam);
            FileUtil.deleteFile(oldFilePath);
        }
    }

    /**
     * 处理仿真系统配置数据
     *
     * @param configCreateRequestList 仿真系统配置入参
     * @param simulatorVersion        simulator版本
     */
    private void dealAgentConfig(List<AgentConfigCreateRequest> configCreateRequestList, String simulatorVersion) {
        if (CollectionUtils.isEmpty(configCreateRequestList) || StringUtils.isEmpty(simulatorVersion)) {
            return;
        }
        configCreateRequestList.forEach(item -> item.setEffectMinVersion(simulatorVersion));
        agentConfigService.batchInsert(configCreateRequestList);
    }

    /**
     * 数据入库
     *
     * @param pluginCreateBOList      拆包以后的数据
     * @param pluginCreateRequestList 前端提交的业务数据
     */
    private void save(List<PluginCreateBO> pluginCreateBOList, List<PluginCreateRequest> pluginCreateRequestList) {
        List<CreatePluginLibraryParam> libraryParams = new ArrayList<>();
        List<CreatePluginDependentParam> dependentParams = new ArrayList<>();
        for (PluginCreateBO pluginCreateBO : pluginCreateBOList) {
            for (PluginCreateRequest pluginCreateRequest : pluginCreateRequestList) {
                if (pluginCreateBO.getPluginName().equals(pluginCreateRequest.getPluginName())
                    && pluginCreateBO.getPluginVersion().equals(pluginCreateRequest.getPluginVersion())) {
                    CreatePluginLibraryParam libraryParam = new CreatePluginLibraryParam();
                    libraryParam.setPluginName(pluginCreateBO.getPluginName());
                    libraryParam.setPluginType(pluginCreateBO.getPluginType());
                    libraryParam.setVersion(pluginCreateBO.getPluginVersion());
                    libraryParam.setVersionNum(AgentVersionUtil.string2Int(pluginCreateBO.getPluginVersion()));
                    libraryParam.setIsCustomMode(pluginCreateBO.getIsCustomMode() ? 1 : 0);
                    libraryParam.setUpdateDescription(pluginCreateRequest.getUpdateInfo());
                    libraryParam.setDownloadPath(pluginCreateBO.getDownloadPath());
                    libraryParams.add(libraryParam);
                }
            }

            // 解析依赖
            List<AgentModuleInfo> agentModuleInfos = ModulePropertiesResolver.dealDependsInfo(
                pluginCreateBO.getDependenciesInfo());
            for (AgentModuleInfo agentModuleInfo : agentModuleInfos) {
                CreatePluginDependentParam dependentParam = new CreatePluginDependentParam();
                dependentParam.setPluginName(pluginCreateBO.getPluginName());
                dependentParam.setPluginVersion(pluginCreateBO.getPluginVersion());
                dependentParam.setPluginVersionNum(AgentVersionUtil.string2Int(pluginCreateBO.getPluginVersion()));
                dependentParam.setDependentPluginName(agentModuleInfo.getModuleId());
                dependentParam.setDependentPluginVersion(agentModuleInfo.getModuleVersion());
                dependentParam.setDependentPluginVersionNum(
                    AgentVersionUtil.string2Int(agentModuleInfo.getModuleVersion()));
                dependentParams.add(dependentParam);
            }

        }

        pluginLibraryDAO.batchInsert(libraryParams);
        pluginDependentDAO.batchInsert(dependentParams);
        // TODO ocean_wll 租户上线以后还得考虑租户 t_plugin_tenant_ref 插入数据

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

        List<PluginLibraryListResponse> listResList = CommonUtil.list2list(results, PluginLibraryListResponse.class);
        // 查询是否有依赖
        listResList.parallelStream().forEach(item -> item.setHasDependence(
            pluginDependentDAO.queryPluginDependentDetailList(item.getPluginName(), item.getVersion()).size() > 0));

        return PagingList.of(listResList, resultPage.getTotal());
    }

//    @Override
//    public List<PluginLibraryDetailResult> list(List<Long> pluginIds) {
//        return pluginLibraryDAO.list(pluginIds);
//    }


    @Override
    public List<PluginLibraryDetailResult> list(List<ApplicationPluginUpgradeRefDetailResult> paramList) {
        List<PluginLibraryDetailResult> list = new ArrayList<>();
        paramList.forEach(detail -> {
            list.add(pluginLibraryDAO.getOneByPluginNameAndVersion(detail.getPluginName(), detail.getPluginVersion()));
        });

        return list;
    }

    @Override
    public List<PluginLibraryListResponse> queryAllPlugin() {
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
        if (fileName.toLowerCase().startsWith("simulator-agent")) {
            return PluginTypeEnum.AGENT;
        } else if (fileName.toLowerCase().startsWith("simulator")) {
            return PluginTypeEnum.SIMULATOR;
        } else if (fileName.toLowerCase().startsWith("modules")) {
            return PluginTypeEnum.MIDDLEWARE;
        }
        return null;
    }

    @Override
    public Response<List<PluginInfo>> queryByPluginName(PluginAllowUpgradeLibraryListQueryRequest queryRequest) {
        List<Long> applications = queryRequest.getApplicationIds();
        /*
         * 确定应用的当前最高升级单和最高版本插件列表
         * 多节点应用可能存在多个版本
         * 这里只取最高的版本
         */

        List<AgentReportDetailResult> list = agentReportService.getList(applications);
        Map<Long, List<PluginLibraryDetailResult>> appPluginListMap = agentVersionService.findAppPluginList(list);

        Set<PluginLibraryDetailResult> pluginSet = new HashSet<>();
        appPluginListMap.forEach((k,v) -> pluginSet.addAll(v));
        Map<String, PluginLibraryDetailResult> pluginName2Detail = CollStreamUtil.toMap(pluginSet,
                PluginLibraryDetailResult::getPluginName,
                Function.identity());

        Long versionNum = 0L;
        if(pluginName2Detail.containsKey(queryRequest.getPluginName())){
             versionNum = pluginName2Detail.get(queryRequest.getPluginName()).getVersionNum();
        }
        List<PluginLibraryDetailResult> detailResults = pluginLibraryDAO
                .queryListByPluginNameAndGtVersion(queryRequest.getPluginName(), versionNum);

        /*
         *  确定需要一起升级的依赖插件
         */
        List<PluginInfo> pluginInfos = CommonUtil.list2list(detailResults, PluginInfo.class);
        List<PluginInfo> dependenciesInfos = new ArrayList<>();
        pluginInfos.forEach(pluginInfo -> {
            List<PluginDependentDetailResult> dependents = pluginDependentDAO
                    .queryPluginDependentDetailList(pluginInfo.getPluginName(), pluginInfo.getVersion());
            dependents.forEach(dependent ->{
                PluginLibraryDetailResult one = pluginLibraryDAO
                        .getOneByPluginNameAndVersion(dependent.getDependentPluginName(), dependent.getDependentPluginVersion());
                dependenciesInfos.add(Convert.convert(PluginInfo.class,one));
            });
            pluginInfo.setDependenciesInfos(dependenciesInfos);
        });

        //todo nf 租户隔离没加
        return Response.success(CommonUtil.list2list(detailResults, PluginInfo.class));
    }

    @Override
    public PluginLibraryDetailResult queryOneById(Long pluginId) {
        return  pluginLibraryDAO.queryById(pluginId);
    }

    @Override
    public List<PluginLibraryDetailResult> queryListByIds(List<Long> pluginIds) {
        if(CollectionUtils.isEmpty(pluginIds)){
            return Collections.emptyList();
        }
        return pluginLibraryDAO.queryListByIds(pluginIds);
    }
}
