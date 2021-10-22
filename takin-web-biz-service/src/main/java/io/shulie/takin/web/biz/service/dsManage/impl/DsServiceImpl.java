package io.shulie.takin.web.biz.service.dsManage.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.pamirs.attach.plugin.dynamic.Converter;
import com.pamirs.attach.plugin.dynamic.Type;
import com.pamirs.attach.plugin.dynamic.template.RedisTemplate;
import com.pamirs.takin.common.enums.ds.DbTypeEnum;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import com.pamirs.takin.common.enums.ds.MiddleWareTypeEnum;
import com.pamirs.takin.entity.dao.simplify.TAppBusinessTableInfoMapper;
import com.pamirs.takin.entity.domain.entity.DsModelWithBLOBs;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import com.pamirs.takin.entity.domain.entity.simplify.AppBusinessTableInfo;
import com.pamirs.takin.entity.domain.query.agent.AppBusinessTableQuery;
import com.pamirs.takin.entity.domain.vo.dsmanage.Configurations;
import com.pamirs.takin.entity.domain.vo.dsmanage.DataSource;
import com.pamirs.takin.entity.domain.vo.dsmanage.DatasourceMediator;
import com.pamirs.takin.entity.domain.vo.dsmanage.DsAgentVO;
import com.pamirs.takin.entity.domain.vo.dsmanage.DsServerVO;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.result.application.AppShadowDatabaseDTO;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.convert.db.parser.AbstractTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.DbTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.RedisTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.TemplateParser;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInputV2;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsDeleteInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsEnableInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInputV2;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationDsDetailOutput;
import io.shulie.takin.web.biz.pojo.output.application.ShadowServerConfigurationOutput;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationDsResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationDsV2Response;
import io.shulie.takin.web.biz.pojo.response.application.DbTypeResponse;
import io.shulie.takin.web.biz.pojo.response.application.DsTypeResponse;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.dsManage.AbstractDsService;
import io.shulie.takin.web.biz.service.dsManage.AbstractDsTemplateService;
import io.shulie.takin.web.biz.service.dsManage.AbstractShaDowManageService;
import io.shulie.takin.web.biz.service.dsManage.DsService;
import io.shulie.takin.web.biz.service.dsManage.impl.v2.ShaDowCacheServiceImpl;
import io.shulie.takin.web.biz.service.dsManage.impl.v2.ShaDowDbServiceImpl;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDsCacheManageDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDsDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbManageDAO;
import io.shulie.takin.web.data.param.application.ApplicationDsDeleteParam;
import io.shulie.takin.web.data.param.application.ApplicationDsEnableParam;
import io.shulie.takin.web.data.param.application.ApplicationDsQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationDsUpdateParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDsCacheManageDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDsDbManageDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDsResult;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fanxx
 * @date 2020/3/12 下午3:40
 */
@Service
@Slf4j
public class DsServiceImpl implements DsService {

    @Resource
    private TAppBusinessTableInfoMapper tAppBusinessTableInfoMapper;

    @Autowired
    private ApplicationDsDAO applicationDsDAO;

    private Map<DsTypeEnum, AbstractDsService> map;

    @Autowired
    private ShadowDbServiceImpl shadowDbService;

    @Autowired
    private ShadowTableServiceImpl shadowTableService;

    @Autowired
    private ShadowRedisServerServiceImpl shadowRedisServerService;

    @Autowired
    private ShadowEsServiceImpl shadowEsService;

    @Autowired
    private ShadowHbaseServiceImpl shadowHbaseService;

    @Autowired
    private ShadowKafkaServiceImpl shadowKafkaService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ApplicationClient applicationClient;

    @Autowired
    private ApplicationDsCacheManageDAO dsCacheManageDAO;

    @Autowired
    private ApplicationDsDbManageDAO dsDbManageDAO;

    @Autowired
    private DsCacheTemplateServiceImpl dsCacheTemplateService;

    @Autowired
    private DsDbTemplateServiceImpl dsDbTemplateService;

    @Autowired
    private ShaDowDbServiceImpl shaDowDbService;

    @Autowired
    private ShaDowCacheServiceImpl shaDowCacheService;

    @Autowired
    private RedisTemplateParser redisTemplateParser;

    @Autowired
    private DbTemplateParser dbTemplateParser;

    @Autowired
    private AgentConfigCacheManager agentConfigCacheManager;


