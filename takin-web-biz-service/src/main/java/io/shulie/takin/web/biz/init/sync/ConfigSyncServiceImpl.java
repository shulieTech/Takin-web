package io.shulie.takin.web.biz.init.sync;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.alibaba.excel.util.StringUtils;

import com.google.common.collect.Lists;
import com.pamirs.takin.entity.dao.confcenter.TBListMntDao;
import com.pamirs.takin.entity.domain.entity.DsModelWithBLOBs;
import com.pamirs.takin.entity.domain.entity.LinkGuardEntity;
import com.pamirs.takin.entity.domain.entity.TBList;
import com.pamirs.takin.entity.domain.entity.TWList;
import com.pamirs.takin.entity.domain.entity.configs.Configurations;
import com.pamirs.takin.entity.domain.entity.configs.DataSource;
import com.pamirs.takin.entity.domain.entity.simplify.TShadowJobConfig;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.pojo.output.application.ShadowConsumerOutput;
import io.shulie.takin.web.biz.service.ShadowConsumerService;
import io.shulie.takin.web.biz.service.config.ConfigService;
import io.shulie.takin.web.biz.service.dsManage.DsService;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
import io.shulie.takin.web.biz.service.linkmanage.LinkGuardService;
import io.shulie.takin.web.biz.service.linkmanage.WhiteListService;
import io.shulie.takin.web.biz.service.simplify.ShadowJobConfigService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.config.entity.AllowList;
import io.shulie.takin.web.config.entity.Guard;
import io.shulie.takin.web.config.entity.ShadowConsumer;
import io.shulie.takin.web.config.entity.ShadowDB;
import io.shulie.takin.web.config.entity.ShadowJob;
import io.shulie.takin.web.config.enums.AllowListType;
import io.shulie.takin.web.config.enums.BlockListType;
import io.shulie.takin.web.config.enums.ShadowConsumerType;
import io.shulie.takin.web.config.enums.ShadowDSType;
import io.shulie.takin.web.config.enums.ShadowJobType;
import io.shulie.takin.web.config.sync.api.AllowListSyncService;
import io.shulie.takin.web.config.sync.api.BlockListSyncService;
import io.shulie.takin.web.config.sync.api.GuardSyncService;
import io.shulie.takin.web.config.sync.api.ShadowConsumerSyncService;
import io.shulie.takin.web.config.sync.api.ShadowDbSyncService;
import io.shulie.takin.web.config.sync.api.ShadowJobSyncService;
import io.shulie.takin.web.config.sync.api.SwitchSyncService;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
@Service
@Slf4j
public class ConfigSyncServiceImpl implements ConfigSyncService {

    @Autowired
    private GuardSyncService guardSyncService;
    @Autowired
    private SwitchSyncService switchSyncService;
    @Autowired
    private AllowListSyncService allowListSyncService;
    @Autowired
    private ShadowJobSyncService shadowJobSyncService;
    @Autowired
    private ShadowConsumerSyncService shadowConsumerSyncService;
    @Autowired
    private BlockListSyncService blockListSyncService;
    @Autowired
    private ShadowDbSyncService shadowDbSyncService;
    @Autowired
    private LinkGuardService linkGuardService;
    @Resource
    private ApplicationDAO applicationDAO;
    @Autowired
    private ConfigService configService;
    @Autowired
    private WhiteListService whiteListService;
    @Autowired
    private ShadowJobConfigService shadowJobConfigService;
    @Autowired
    private DsService dsService;
    @Resource
    private TBListMntDao tbListMntDao;

    @Autowired
    private ShadowConsumerService shadowConsumerService;

    @PostConstruct
    public void init() {
    }

    /**
     * TODO 未来应用从AMDB查询，applicationId 可能是根据规则生成的字符串
     */
    @Override
    public void syncGuard(TenantCommonExt commonExt, long applicationId, String applicationName) {
        if (StringUtils.isEmpty(applicationName)) {
            ApplicationDetailResult tApplicationMnt = applicationDAO.getApplicationById(applicationId);
            applicationName = tApplicationMnt.getApplicationName();
        }
        guardSyncService.syncGuard(commonExt, applicationName, queryAndParseGuard(applicationId));
    }

    private List<Guard> queryAndParseGuard(long applicationId) {
        List<LinkGuardEntity> dbGuards = linkGuardService.getAllEnabledGuard(String.valueOf(applicationId));
        if (CollectionUtils.isEmpty(dbGuards)) {
            return Lists.newArrayList();
        }
        List<Guard> guards = dbGuards.stream().map(db -> {
            Guard guard = new Guard();
            guard.setId(db.getId());
            String methodInfo = db.getMethodInfo();
            String[] methodInfoArray = methodInfo.split("#");
            String classname = methodInfoArray.length == 2 ? methodInfoArray[0] : db.getMethodInfo();
            String method = methodInfoArray.length == 2 ? methodInfoArray[1] : db.getMethodInfo();
            guard.setClassName(classname);
            guard.setMethodName(method);
            guard.setCodeScript(db.getGroovy());
            return guard;
        }).sorted(Comparator.comparing(Guard::getId)).collect(Collectors.toList());
        return guards;
    }

