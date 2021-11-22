package io.shulie.takin.web.biz.service.fastagentaccess.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.pamirs.takin.entity.dao.confcenter.TApplicationMntDao;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import com.pamirs.takin.entity.domain.query.ApplicationQueryParam;
import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.LoginConstant;
import io.shulie.takin.web.biz.pojo.bo.ConfigListQueryBO;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentInfoListQueryRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentVersionCreateRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentVersionQueryRequest;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentInfoListResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentVersionListResponse;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.agentupgradeonline.AgentReportService;
import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeRefService;
import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeService;
import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationTagRefService;
import io.shulie.takin.web.biz.service.agentupgradeonline.PluginLibraryService;
import io.shulie.takin.web.biz.service.fastagentaccess.AgentConfigService;
import io.shulie.takin.web.biz.service.fastagentaccess.AgentVersionService;
import io.shulie.takin.web.biz.utils.fastagentaccess.AgentDownloadUrlVerifyUtil;
import io.shulie.takin.web.biz.utils.fastagentaccess.AgentVersionUtil;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigEffectTypeEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentReportStatusEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentShowStatusEnum;
import io.shulie.takin.web.common.util.AppCommonUtil;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.fastagentaccess.AgentVersionDAO;
import io.shulie.takin.web.data.dao.scene.SceneBusinessActivityRefDAO;
import io.shulie.takin.web.data.param.fastagentaccess.AgentVersionQueryParam;
import io.shulie.takin.web.data.param.fastagentaccess.CreateAgentVersionParam;
import io.shulie.takin.web.data.result.agentUpgradeOnline.PluginLibraryDetailResult;
import io.shulie.takin.web.data.result.application.AgentConfigDetailResult;
import io.shulie.takin.web.data.result.application.AgentReportDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeRefDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationTagRefDetailResult;
import io.shulie.takin.web.data.result.fastagentaccess.AgentVersionDetailResult;
import io.shulie.takin.web.data.result.fastagentaccess.AgentVersionListResult;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * agent版本管理(AgentVersion)service
 *
 * @author liuchuan
 * @date 2021-08-11 19:43:38
 */
@Service
@Slf4j
public class AgentVersionServiceImpl implements AgentVersionService {

    /**
     * 安装脚本的路径
     */
    private final static String TEMPLATE_PATH = "/fastagentaccess/installAgentTemplate.sh";

    /**
     * agent下载的url模板
     */
    private final static String AGENT_DOWNLOAD_TEMPLATE
            = "%s/fast/agent/access/project/download?projectName=%s&userAppKey=%s&version=%s&expireDate=%s"
            + "&flag=%s";

    /**
     * userId字符串
     */
    private final static String PRADAR_USER_ID = "pradar.user.id";

    /**
     * userAppKey字符串
     */
    private final static String USER_APP_KEY = "user.app.key";

    @Autowired
    private AgentVersionDAO agentVersionDAO;

    @Autowired
    private AgentConfigService agentConfigService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationTagRefService tagRefService;

    @Autowired
    private ApplicationPluginUpgradeRefService pluginUpgradeRefService;

    @Autowired
    private ApplicationPluginUpgradeService pluginUpgradeService;

    @Autowired
    private SceneBusinessActivityRefDAO activityRefDAO;

    @Autowired
    private AgentReportService agentReportService;

    @Autowired
    private TApplicationMntDao mntDao;

    @Autowired
    private PluginLibraryService pluginLibraryService;

    @Override
    public AgentVersionListResponse queryLatestOrFixedVersion(String version) {
        AgentVersionDetailResult agentVersionDetailResult;
        if (StringUtils.isEmpty(version)) {
            agentVersionDetailResult = agentVersionDAO.findMaxVersionAgent();
        } else {
            agentVersionDetailResult = agentVersionDAO.selectByVersion(version);
        }
        if (agentVersionDetailResult == null) {
            return null;
        }
        AgentVersionListResponse agentVersionListResponse = new AgentVersionListResponse();
        BeanUtils.copyProperties(agentVersionDetailResult, agentVersionListResponse);
        return agentVersionListResponse;
    }

    @Override
    public void deleteByVersion(String version) {
        if (!StringUtils.hasLength(version)) {
            return;
        }
        agentVersionDAO.deleteByVersion(version);
    }