    @PostConstruct
    public void init() {
        map = new HashMap<>(6);
        map.put(DsTypeEnum.SHADOW_DB, shadowDbService);
        map.put(DsTypeEnum.SHADOW_TABLE, shadowTableService);
        map.put(DsTypeEnum.SHADOW_REDIS_SERVER, shadowRedisServerService);
        map.put(DsTypeEnum.SHADOW_ES_SERVER, shadowEsService);
        map.put(DsTypeEnum.SHADOW_HBASE_SERVER, shadowHbaseService);
        map.put(DsTypeEnum.SHADOW_KAFKA_CLUSTER, shadowKafkaService);

        templateServiceMap = new HashMap<>(6);
        templateServiceMap.put(Type.MiddleWareType.CACHE.value(), dsCacheTemplateService);
        templateServiceMap.put(Type.MiddleWareType.LINK_POOL.value(), dsDbTemplateService);

        shaDowServiceMap = new HashMap<>(6);
        shaDowServiceMap.put(MiddleWareTypeEnum.CACHE.getCode(), shaDowCacheService);
        shaDowServiceMap.put(MiddleWareTypeEnum.DB.getCode(), shaDowDbService);
        shaDowServiceMap.put(MiddleWareTypeEnum.LINK_POOL.getCode(), shaDowDbService);

        templateParserMap = new HashMap<>(6);
        templateParserMap.put(Type.MiddleWareType.LINK_POOL, dbTemplateParser);
        templateParserMap.put(Type.MiddleWareType.CACHE, redisTemplateParser);

    }


    private Map<String, AbstractDsTemplateService> templateServiceMap;

    private Map<Integer, AbstractShaDowManageService> shaDowServiceMap;

    private Map<Type, AbstractTemplateParser> templateParserMap;


    @Override
    public Response dsUpdate(ApplicationDsUpdateInput updateRequest) {
        if (updateRequest == null) {
            return Response.fail("updateRequest obj is null");
        }
        AbstractDsService dsService = getAbstractDsService(updateRequest.getDsType());
        if (dsService == null) {
            return Response.fail("dsService obj is null");
        }
        return dsService.dsUpdate(updateRequest);
    }