    @Override
    public void syncClusterTestSwitch(TenantCommonExt commonExt) {
        boolean clusterTestSwitch = configService.getClusterTestSwitch(commonExt);
        switchSyncService.turnClusterTestSwitch(commonExt, clusterTestSwitch);
    }

    @Override
    public void syncAllowListSwitch(TenantCommonExt commonExt) {
        boolean clusterTestSwitch = configService.getAllowListSwitch(commonExt);
        switchSyncService.turnAllowListSwitch(commonExt, clusterTestSwitch);

    }

    @Override
    public void syncAllowList(TenantCommonExt commonExt, long applicationId, String applicationName) {
        if (StringUtils.isEmpty(applicationName)) {
            ApplicationDetailResult tApplicationMnt = applicationDAO.getApplicationById(applicationId);
            if (tApplicationMnt == null) {
                throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_WHITELIST_VALIDATE_ERROR, "应用不存在!");
            }

            applicationName = tApplicationMnt.getApplicationName();
        }

        allowListSyncService.syncAllowList(commonExt, applicationName, queryAndParseAllowList(applicationId));
    }

    private List<AllowList> queryAndParseAllowList(long applicationId) {
        List<TWList> allEnableWhitelists = whiteListService.getAllEnableWhitelists(String.valueOf(applicationId));
        if (CollectionUtils.isEmpty(allEnableWhitelists)) {
            return Lists.newArrayList();
        }
        List<AllowList> allowLists = allEnableWhitelists.stream().map(db -> {
            AllowList allowList = new AllowList();
            allowList.setId(db.getWlistId());
            allowList.setInterfaceName(db.getInterfaceName());
            String type = db.getType();
            try {
                allowList.setType(getType(Integer.parseInt(type)));
            } catch (Exception e) {
                log.error("AllowList Type Parse Exception: {}", e.getMessage());
            }
            return allowList;
        }).sorted(Comparator.comparing(AllowList::getId)).collect(Collectors.toList());
        return allowLists;
    }

    private AllowListType getType(int dbType) {
        AllowListType type;
        switch (dbType) {
            case 1:
                type = AllowListType.HTTP;
                break;
            case 2:
                type = AllowListType.DUBBO;
                break;
            case 3:
                type = AllowListType.RABBITMQ;
                break;
            default:
                type = AllowListType.UNKNOW;
                break;
        }
        return type;
    }

    @Override
    public void syncShadowJob(TenantCommonExt commonExt, long applicationId, String applicationName) {
        if (StringUtils.isEmpty(applicationName)) {
            ApplicationDetailResult tApplicationMnt = applicationDAO.getApplicationById(applicationId);
            applicationName = tApplicationMnt.getApplicationName();
        }
        shadowJobSyncService.syncShadowJob(commonExt, applicationName, queryAndParseShadowJob(applicationId));
    }

    private List<ShadowJob> queryAndParseShadowJob(long applicationId) {
        List<TShadowJobConfig> allEnableShadowJobs = shadowJobConfigService.getAllEnableShadowJobs(applicationId);
        if (CollectionUtils.isEmpty(allEnableShadowJobs)) {
            return Lists.newArrayList();
        }
        List<ShadowJob> shadowJobs = allEnableShadowJobs.stream().map(db -> {
            ShadowJob shadowJob = new ShadowJob();
            shadowJob.setId(db.getId());
            Integer type = db.getType();
            shadowJob.setType(
                type == 0 ?
                    ShadowJobType.QUARTZ :
                    type == 1 ?
                        ShadowJobType.ELASTIC_JOB :
                        type == 2 ?
                            ShadowJobType.XXL_JOB :
                            ShadowJobType.UNKNOWN
            );
            String xmlConfigStr = db.getConfigCode();
            SAXReader reader = new SAXReader();
            try {
                Document document = reader.read(new ByteArrayInputStream(xmlConfigStr.getBytes()));
                Element root = document.getRootElement();
                Element classnameE = root.element("className");
                Element jobDataTypeE = root.element("jobDataType");
                shadowJob.setClassName(classnameE.getText());
                shadowJob.setJobDataType(jobDataTypeE.getText());

                Element cronE = root.element("cron");
                if(cronE != null) {
                    // 前端可能不配cron
                    shadowJob.setCron(cronE.getText());
                }
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
            return shadowJob;
        }).sorted(Comparator.comparing(ShadowJob::getId)).collect(Collectors.toList());
        return shadowJobs;
    }

    @Override
    public void syncShadowDB(TenantCommonExt commonExt, long applicationId, String applicationName) {
        if (StringUtils.isEmpty(applicationName)) {
            ApplicationDetailResult tApplicationMnt = applicationDAO.getApplicationById(applicationId);
            applicationName = tApplicationMnt.getApplicationName();
        }
        shadowDbSyncService.syncShadowDataBase(commonExt, applicationName, queryAndParseShadowDatabase(applicationId));
    }

    @Override
    public void syncShadowConsumer(TenantCommonExt commonExt, long applicationId, String applicationName) {
        if (StringUtils.isEmpty(applicationName)) {
            ApplicationDetailResult tApplicationMnt = applicationDAO.getApplicationById(applicationId);
            applicationName = tApplicationMnt.getApplicationName();
        }
        shadowConsumerSyncService.syncShadowConsumer(commonExt, applicationName,
            queryAndParseShadowConsumer(applicationId));
    }

    private List<ShadowConsumer> queryAndParseShadowConsumer(long applicationId) {
        List<ShadowConsumerOutput> consumerResponses = shadowConsumerService
            .getShadowConsumersByApplicationId(applicationId);
        if (CollectionUtils.isEmpty(consumerResponses)) {
            return Lists.newArrayList();
        }
        return consumerResponses.stream()
            .filter(ShadowConsumerOutput::getEnabled)
            .map(consumer -> {
                ShadowConsumer shadowConsumer = new ShadowConsumer();
                String[] split = consumer.getTopicGroup().split("#");
                shadowConsumer.setGroup(split[1]);
                shadowConsumer.setTopic(split[0]);
                shadowConsumer.setId(consumer.getId());
                shadowConsumer.setType(ShadowConsumerType.of(consumer.getType().name()));
                return shadowConsumer;
            }).collect(Collectors.toList());
    }

    private List<ShadowDB> queryAndParseShadowDatabase(long applicationId) {
        List<DsModelWithBLOBs> allEnableDbConfigs = dsService.getAllEnabledDbConfig(applicationId);
        if (CollectionUtils.isEmpty(allEnableDbConfigs)) {
            return Lists.newArrayList();
        }
        List<ShadowDB> shadowDatabaseList = allEnableDbConfigs.stream().map(config -> {
            ShadowDB shadowDatabase = new ShadowDB();
            shadowDatabase.setId(config.getId());
            Byte type = config.getDsType();
            if (type == 0) {
                shadowDatabase.setType(ShadowDSType.SCHEMA);
            } else if (type == 1) {
                shadowDatabase.setType(ShadowDSType.TABLE);
            } else if (type == 2) {
                shadowDatabase.setType(ShadowDSType.SERVER);
            }
            shadowDatabase.setBizJdbcUrl(config.getUrl());
            if (shadowDatabase.getType() == ShadowDSType.TABLE) {
                String tables = config.getConfig();
                if (!StringUtils.isEmpty(tables)) {
                    ShadowDB.ShadowTableConfig shadowTableConfig = new ShadowDB.ShadowTableConfig();
                    shadowTableConfig.setTableNames(Arrays.asList(tables.split(",")));
                    shadowDatabase.setShadowTableConfig(shadowTableConfig);
                }
            } else if (shadowDatabase.getType() == ShadowDSType.SCHEMA) {
                if (!StringUtils.isEmpty(config.getParseConfig())) {
                    Configurations configurations = JsonHelper.json2Bean(config.getParseConfig(), Configurations.class);
                    List<DataSource> dataSourceList = configurations.getDataSources();
                    if (CollectionUtils.isNotEmpty(dataSourceList)) {
                        String bizUsername = dataSourceList
                            .stream()
                            .filter(dataSource -> "dataSourceBusiness".equals(dataSource.getId()))
                            .map(DataSource::getUsername)
                            .collect(Collectors.joining());
                        shadowDatabase.setBizUserName(bizUsername);
                        ShadowDB.ShadowSchemaConfig shadowSchemaConfig = new ShadowDB.ShadowSchemaConfig();
                        Optional<DataSource> optional = dataSourceList
                            .stream()
                            .filter(dataSource -> "dataSourcePerformanceTest".equals(dataSource.getId())).findFirst();
                        if (optional.isPresent()) {
                            DataSource ptDatasource = optional.get();
                            BeanUtils.copyProperties(ptDatasource, shadowSchemaConfig);
                            shadowDatabase.setShadowSchemaConfig(shadowSchemaConfig);
                        }
                    }
                }
            } else if (shadowDatabase.getType() == ShadowDSType.SERVER) {

            }
            return shadowDatabase;
        }).sorted(Comparator.comparing(ShadowDB::getId)).collect(Collectors.toList());
        return shadowDatabaseList;
    }

    @Override
    public void syncBlockList(TenantCommonExt commonExt) {
        blockListSyncService.syncBlockList(commonExt, BlockListType.CACHE, queryAndParseShadowDatabase(commonExt));
    }

    private List<String> queryAndParseShadowDatabase(TenantCommonExt commonExt) {
        List<TBList> allEnabledBlockList = tbListMntDao.getAllEnabledBlockList(commonExt.getTenantId(),commonExt.getEnvCode());
        if (CollectionUtils.isEmpty(allEnabledBlockList)) {
            return Lists.newArrayList();
        }
        List<String> blockLists = allEnabledBlockList.stream().map(TBList::getRedisKey).collect(Collectors.toList());
        return blockLists;
    }

}