    @Override
    public Integer create(AgentVersionCreateRequest createRequest) {
        CreateAgentVersionParam createParam = new CreateAgentVersionParam();
        createParam.setOperator(
                WebPluginUtils.getUser() == null ? LoginConstant.DEFAULT_OPERATOR : WebPluginUtils.getUser().getName());
        BeanUtils.copyProperties(createRequest, createParam);
        // 处理大版本号 完整的版本号为 5.0.0.3，则对应的大版本为 5.0
        String[] items = createParam.getVersion().split("\\.");
        if (items.length < 2) {
            throw AppCommonUtil.getCommonError("版本号格式错误！");
        }
        createParam.setFirstVersion(items[0] + "." + items[1]);
        createParam.setVersionNum(AgentVersionUtil.string2Int(createParam.getVersion()));
        return agentVersionDAO.insert(createParam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void release(AgentVersionCreateRequest createRequest) {
        // 如果当前发布版本已存在则先删除
        if (createRequest.getExist()) {
            deleteByVersion(createRequest.getVersion());
        }
        create(createRequest);

        List<AgentConfigCreateRequest> configCreateRequestList = createRequest.getConfigList();
        // 新增配置信息
        if (!CollectionUtils.isEmpty(configCreateRequestList)) {
            // 因为废弃了最高版本，所以最低版本以当前agent版本为准
            configCreateRequestList.forEach(item -> item.setEffectMinVersion(createRequest.getVersion()));
            agentConfigService.batchInsert(createRequest.getConfigList());
        }
    }

    @Override
    public List<String> getFirstVersionList() {
        return agentVersionDAO.findFirstVersionList();
    }

    @Override
    public List<String> getAllVersionList() {
        return agentVersionDAO.findAllVersionList();
    }

    @Override
    public PagingList<AgentVersionListResponse> list(AgentVersionQueryRequest queryRequest) {

        AgentVersionQueryParam queryParam = new AgentVersionQueryParam();
        BeanUtils.copyProperties(queryRequest, queryParam);
        PagingList<AgentVersionListResult> resultPage = agentVersionDAO.page(queryParam);

        List<AgentVersionListResult> results = resultPage.getList();
        if (CollectionUtil.isEmpty(results)) {
            return PagingList.empty();
        }

        return PagingList.of(CommonUtil.list2list(results, AgentVersionListResponse.class), resultPage.getTotal());
    }

    @Override
    public File getFile(String version) {
        // 根据version查询对应的数据库记录
        AgentVersionDetailResult detailResult = agentVersionDAO.selectByVersion(version);
        if (detailResult == null) {
            return null;
        }
        return new File(detailResult.getFilePath());
    }

    @Override
    public File getProjectFile(String projectName, String userAppKey, String version) {
        // 1、获取对应版本的agent文件
        AgentVersionDetailResult detailResult = agentVersionDAO.selectByVersion(version);
        if (detailResult == null) {
            return null;
        }

        // 2、获取当前project对应的应用配置
        ConfigListQueryBO queryBO = new ConfigListQueryBO();
        queryBO.setProjectName(projectName);
        queryBO.setEffectMinVersionNum(AgentVersionUtil.string2Int(version));
        queryBO.setUserAppKey(userAppKey);
        Map<String, AgentConfigDetailResult> configMap = agentConfigService.getConfigList(queryBO);

        // 3、将对应的配置写入文件中
        List<AgentConfigDetailResult> agentConfig = new ArrayList<>();
        List<AgentConfigDetailResult> simulatorConfig = new ArrayList<>();
        configMap.values().forEach(item -> {
            if (AgentConfigEffectTypeEnum.AGENT.getVal().equals(item.getEffectType())) {
                agentConfig.add(item);
            } else if (AgentConfigEffectTypeEnum.PROBE.getVal().equals(item.getEffectType())) {
                simulatorConfig.add(item);
            }
        });

        // 4、特殊处理一下agentConfig,将当前用户的pradar.user.id和user.app.key写入配置
        dealAgentConfig(userAppKey, agentConfig);

        // 5、将替换后配置参数的文件进行返回
        return updateZipFile(detailResult.getFilePath(), agentConfig, simulatorConfig);
    }

    @Override
    public File getInstallScript(String projectName, String version, String urlPrefix) {
        File outputFile;
        try {
            outputFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".sh");
        } catch (IOException e) {
            return null;
        }
        try (InputStream inputStream = this.getClass().getResourceAsStream(TEMPLATE_PATH);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             FileOutputStream fos = new FileOutputStream(outputFile)
        ) {
            String downloadUrl = generatorDownLoadUrl(projectName, version, urlPrefix);
            String str;
            while ((str = reader.readLine()) != null) {
                if (str.contains("{|downloadUrl|}")) {
                    str = str.replace("{|downloadUrl|}", downloadUrl);
                } else if (str.contains("{|projectName|}")) {
                    str = str.replace("{|projectName|}", projectName);
                }
                fos.write((str + "\n").getBytes());
            }
        } catch (IOException e) {
            log.error("read installAgentTemplate error", e);
        }
        return outputFile;
    }

    /**
     * 生成agent下载URL
     *
     * @param projectName 应用名
     * @param version     agent版本
     * @param urlPrefix   http://domain:host
     * @return 下载的url
     */
    private String generatorDownLoadUrl(String projectName, String version, String urlPrefix) {
        // 获取一小时后的时间戳
        long expireDate = System.currentTimeMillis() + 60 * 60 * 1000;
        String userAppKey = WebPluginUtils.getTenantUserAppKey();
        String flag = AgentDownloadUrlVerifyUtil.generatorFlag(projectName, userAppKey, version, expireDate);
        urlPrefix = urlPrefix.endsWith("/") ? urlPrefix.substring(0, urlPrefix.length() - 1) : urlPrefix;
        return String.format(AGENT_DOWNLOAD_TEMPLATE, urlPrefix, projectName, userAppKey, version, expireDate, flag);
    }

    /**
     * 更新zip包中的配置信息
     *
     * @param inputFilePath   源文件路径
     * @param agentConfig     agent配置信息
     * @param simulatorConfig 探针配置信息
     * @return 需要下载的包
     */
    private File updateZipFile(String inputFilePath, List<AgentConfigDetailResult> agentConfig,
                               List<AgentConfigDetailResult> simulatorConfig) {

        ZipFile zipFile = null;
        File outputFile = null;
        ZipOutputStream zos = null;
        try {
            zipFile = new ZipFile(inputFilePath);
            outputFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".zip");
            // 复制为新zip
            zos = new ZipOutputStream(new FileOutputStream(outputFile));
            // 遍历所有文件复制
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements(); ) {
                ZipEntry entryIn = e.nextElement();
                boolean isAgentProperties = entryIn.getName().endsWith("config/agent.properties");
                boolean isSimulatorProperties = entryIn.getName().endsWith(
                        "agent/simulator/config/simulator.properties");
                // 如果不是配置文件则直接copy
                if (!isAgentProperties && !isSimulatorProperties) {
                    zos.putNextEntry(new ZipEntry(entryIn.getName()));
                    InputStream is = zipFile.getInputStream(entryIn);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) > 0) {
                        zos.write(buf, 0, Math.min(len, buf.length));
                    }
                } else if (isAgentProperties) {
                    // 重写agent.properties
                    writeFile(zos, entryIn, agentConfig);
                } else {
                    // 重写simulator.properties
                    writeFile(zos, entryIn, simulatorConfig);
                }
                zos.closeEntry();
            }
        } catch (IOException e) {
            log.error("generator project agent error", e);
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                log.error("close project agent error", e);
            }
        }
        return outputFile;
    }

    /**
     * 将配置数据写入文件中
     *
     * @param zos        ZipOutputStream流
     * @param entryIn    ZipEntry对象
     * @param configList 配置对象
     * @throws IOException i/o异常
     */
    private void writeFile(ZipOutputStream zos, ZipEntry entryIn, List<AgentConfigDetailResult> configList)
            throws IOException {
        zos.putNextEntry(new ZipEntry(entryIn.getName()));
        for (AgentConfigDetailResult detail : configList) {
            // 写入配置文件的数据格式为 # desc \n enkey = value \n
            String config = null;
            if (StringUtils.hasLength(detail.getDesc())) {
                config = "# " + detail.getDesc().replaceAll("\n", "") + "\n";
            }
            config += detail.getEnKey() + " = " + detail.getDefaultValue() + "\n";

            byte[] buf = config.getBytes();
            zos.write(buf, 0, buf.length);
        }
    }

    /**
     * 处理agentConfig将
     * pradar.user.id
     * user.app.key
     * 两个配置的值修改成当前用户信息
     *
     * @param userAppKey      用户唯一标识
     * @param agentConfigList AgentConfigDetailResult集合
     */
    private void dealAgentConfig(String userAppKey, List<AgentConfigDetailResult> agentConfigList) {
        UserExt userExt = WebPluginUtils.queryUserFromCache(userAppKey);
        if (userExt == null) {
            return;
        }
        agentConfigList.removeIf(detailResult -> PRADAR_USER_ID.equals(detailResult.getEnKey()) || USER_APP_KEY.equals(
                detailResult.getEnKey()));

        AgentConfigDetailResult pradarUserIdObj = new AgentConfigDetailResult();
        pradarUserIdObj.setDesc("pradar.user.id");
        pradarUserIdObj.setEnKey(PRADAR_USER_ID);
        pradarUserIdObj.setDefaultValue(String.valueOf(userExt.getId()));

        AgentConfigDetailResult userAppKeyObj = new AgentConfigDetailResult();
        userAppKeyObj.setDesc("user.app.key");
        userAppKeyObj.setEnKey(USER_APP_KEY);
        userAppKeyObj.setDefaultValue(userAppKey);

        agentConfigList.add(pradarUserIdObj);
        agentConfigList.add(userAppKeyObj);
    }

    /**
     * 查询列表数据
     *
     * @param queryRequest
     * @return
     */
    @Override
    public PagingList<AgentInfoListResponse> getList(AgentInfoListQueryRequest queryRequest) {

        ApplicationQueryParam param = Convert.convert(ApplicationQueryParam.class, queryRequest);
        //通过搜索条件过滤出需要的所有appId
        List<Long> applicationIds = this.buildParamAppIds(queryRequest);
        if (applicationIds == null) {
            //没有符合条件的应用
            return PagingList.of(Collections.emptyList(), 0);
        }
        param.setApplicationIds(applicationIds);
        Response<List<ApplicationVo>> applicationList = applicationService.getApplicationList(param);


        List<ApplicationVo> applicationMnts = applicationList.getData();
        if (CollectionUtils.isEmpty(applicationMnts)) {
            return PagingList.of(Collections.emptyList(), 0);
        }

        List<AgentInfoListResponse> list = new ArrayList<>();
        List<Long> appIds = new ArrayList<>();
        applicationMnts.stream().peek(mnt -> {
            appIds.add(Long.valueOf(mnt.getId()));
            AgentInfoListResponse response = new AgentInfoListResponse();
            response.setApplicationId(Long.valueOf(mnt.getId()));
            response.setApplicationName(mnt.getApplicationName());
            response.setGmtUpdate(mnt.getUpdateTime());
            response.setOwner(mnt.getUserName());
            response.setMntNodeNum(mnt.getNodeNum());
            //todo   按钮权限
            list.add(response);
        });
        List<ApplicationTagRefDetailResult> refDetailResults = tagRefService.getList(appIds);
        Map<Long, List<String>> tagGroupByAppIdMap = refDetailResults.stream()
                .collect(Collectors
                        .toMap(ApplicationTagRefDetailResult::getApplicationId, result -> {
                                    String tagName = result.getTagName();
                                    List<String> arr = new ArrayList<>();
                                    arr.add(tagName);
                                    return arr;
                                }, (oldValue, value) -> {
                                    oldValue.addAll(value);
                                    return oldValue;
                                }
                        ));


        List<Long> responseIds = CollStreamUtil.toList(list, AgentInfoListResponse::getApplicationId);
        List<AgentReportDetailResult> agentReportList = agentReportService.getList(responseIds);
        Map<Long, Map<Integer, List<AgentReportDetailResult>>> agentReportMap = CollStreamUtil.groupBy2Key(agentReportList,
                AgentReportDetailResult::getApplicationId, AgentReportDetailResult::getStatus);

        //查找当前探针所有节点存在的最高批次版本
        Map<Long, String> maxMainVersionMap = this.findMaxMainVersion(agentReportList);


        list.forEach(response -> {
            response.setTags(tagGroupByAppIdMap.get(response.getApplicationId()));
            Map<Integer, List<AgentReportDetailResult>> agentReportByAppIdMap = agentReportMap.get(response.getApplicationId());
            List<AgentReportDetailResult> runningAgents = agentReportByAppIdMap.get(AgentReportStatusEnum.RUNNING.getVal());
            List<AgentReportDetailResult> errorAgents = agentReportByAppIdMap.get(AgentReportStatusEnum.ERROR.getVal());
            List<AgentReportDetailResult> sleepAgents = agentReportByAppIdMap.get(AgentReportStatusEnum.SLEEP.getVal());
            List<AgentReportDetailResult> waitRestartAgents = agentReportByAppIdMap.get(AgentReportStatusEnum.WAIT_RESTART.getVal());

            AgentInfoListResponse.AgentStateInfo agentStateInfo = new AgentInfoListResponse.AgentStateInfo(
                    agentReportByAppIdMap.size(),
                    errorAgents.size(),
                    waitRestartAgents.size(),
                    sleepAgents.size(),
                    runningAgents.size(),
                    runningAgents.get(0).getAgentId()
            );
            response.setAgentState(agentStateInfo);
            response.setVersion(maxMainVersionMap.get(response.getApplicationId()));
        });

        return PagingList.of(list, applicationIds.size());
    }


    private List<Long> buildParamAppIds(AgentInfoListQueryRequest queryRequest) {
        List<Long> applicationIds = new ArrayList<>();

        //按插件查
        if (!StringUtils.isEmpty(queryRequest.getPluginId())) {
            //List<ApplicationPluginUpgradeRefDetailResult> list = pluginUpgradeRefService.getList(queryRequest.getPluginId());
            List<ApplicationPluginUpgradeRefDetailResult> list = new ArrayList<>();

            List<String> upgradeBatchs = CollStreamUtil.toList(list, ApplicationPluginUpgradeRefDetailResult::getUpgradeBatch);
            Set<String> set = new HashSet<>(upgradeBatchs);
            List<ApplicationPluginUpgradeDetailResult> upgradeDetails = pluginUpgradeService.getList(set);
            applicationIds = CollStreamUtil.toList(upgradeDetails, ApplicationPluginUpgradeDetailResult::getApplicationId);
            if (CollectionUtils.isEmpty(applicationIds)) {
                return null;
            }

            applicationIds.addAll(CollStreamUtil.toList(upgradeDetails, ApplicationPluginUpgradeDetailResult::getApplicationId));
        }

        //按标签查
        if (!StringUtils.isEmpty(queryRequest.getTagId())) {
            List<ApplicationTagRefDetailResult> list = tagRefService.getList(queryRequest.getTagId());
            List<Long> ids = CollStreamUtil.toList(list, ApplicationTagRefDetailResult::getApplicationId);
            if (CollectionUtils.isEmpty(ids)) {
                return null;
            }

            applicationIds = (List<Long>) CollUtil.intersection(applicationIds,
                    CollStreamUtil.toList(list, ApplicationTagRefDetailResult::getApplicationId));
        }

        //按业务活动查
        if (!StringUtils.isEmpty(queryRequest.getBusinessActivityId())) {
            List<Long> ids = activityRefDAO.getAppIdList(queryRequest.getBusinessActivityId());
            if (CollectionUtils.isEmpty(ids)) {
                return null;
            }
            applicationIds = (List<Long>) CollUtil.intersection(applicationIds, ids);

        }

        //按接入状态查
        if (Objects.nonNull(queryRequest.getAccessStatus())) {
            Map<Long, Integer> appId2Count = agentReportService.appId2Count();
            Integer accessStatus = queryRequest.getAccessStatus();
            if (AgentShowStatusEnum.ERROR.getVal().equals(accessStatus)) {
                List<Integer> statusList = Collections.singletonList(accessStatus);
                List<AgentReportDetailResult> listByStatus = agentReportService.getListByStatus(statusList);
                List<TApplicationMnt> list = mntDao.getListByAppIdAndNotEqualsNodeNum(appId2Count);
                return this.intersection(applicationIds, listByStatus, list);

            }

            if (AgentShowStatusEnum.NORMAL.getVal().equals(accessStatus)) {
                List<Integer> statusList = Arrays.asList(
                        AgentReportStatusEnum.RUNNING.getVal(),
                        AgentReportStatusEnum.SLEEP.getVal(),
                        AgentReportStatusEnum.WAIT_RESTART.getVal(),
                        AgentReportStatusEnum.UNINSTALL.getVal()
                );
                List<AgentReportDetailResult> listByStatus = agentReportService.getListByStatus(statusList);
                List<TApplicationMnt> list = mntDao.getListByAppIdAndEqualsNodeNum(appId2Count);
                return this.intersection(applicationIds, listByStatus, list);
            }

            if (AgentShowStatusEnum.UPGRADE.getVal().equals(accessStatus)) {
                List<ApplicationPluginUpgradeDetailResult> listByStatus = pluginUpgradeService.getListByStatus(0);
                List<Long> ids = CollStreamUtil.toList(listByStatus, ApplicationPluginUpgradeDetailResult::getApplicationId);
                return (List<Long>) CollUtil.intersection(applicationIds, ids);
            }
        }
        return applicationIds;
    }


    public Map<Long, String> findMaxMainVersion(List<AgentReportDetailResult> agentReportList) {
        Map<Long, String> appId2NewVersion = new HashMap<>();
        Map<Long, List<PluginLibraryDetailResult>> appPluginList = this.findAppPluginList(agentReportList);
        appPluginList.forEach((appId, plugins) -> {
            plugins.forEach(plugin -> {
                if (plugin.getPluginType() == 1) {
                    appId2NewVersion.put(appId, plugin.getVersion());
                }
            });
        });
        return appId2NewVersion;
    }

    @Override
    public Map<Long, List<PluginLibraryDetailResult>> findAppPluginList(List<AgentReportDetailResult> agentReportList) {
        //查找当前探针所有节点存在的最高批次版本
        Map<Long, List<AgentReportDetailResult>> appId2Detail = CollStreamUtil.groupByKey(agentReportList,
                AgentReportDetailResult::getApplicationId);

        Map<Long, String> appId2UpgradeBatch = new HashMap<>();
        Map<Long, List<PluginLibraryDetailResult>> appId2Plugins = new HashMap<>();
        appId2Detail.forEach((k, v) -> {
            List<String> upgradeBatchs = CollStreamUtil.toList(v, AgentReportDetailResult::getCurUpgradeBatch);
            Set<String> set = new HashSet<>(upgradeBatchs);
            List<ApplicationPluginUpgradeDetailResult> upgradeDetails = pluginUpgradeService.getList(set);
            ApplicationPluginUpgradeDetailResult detail = upgradeDetails.stream()
                    .max(Comparator.comparing(ApplicationPluginUpgradeDetailResult::getId)).get();
            appId2UpgradeBatch.put(k, detail.getUpgradeBatch());
        });

        appId2UpgradeBatch.forEach((k, v) -> {
            List<ApplicationPluginUpgradeRefDetailResult> upgradeRefs = pluginUpgradeRefService.getList(v);
            //List<Long> pluginIds = CollStreamUtil.toList(upgradeRefs, ApplicationPluginUpgradeRefDetailResult::getPluginId);
            List<Long> pluginIds = new ArrayList<>();
            List<PluginLibraryDetailResult> plugins = pluginLibraryService.list(pluginIds);
            appId2Plugins.put(k, plugins);
        });
        return appId2Plugins;
    }


    private List<Long> intersection(List<Long> applicationIds, List<AgentReportDetailResult> listByStatus,
                                    List<TApplicationMnt> list) {
        List<Long> ids = CollStreamUtil.toList(list, TApplicationMnt::getApplicationId);
        ids.addAll(CollStreamUtil.toList(listByStatus, AgentReportDetailResult::getApplicationId));

        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        return (List<Long>) CollUtil.intersection(applicationIds, ids);
    }


//    @Override
//    public List<SelectVO> getAccessStatusList() {
//        return Arrays.stream(AgentShowStatusEnum.values()).map(
//                statusEnum -> new SelectVO(statusEnum.getDesc(), String.valueOf(statusEnum.getVal()))
//        ).collect(Collectors.toList());
//    }
}