    @Override
    public Response dsQuery(Long applicationId) {
        if (null == applicationId) {
            return Response.fail("0", "参数缺失");
        }
        ApplicationDsQueryParam queryParam = new ApplicationDsQueryParam();
        queryParam.setApplicationId(applicationId);
        queryParam.setIsDeleted(0);
        WebPluginUtils.fillQueryParam(queryParam);
        List<ApplicationDsResult> dsResultList = applicationDsDAO.queryList(queryParam);
        List<ApplicationDsResponse> dsResponseList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(dsResultList)) {
            dsResponseList = dsResultList.stream().map(dsResult -> {
                ApplicationDsResponse dsResponse = new ApplicationDsResponse();
                dsResponse.setId(dsResult.getId());
                dsResponse.setApplicationId(dsResult.getApplicationId());
                DbTypeResponse dbTypeResponse = new DbTypeResponse();
                dbTypeResponse.setLabel(DbTypeEnum.getDescByCode(dsResult.getDbType()));
                dbTypeResponse.setValue(dsResult.getDbType());
                dsResponse.setDbType(dbTypeResponse);

                DsTypeResponse dsTypeResponse = new DsTypeResponse();
                dsTypeResponse.setLabel(DsTypeEnum.getDescByCode(dsResult.getDsType()));
                dsTypeResponse.setValue(dsResult.getDsType());
                dsResponse.setDsType(dsTypeResponse);
                dsResponse.setUrl(dsResult.getUrl());
                dsResponse.setStatus(dsResult.getStatus());
                dsResponse.setUpdateTime(dsResult.getUpdateTime());
                dsResponse.setUserId(dsResult.getUserId());
                return dsResponse;
            }).collect(Collectors.toList());
        }
        return Response.success(dsResponseList);
    }

    @Override
    public Response<ApplicationDsDetailOutput> dsQueryDetail(Long dsId, boolean isOldVersion) {
        ApplicationDsResult dsResult = applicationDsDAO.queryByPrimaryKey(dsId);
        if (dsResult == null) {
            return Response.fail("dataSource obj is null");
        }
        AbstractDsService dsService = getAbstractDsService(dsResult.getDsType());
        if (dsService == null) {
            return Response.fail("dsService obj is null");
        }
        return dsService.dsQueryDetail(dsId, isOldVersion);
    }

    @Override
    public Response enableConfig(ApplicationDsEnableInput enableRequest) {
        ApplicationDsResult dsResult = applicationDsDAO.queryByPrimaryKey(enableRequest.getId());
        if (Objects.isNull(dsResult)) {
            return Response.fail("0", "该配置不存在");
        }

        AbstractDsService dsService = getAbstractDsService(dsResult.getDsType());
        if (dsService == null) {
            return Response.fail("dsService obj is null");
        }
        return dsService.enableConfig(enableRequest);
    }

    private AbstractDsService getAbstractDsService(Integer code) {
        DsTypeEnum dsTypeEnum = DsTypeEnum.getEnumByCode(code);
        return map.get(dsTypeEnum);
    }

    @Override
    public List<DsAgentVO> getConfigs(String appName) {
        List<DsAgentVO> dsAgentVOList = new ArrayList<>();
        TApplicationMnt applicationMnt = applicationService.queryTApplicationMntByName(appName);
        if (applicationMnt != null) {
            List<DsModelWithBLOBs> dsModels = applicationDsDAO.selectByAppIdForAgent(applicationMnt.getApplicationId());
            if (CollectionUtils.isNotEmpty(dsModels)) {
                dsModels = dsModels.stream()
                        .filter(
                                dsModel -> dsModel.getDsType().equals(new Byte(String.valueOf(DsTypeEnum.SHADOW_DB.getCode())))
                                        || dsModel.getDsType().equals(new Byte(String.valueOf(DsTypeEnum.SHADOW_TABLE.getCode()))))
                        .collect(Collectors.toList());
                for (DsModelWithBLOBs dsModel : dsModels) {
                    DsAgentVO vo = new DsAgentVO();
                    vo.setApplicationName(dsModel.getApplicationName());
                    vo.setDsType(dsModel.getDsType());
                    vo.setStatus(dsModel.getStatus());
                    vo.setUrl(dsModel.getUrl());
                    if (dsModel.getDsType() == 0) {
                        //影子库
                        if (dsModel.getParseConfig() != null && !dsModel.getParseConfig().isEmpty()) {
                            vo.setShadowDbConfig(JSON.parseObject(dsModel.getParseConfig(), Configurations.class));
                        }
                    } else if (dsModel.getDsType() == 1) {
                        //影子表
                        vo.setShadowTableConfig(dsModel.getConfig());
                    }
                    dsAgentVOList.add(vo);
                }
            }

            List<DsAgentVO> newList = this.compatibleNewVersion(applicationMnt.getApplicationId(), appName);
            dsAgentVOList.addAll(newList);
        }
        return dsAgentVOList;
    }

    @Override
    public List<ShadowServerConfigurationOutput> getShadowServerConfigs(String appName) {
        return shadowRedisServerService.getShadowServerConfigs(appName);
    }

    @Override
    public List<DsServerVO> getShadowDsServerConfigs(String namespace, DsTypeEnum dsServer) {
        List<DsServerVO> responseList = new ArrayList<>();
        TApplicationMnt applicationMnt = applicationService.queryTApplicationMntByName(namespace);
        if (applicationMnt != null) {
            List<DsModelWithBLOBs> dsModels = applicationDsDAO.selectByAppIdForAgent(applicationMnt.getApplicationId());
            if (CollectionUtils.isNotEmpty(dsModels)) {
                dsModels = dsModels.stream()
                        .filter(dsModel -> dsModel.getDsType().equals(new Byte(String.valueOf(dsServer.getCode()))))
                        .collect(Collectors.toList());
            }
            for (DsModelWithBLOBs dsModel : dsModels) {
                DsServerVO serverVO = new DsServerVO();
                BeanUtils.copyProperties(dsModel, serverVO);
                // agent需要返回0
                serverVO.setStatus(0);
                responseList.add(serverVO);
            }
        }
        if (CollectionUtils.isEmpty(responseList)) {
            return null;
        }
        return responseList;
    }

    @Override
    public void addBusiness(AppBusinessTableInfo info) {
        AppBusinessTableInfo query = new AppBusinessTableInfo();
        query.setUrl(info.getUrl());
        // 补充用户数据
        UserExt user = WebPluginUtils.getUser();
        query.setUserId(user.getId());
        Long count = tAppBusinessTableInfoMapper.selectCountByUserIdAndUrl(query);
        if (count == 1) {
            AppBusinessTableInfo updateInfo = tAppBusinessTableInfoMapper.selectByUserIdAndUrl(query);
            if (null != updateInfo) {
                info.setId(updateInfo.getId());
                tAppBusinessTableInfoMapper.update(info);
            }
        } else if (count < 1) {
            tAppBusinessTableInfoMapper.insert(info);
        } else {
            throw new IllegalArgumentException(String.format("count:%s, url : %s Url大于 1", count, info.getUrl()));
        }
    }

    @Override
    public Response queryPageBusiness(AppBusinessTableQuery query) {
        UserExt user = WebPluginUtils.getUser();
        if (1 == user.getRole()) {
            query.setUserId(user.getId());
        }
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<AppBusinessTableInfo> list = tAppBusinessTableInfoMapper.selectList(query);
        PageInfo<AppBusinessTableInfo> page = new PageInfo<>(list);
        page.getList().forEach(info -> {
            int index = info.getUrl().indexOf("?");
            if (-1 != index) {
                String substring = info.getUrl().substring(0, index);
                info.setUrl(substring);
            }
        });
        return Response.success(page.getList(), page.getTotal());
    }

    @Override
    public List<DsModelWithBLOBs> getAllEnabledDbConfig(Long applicationId) {
        return applicationDsDAO.getAllEnabledDbConfig(applicationId);
    }

    @Override
    public Response secureInit() {
        ApplicationDsQueryParam param = new ApplicationDsQueryParam();
        List<ApplicationDsResult> applicationDsResults = applicationDsDAO.queryList(param);
        applicationDsResults.forEach(applicationDsResult -> {
            ApplicationDsUpdateParam update = new ApplicationDsUpdateParam();
            BeanUtils.copyProperties(applicationDsResult, update);
            applicationDsDAO.update(update);
        });
        return Response.success();
    }

    @Override
    public String parseShadowDbUrl(String config) {
        return shadowDbService.parseShadowDbUrl(config);
    }

    @Override
    public Response dsAdd(ApplicationDsCreateInput createRequest) {
        if (Objects.isNull(createRequest)) {
            return Response.fail("0", "该配置不存在");
        }
        AbstractDsService dsService = getAbstractDsService(createRequest.getDsType());
        if (dsService == null) {
            return Response.fail("dsService obj is null");
        }
        return dsService.dsAdd(createRequest);
    }

    @Override
    public Response dsDelete(ApplicationDsDeleteInput dsDeleteRequest) {

        ApplicationDsResult dsResult = applicationDsDAO.queryByPrimaryKey(dsDeleteRequest.getId());
        if (Objects.isNull(dsResult)) {
            return Response.fail("0", "该配置不存在");
        }
        AbstractDsService dsService = getAbstractDsService(dsResult.getDsType());
        if (dsService == null) {
            return Response.fail("dsService obj is null");
        }
        return dsService.dsDelete(dsDeleteRequest);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response dsQueryV2(Long applicationId) {
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(applicationId);
        if (Objects.isNull(detailResult)) {
            return Response.fail("0", "该应用不存在");
        }

        List<AppShadowDatabaseDTO> shadowDataBaseInfos = applicationClient.getApplicationShadowDataBaseInfo(detailResult.getApplicationName());


        ApplicationDsQueryParam queryParam = new ApplicationDsQueryParam();
        queryParam.setApplicationId(applicationId);
        queryParam.setIsDeleted(0);
        WebPluginUtils.fillQueryParam(queryParam);
        this.filterAndSave(shadowDataBaseInfos, applicationId, queryParam);
        //这里为了拿到id,先存后查
        List<ApplicationDsCacheManageDetailResult> caches = dsCacheManageDAO.selectList(queryParam);
        List<ApplicationDsDbManageDetailResult> dbs = dsDbManageDAO.selectList(queryParam);

        List<ApplicationDsV2Response> response = new ArrayList<>();
        List<ApplicationDsResponse> oldResponseList = (List<ApplicationDsResponse>) this.dsQuery(applicationId).getData();

        response.addAll(caches.stream().map(this::cacheBuild).collect(Collectors.toList()));
        response.addAll(dbs.stream().map(this::dbBuild).collect(Collectors.toList()));
        response.addAll(oldResponseList.stream().map(this::v1Build).collect(Collectors.toList()));

        agentConfigCacheManager.evictShadowDb(detailResult.getApplicationName());
        agentConfigCacheManager.evictShadowServer(detailResult.getApplicationName());
        return Response.success(response);
    }


    @Override
    public Response dsQueryDetailV2(Long applicationId, Long id, String middlewareType, Boolean isNewData) {
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(applicationId);
        if (Objects.isNull(detailResult)) {
            return Response.fail("0", "该应用不存在");
        }

        if (!isNewData) {
            // [db/cache]老数据兼容改造,其他类型保留原返回结构
            Response<ApplicationDsDetailOutput> oldResponse = this.dsQueryDetail(id, true);
            List<Integer> list = Arrays.asList(DbTypeEnum.DB.getCode(), DbTypeEnum.CACHE.getCode());
            if (list.contains(oldResponse.getData().getDbType())) {
                Integer dsType = oldResponse.getData().getDsType();
                AbstractDsService abstractDsService = getAbstractDsService(dsType);
                return Response.success(abstractDsService.convertDetailByTemplate(id));
            }
            return Response.success(this.dsQueryDetail(id, false));
        }
        AbstractTemplateParser templateParser = templateParserMap.get(Type.MiddleWareType.ofKey(middlewareType));
        return Response.success(templateParser.convertDetailByTemplate(id));
    }

    private void filterAndSave(List<AppShadowDatabaseDTO> shadowDataBaseInfos, Long applicationId, ApplicationDsQueryParam queryParam) {

        Map<String, List<AppShadowDatabaseDTO>> amdbTemplateMap = CollStreamUtil.groupByKey(shadowDataBaseInfos, AppShadowDatabaseDTO::getMiddlewareType);
        if (amdbTemplateMap.isEmpty()) {
            return;
        }

        List<ApplicationDsResult> oldList = applicationDsDAO.queryList(queryParam);
        Map<Integer, List<ApplicationDsResult>> oldMap = CollStreamUtil.groupByKey(oldList, ApplicationDsResult::getDbType);
        List<ApplicationDsResult> dbOlds = oldMap.get(MiddleWareTypeEnum.LINK_POOL.getCode());
        List<ApplicationDsResult> cacheOlds = oldMap.get(MiddleWareTypeEnum.CACHE.getCode());

        List<ApplicationDsCacheManageDetailResult> caches = dsCacheManageDAO.selectList(queryParam);
        List<ApplicationDsDbManageDetailResult> dbs = dsDbManageDAO.selectList(queryParam);

        List<AppShadowDatabaseDTO> amdbByDbs = amdbTemplateMap.get(Type.MiddleWareType.LINK_POOL.value());
        List<AppShadowDatabaseDTO> amdbByCaches = amdbTemplateMap.get(Type.MiddleWareType.CACHE.value());

        List<ApplicationDsCacheManageDetailResult> saveCaches = Lists.newArrayList();
        List<ApplicationDsDbManageDetailResult> saveDbs = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(amdbByDbs)) {

            Map<String, AppShadowDatabaseDTO> amdbDbMap = amdbByDbs
                    .stream()
                    .collect(Collectors.toMap(AppShadowDatabaseDTO::getFilterStr, Function.identity(), (key1, key2) -> key2));

            Map<String, ApplicationDsResult> dbOldMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(dbOlds)) {
                dbOldMap = dbOlds
                        .stream()
                        .collect(Collectors.toMap(ApplicationDsResult::getFilterStr, Function.identity(), (key1, key2) -> key2));
            }
            Map<String, ApplicationDsDbManageDetailResult> dbMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(dbs)) {
                dbMap = dbs
                        .stream()
                        .collect(Collectors.toMap(ApplicationDsDbManageDetailResult::getFilterStr, Function.identity(), (key1, key2) -> key2));
            }


            for (String k : amdbDbMap.keySet()) {
                if (!dbOldMap.containsKey(k) && !dbMap.containsKey(k)) {
                    ApplicationDsDbManageDetailResult dbDetail = this.buildDbDetail(amdbDbMap.get(k), applicationId);
                    saveDbs.add(dbDetail);
                }
            }
        }


        if (CollectionUtils.isNotEmpty(amdbByCaches)) {


            Map<String, AppShadowDatabaseDTO> amdbCacheMap = amdbByCaches
                    .stream()
                    .collect(Collectors.toMap(AppShadowDatabaseDTO::getFilterStr, Function.identity(), (key1, key2) -> key2));

            Map<String, ApplicationDsResult> cacheOldMap = new HashMap<>();
            Map<String, ApplicationDsCacheManageDetailResult> cacheMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(cacheOlds)) {
                cacheOldMap = cacheOlds
                        .stream()
                        .collect(Collectors.toMap(ApplicationDsResult::getFilterStr, Function.identity(), (key1, key2) -> key2));
            }
            if (CollectionUtils.isNotEmpty(caches)) {
                cacheMap = caches
                        .stream()
                        .collect(Collectors.toMap(ApplicationDsCacheManageDetailResult::getFilterStr, Function.identity(), (key1, key2) -> key2));
            }


            for (String k : amdbCacheMap.keySet()) {
                if (!cacheOldMap.containsKey(k) && !cacheMap.containsKey(k)) {
                    ApplicationDsCacheManageDetailResult cacheDetail = this.buildCacheDetail(amdbCacheMap.get(k), applicationId);
                    saveCaches.add(cacheDetail);
                }
            }
        }

        dsDbManageDAO.batchSave(saveDbs);
        dsCacheManageDAO.batchSave(saveCaches);
    }


    /**
     * 查询中间件支持的隔离方案
     *
     * @param middlewareType 中间件类型
     * @param engName        中间件英文名
     * @return
     */
    @Override
    public List<SelectVO> queryDsType(String middlewareType, String engName) {
        AbstractDsTemplateService service = templateServiceMap.get(middlewareType);
        return service.queryDsType(middlewareType, engName);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response dsUpdateConfig(ApplicationDsUpdateInputV2 updateRequestV2) {
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(updateRequestV2.getApplicationId());
        if (Objects.isNull(detailResult)) {
            return Response.fail("0", "该应用不存在");
        }
        Integer code = MiddleWareTypeEnum.getEnumByValue(updateRequestV2.getMiddlewareType()).getCode();
        AbstractShaDowManageService service = shaDowServiceMap.get(code);

        if (!updateRequestV2.getIsNewData()) {
            //针对老版本数据,先删除原表数据,新表重新保存
            ApplicationDsResult dsResult = applicationDsDAO.queryByPrimaryKey(updateRequestV2.getId());
            if (Objects.nonNull(dsResult)) {
                ApplicationDsDeleteParam deleteParam = new ApplicationDsDeleteParam();
                deleteParam.setIdList(Collections.singletonList(updateRequestV2.getId()));
                applicationDsDAO.delete(deleteParam);
                service.createShadowProgramme(updateRequestV2, false);
            }
        }
        service.updateShadowProgramme(updateRequestV2);

        agentConfigCacheManager.evictShadowDb(detailResult.getApplicationName());
        agentConfigCacheManager.evictShadowServer(detailResult.getApplicationName());
        return Response.success();
    }

    @Override
    public Response dsQueryConfigTemplate(String agentSourceType, Integer dsType, Boolean isNewData, String cacheType, String connectionPool) {
        Converter.TemplateConverter.TemplateEnum templateEnum;
        if (Strings.isNotBlank(connectionPool)) {
            templateEnum = redisTemplateParser.convert(connectionPool);
            if(Objects.isNull(templateEnum)){
                templateEnum = dbTemplateParser.convert(connectionPool);
            }
        } else {
            templateEnum = Converter.TemplateConverter.ofKey(agentSourceType);
        }
        Type type ;
        if(Objects.isNull(templateEnum)){
            type = Type.MiddleWareType.LINK_POOL;
        }else{
            type = templateEnum.getType();
        }

        if (!templateParserMap.containsKey(type)) {
            return Response.success();
        }
        AbstractTemplateParser templateParser = templateParserMap.get(type);
        return Response.success(templateParser.convertShadowMsgWithTemplate(dsType, isNewData, cacheType, templateEnum));
    }

    /**
     * 删除
     *
     * @param id
     * @param middlewareType
     * @param isNewData
     * @return
     */
    @Override
    public Response dsDeleteV2(Long id, String middlewareType, Boolean isNewData, Long applicationId) {
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(applicationId);
        agentConfigCacheManager.evictShadowDb(detailResult.getApplicationName());
        agentConfigCacheManager.evictShadowServer(detailResult.getApplicationName());
        if (Objects.nonNull(isNewData) && BooleanUtil.isFalse(isNewData)) {
            //删除老数据
            ApplicationDsDeleteParam deleteParam = new ApplicationDsDeleteParam();
            deleteParam.setIdList(Collections.singletonList(id));
            applicationDsDAO.delete(deleteParam);
            return Response.success();
        }
        AbstractTemplateParser templateParser = templateParserMap.get(Type.MiddleWareType.ofKey(middlewareType));
        templateParser.deletedRecord(id);
        return Response.success();
    }

    @Override
    public Response dsCreateConfig(ApplicationDsCreateInputV2 createRequestV2) {
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(createRequestV2.getApplicationId());
        if (Objects.isNull(detailResult)) {
            return Response.fail("0", "该应用不存在");
        }
        Integer code = MiddleWareTypeEnum.getEnumByValue(createRequestV2.getMiddlewareType()).getCode();
        AbstractShaDowManageService service = shaDowServiceMap.get(code);
        service.createShadowProgramme(createRequestV2, true);

        agentConfigCacheManager.evictShadowDb(detailResult.getApplicationName());
        agentConfigCacheManager.evictShadowServer(detailResult.getApplicationName());
        return Response.success();
    }

    /**
     * 获取中间件支持的版本
     *
     * @param middlewareType
     * @return
     */
    @Override
    public List<SelectVO> querySupperName(String middlewareType) {
        AbstractDsTemplateService templateService = templateServiceMap.get(middlewareType);
        return templateService.queryDsSupperName();
    }

    /**
     * 获取缓存支持的模式
     *
     * @return
     */
    @Override
    public List<SelectVO> queryCacheType() {
        return redisTemplateParser.queryCacheType();
    }

    private ApplicationDsDbManageDetailResult buildDbDetail(AppShadowDatabaseDTO amdbData, Long appId) {
        ApplicationDsDbManageDetailResult detailResult = new ApplicationDsDbManageDetailResult();
        detailResult.setApplicationId(appId);
        detailResult.setApplicationName(amdbData.getAppName());
        detailResult.setConnPoolName(amdbData.getConnectionPool());
        detailResult.setDbName(amdbData.getDbName());
        detailResult.setUrl(amdbData.getDataSource());
        detailResult.setUserName(amdbData.getTableUser());
        detailResult.setPwd(amdbData.getPassword());
        detailResult.setFileExtedn(amdbData.getAttachment());
        detailResult.setConfigJson(JSON.toJSONString(amdbData));
        detailResult.setAgentSourceType(amdbData.getType());
        detailResult.setShaDowUrl(amdbData.getShadowDataSource());
        detailResult.setSource(0);
        detailResult.setStatus(1);
        return detailResult;
    }

    private ApplicationDsCacheManageDetailResult buildCacheDetail(AppShadowDatabaseDTO amdbData, Long appId) {
        ApplicationDsCacheManageDetailResult detailResult = new ApplicationDsCacheManageDetailResult();
        detailResult.setApplicationId(appId);
        detailResult.setApplicationName(amdbData.getAppName());
        detailResult.setCacheName(amdbData.getConnectionPool());
        detailResult.setColony(amdbData.getDataSource());
        detailResult.setUserName(amdbData.getTableUser());
        detailResult.setPwd(amdbData.getPassword());
        detailResult.setFileExtedn(amdbData.getAttachment());
        detailResult.setType(amdbData.getExtInfo());
        detailResult.setConfigJson(JSON.toJSONString(amdbData));
        detailResult.setAgentSourceType(amdbData.getType());
        detailResult.setSource(0);
        detailResult.setStatus(1);
        return detailResult;
    }

    private ApplicationDsV2Response dbBuild(ApplicationDsDbManageDetailResult dbDetail) {
        ApplicationDsV2Response v2Response = new ApplicationDsV2Response();
        v2Response.setId(dbDetail.getId());
        v2Response.setApplicationId(String.valueOf(dbDetail.getApplicationId()));
        v2Response.setMiddlewareType(Type.MiddleWareType.LINK_POOL.value());
        DsTypeEnum enumByCode = DsTypeEnum.getEnumByCode(dbDetail.getDsType());
        v2Response.setDsType(Objects.isNull(enumByCode) ? "" : enumByCode.getDesc());
        v2Response.setUrl(dbDetail.getUrl());
        v2Response.setConnectionPool(dbDetail.getConnPoolName());
        v2Response.setIsManual(dbDetail.getSource() == 1);
        v2Response.setAgentSourceType(dbDetail.getAgentSourceType());
        v2Response.setCacheType("");
        v2Response.setIsNewData(true);
        v2Response.setIsNewPage(true);
        v2Response.setCanRemove(v2Response.getIsManual());
        v2Response.setStatus(dbDetail.getStatus());
        return v2Response;
    }

    private ApplicationDsV2Response cacheBuild(ApplicationDsCacheManageDetailResult cacheDetail) {
        ApplicationDsV2Response v2Response = new ApplicationDsV2Response();
        v2Response.setId(cacheDetail.getId());
        v2Response.setApplicationId(String.valueOf(cacheDetail.getApplicationId()));
        v2Response.setMiddlewareType(Type.MiddleWareType.CACHE.value());
        DsTypeEnum enumByCode = DsTypeEnum.getEnumByCode(cacheDetail.getDsType());
        v2Response.setDsType(Objects.isNull(enumByCode) ? "" : enumByCode.getDesc());
        v2Response.setUrl(cacheDetail.getColony());
        v2Response.setConnectionPool(cacheDetail.getCacheName());
        v2Response.setIsManual(cacheDetail.getSource() == 1);
        v2Response.setAgentSourceType(cacheDetail.getAgentSourceType());
        v2Response.setCacheType(cacheDetail.getType());
        v2Response.setIsNewData(true);
        v2Response.setIsNewPage(true);
        v2Response.setCanRemove(v2Response.getIsManual());
        v2Response.setStatus(cacheDetail.getStatus());
        v2Response.setExtMsg(cacheDetail.getType());
        return v2Response;
    }

    private ApplicationDsV2Response v1Build(ApplicationDsResponse response) {
        ApplicationDsV2Response v2Response = new ApplicationDsV2Response();
        v2Response.setId(response.getId());
        v2Response.setApplicationId(String.valueOf(response.getApplicationId()));
        v2Response.setMiddlewareType(MiddleWareTypeEnum.getValueByCode(response.getDbType().getValue()));
        v2Response.setDsType(DsTypeEnum.getEnumByCode(response.getDsType().getValue()).getDesc());
        v2Response.setUrl(response.getUrl());
        v2Response.setConnectionPool("");
        v2Response.setIsManual(true);
        v2Response.setStatus(response.getStatus());
        v2Response.setCanRemove(v2Response.getIsManual());
        if (MiddleWareTypeEnum.LINK_POOL.getCode().equals(response.getDbType().getValue())) {
            v2Response.setUrl(response.getUrl());
            v2Response.setConnectionPool("druid");
            v2Response.setIsNewPage(true);
            v2Response.setAgentSourceType(Converter.TemplateConverter.TemplateEnum._1.getKey());
        }
        if (MiddleWareTypeEnum.CACHE.getCode().equals(response.getDbType().getValue())) {
            v2Response.setUrl(response.getUrl());
            v2Response.setConnectionPool(RedisTemplate.Client.jedis.toString());
            v2Response.setIsNewPage(true);
            v2Response.setAgentSourceType(Converter.TemplateConverter.TemplateEnum._6.getKey());
            if (Objects.nonNull(response.getDsType())) {
                //处理老数据类型映射的问题
                v2Response.setDsType(DsTypeEnum.SHADOW_REDIS_CLUSTER.getDesc());
            }

        }

        return v2Response;
    }

    private List<DsAgentVO> compatibleNewVersion(Long applicationId, String appName) {
        ApplicationDsQueryParam param = new ApplicationDsQueryParam();
        param.setApplicationId(applicationId);
        param.setStatus(0);
        List<ApplicationDsDbManageDetailResult> detailResults = dsDbManageDAO.selectList(param);
        if (CollectionUtils.isEmpty(detailResults)) {
            return Collections.emptyList();
        }
        detailResults = detailResults.stream()
                .filter(
                        dsModel -> dsModel.getDsType().equals(DsTypeEnum.SHADOW_DB.getCode())
                                || dsModel.getDsType().equals(DsTypeEnum.SHADOW_REDIS_SERVER.getCode())
                                || dsModel.getDsType().equals(DsTypeEnum.SHADOW_TABLE.getCode()))
                .collect(Collectors.toList());

        List<DsAgentVO> collect = detailResults.stream().map(detail -> {
            DsAgentVO dsAgentVO = new DsAgentVO();
            dsAgentVO.setApplicationName(appName);
            dsAgentVO.setDsType(detail.getDsType().byteValue());
            dsAgentVO.setUrl(detail.getUrl());
            dsAgentVO.setStatus((byte) 0);
            String fileExtedn = detail.getFileExtedn();
            JSONObject parameter = JSONObject.parseObject(fileExtedn);
            if (DsTypeEnum.SHADOW_TABLE.getCode().equals(detail.getDsType())) {
                dsAgentVO.setShadowTableConfig(this.matchShadowTable(detail.getShaDowFileExtedn()));
            } else {
                Map<String, String> shadowConfigMap = this.matchShadowDb(detail.getShaDowFileExtedn());
                Configurations configurations = new Configurations();
                configurations.setDatasourceMediator(new DatasourceMediator("dataSourceBusiness", "dataSourcePerformanceTest"));
                DataSource dataSourceBusiness = new DataSource();
                dataSourceBusiness.setId("dataSourceBusiness");
                dataSourceBusiness.setUrl(Objects.isNull(parameter) ? detail.getUrl() : parameter.getString("url"));
                dataSourceBusiness.setUsername(Objects.isNull(parameter) ? detail.getUserName() : parameter.getString("username"));

                DataSource dataSourcePerformanceTest = this.buildShadowMsg(shadowConfigMap);
                dataSourcePerformanceTest.setId("dataSourcePerformanceTest");

                List<DataSource> dataSources = new ArrayList<>();
                dataSources.add(dataSourceBusiness);
                dataSources.add(dataSourcePerformanceTest);
                configurations.setDataSources(dataSources);
                dsAgentVO.setShadowDbConfig(configurations);

            }

            return dsAgentVO;
        }).collect(Collectors.toList());


        return collect;
    }


    private Map<String, String> matchShadowDb(String shadowStr) {
        Map<String, String> matchMap = new HashMap<>();
        Map shadowMap = JSONObject.parseObject(shadowStr, Map.class);
        matchMap.put("username", String.valueOf(shadowMap.get("shadowUserName")));
        matchMap.put("url", String.valueOf(shadowMap.get("shadowUrl")));
        matchMap.put("password", String.valueOf(shadowMap.get("shadowPwd")));

        shadowMap.remove("shadowUserName");
        shadowMap.remove("shadowUrl");
        shadowMap.remove("shadowPwd");
        shadowMap.remove("applicationName");

        shadowMap.forEach((k, v) -> {
            Map map = JSONObject.parseObject(String.valueOf(v), Map.class);
            matchMap.put(String.valueOf(k), String.valueOf(map.get("value")));
        });
        return matchMap;
    }

    private String matchShadowTable(String shadowStr) {
        List<ShadowDetailResponse.TableInfo> tableInfos = JSONArray.parseArray(shadowStr, ShadowDetailResponse.TableInfo.class);
        List<String> tableNames = tableInfos
                .stream()
                .filter(ShadowDetailResponse.TableInfo::getIsCheck)
                .map(ShadowDetailResponse.TableInfo::getBizTableName)
                .collect(Collectors.toList());
        return Joiner.on(",").join(tableNames);
    }


    private DataSource buildShadowMsg(Map<String, String> matchShadow) {
        DataSource dataSourcePerformanceTest = new DataSource();
        try {
            org.apache.commons.beanutils.BeanUtils.populate(dataSourcePerformanceTest, matchShadow);
            dataSourcePerformanceTest.setExtra(matchShadow);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSourcePerformanceTest;
    }

    @Override
    public Response enableConfigV2(Long id, String middlewareType, Boolean isNewData, Long applicationId, Integer status) {
        ApplicationDetailResult result = applicationDAO.getApplicationById(applicationId);
        agentConfigCacheManager.evictShadowDb(result.getApplicationName());
        agentConfigCacheManager.evictShadowServer(result.getApplicationName());
        if (Objects.nonNull(isNewData) && BooleanUtil.isFalse(isNewData)) {
            //更新老数据
            ApplicationDsEnableParam enableParam = new ApplicationDsEnableParam();
            enableParam.setId(id);
            enableParam.setStatus(status);
            applicationDsDAO.enable(enableParam);
            return Response.success();
        }
        AbstractTemplateParser templateParser = templateParserMap.get(Type.MiddleWareType.ofKey(middlewareType));
        templateParser.enable(id, status);
        return Response.success();
    }
}
