package io.shulie.takin.web.biz.service.dsManage.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.pamirs.attach.plugin.dynamic.Type;
import com.pamirs.takin.common.enums.ds.DbTypeEnum;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import com.pamirs.takin.common.enums.ds.MiddleWareTypeEnum;
import com.pamirs.takin.common.enums.ds.pattern.RedisPatternEnum;
import com.pamirs.takin.entity.dao.simplify.TAppBusinessTableInfoMapper;
import com.pamirs.takin.entity.domain.entity.DsModelWithBLOBs;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import com.pamirs.takin.entity.domain.entity.simplify.AppBusinessTableInfo;
import com.pamirs.takin.entity.domain.query.agent.AppBusinessTableQuery;
import com.pamirs.takin.entity.domain.vo.dsmanage.Configurations;
import com.pamirs.takin.entity.domain.vo.dsmanage.DsAgentVO;
import com.pamirs.takin.entity.domain.vo.dsmanage.DsServerVO;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.result.application.AppShadowDatabaseDTO;
import io.shulie.takin.web.biz.convert.db.parser.AbstractTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.DruidTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.RedisTemplateParser;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInput;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private DruidTemplateParser druidTemplateParser;

    @Autowired
    private RedisTemplateParser redisTemplateParser;

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
        templateServiceMap.put(Type.MiddleWareType.CACHE.value(),dsCacheTemplateService);
        templateServiceMap.put(Type.MiddleWareType.LINK_POOL.value(), dsDbTemplateService);

        shaDowServiceMap.put(MiddleWareTypeEnum.CACHE.getCode(),shaDowCacheService);
        shaDowServiceMap.put(MiddleWareTypeEnum.DB.getCode(),shaDowDbService);

        templateParserMap = new HashMap<>();
        templateParserMap.put(MiddleWareTypeEnum.DB.getValue(),druidTemplateParser);
        templateParserMap.put(MiddleWareTypeEnum.CACHE.getValue(),redisTemplateParser);
    }



    private Map<String, AbstractDsTemplateService> templateServiceMap;

    private Map<Integer, AbstractShaDowManageService> shaDowServiceMap;

    private Map<String, AbstractTemplateParser> templateParserMap;



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
        page.getList().stream().forEach(info -> {
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
        applicationDsResults.stream().forEach(applicationDsResult -> {
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
        if(Objects.isNull(detailResult)){
            return Response.fail("0", "该应用不存在");
        }

        List<AppShadowDatabaseDTO> shadowDataBaseInfos = applicationClient.getApplicationShadowDataBaseInfo(detailResult.getApplicationName());

        this.filterAndSave(shadowDataBaseInfos,applicationId);
        //这里为了拿到id,先存后查
        List<ApplicationDsCacheManageDetailResult> caches = dsCacheManageDAO.selectList(applicationId);
        List<ApplicationDsDbManageDetailResult> dbs = dsDbManageDAO.selectList(applicationId);

        List<ApplicationDsV2Response> response = new ArrayList<>();
        List<ApplicationDsResponse> oldResponseList = (List<ApplicationDsResponse>) this.dsQuery(applicationId).getData();
        List<Integer> filterDbType = Arrays.asList(0,1);
        List<ApplicationDsResponse> otherList = oldResponseList.stream()
                .filter(dsResponse -> !filterDbType.contains(dsResponse.getDbType().getValue()))
                .collect(Collectors.toList());

        response.addAll(caches.stream().map(this::cacheBuild).collect(Collectors.toList()));
        response.addAll(dbs.stream().map(this::dbBuild).collect(Collectors.toList()));
        response.addAll(otherList.stream().map(this::v1Build).collect(Collectors.toList()));
        return Response.success(response);
    }


    @Override
    public Response dsQueryDetailV2(Long applicationId, Long id,String middlewareType,String agentSourceType) {
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(applicationId);
        if(Objects.isNull(detailResult)){
            return Response.fail("0", "该应用不存在");
        }

        AbstractTemplateParser templateParser = templateParserMap.get(middlewareType);

        return Response.success(templateParser.convertDetailByTemplate(id));
    }

    private void filterAndSave(List<AppShadowDatabaseDTO> shadowDataBaseInfos, Long applicationId){

        Map<String, List<AppShadowDatabaseDTO>> amdbTemplateMap = CollStreamUtil.groupByKey(shadowDataBaseInfos, AppShadowDatabaseDTO::getMiddlewareType);
        if(amdbTemplateMap.isEmpty()){
            return;
        }

        List<ApplicationDsCacheManageDetailResult> caches = dsCacheManageDAO.selectList(applicationId);
        List<ApplicationDsDbManageDetailResult> dbs = dsDbManageDAO.selectList(applicationId);

        List<AppShadowDatabaseDTO> amdbByDbs = amdbTemplateMap.get(Type.MiddleWareType.LINK_POOL.value());
        List<AppShadowDatabaseDTO> amdbByCaches = amdbTemplateMap.get(Type.MiddleWareType.CACHE.value());

        List<ApplicationDsCacheManageDetailResult> saveCaches = Lists.newArrayList();
        List<ApplicationDsDbManageDetailResult> saveDbs = Lists.newArrayList();

        amdbByDbs.forEach(amdbByDb ->{
            String amdbStr= amdbByDb.getDataSource()+"_"+amdbByDb.getTableUser();
            ApplicationDsDbManageDetailResult dbDetail = this.buildDbDetail(amdbByDb, applicationId);
            if(CollectionUtils.isEmpty(dbs)){
                saveDbs.add(dbDetail);
                dbs.add(dbDetail);
            }else{
                dbs.forEach(db ->{
                    String dbStr = db.getUrl()+"_"+db.getUserName();
                    if(!amdbStr.equals(dbStr)){
                        saveDbs.add(dbDetail);
                        dbs.add(dbDetail);
                    }
                });
            }

        });

        amdbByCaches.forEach(amdbByCache ->{
            String amdbStr= amdbByCache.getDataSource()+"_"+amdbByCache.getTableUser();
            ApplicationDsCacheManageDetailResult cacheDetail = this.buildCacheDetail(amdbByCache, applicationId);
            if(CollectionUtils.isEmpty(caches)){
                saveCaches.add(cacheDetail);
                caches.add(cacheDetail);
            }else{
                caches.forEach(cache ->{
                    String cacheStr = cache.getColony()+"_"+cache.getUserName();
                    if(!amdbStr.equals(cacheStr)){
                        saveCaches.add(cacheDetail);
                        caches.add(cacheDetail);
                    }
                });
            }
        });

        dsCacheManageDAO.batchSave(saveCaches);
        dsDbManageDAO.batchSave(saveDbs);
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
        return service.queryDsType(middlewareType,engName);
    }


    @Override
    public Response dsUpdateConfig(ApplicationDsUpdateInputV2 updateRequestV2) {
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(updateRequestV2.getApplicationId());
        if(Objects.isNull(detailResult)){
            return Response.fail("0", "该应用不存在");
        }
        AbstractShaDowManageService service = shaDowServiceMap.get(Integer.valueOf(updateRequestV2.getMiddlewareType()));
        service.updateShadowProgramme(updateRequestV2);
        return Response.success();
    }

    private ApplicationDsDbManageDetailResult buildDbDetail(AppShadowDatabaseDTO amdbData, Long appId){
        ApplicationDsDbManageDetailResult detailResult = new ApplicationDsDbManageDetailResult();
        detailResult.setApplicationId(appId);
        detailResult.setApplicationName(amdbData.getAppName());
        detailResult.setConnPoolName(amdbData.getConnectionPool());
        detailResult.setDbName(null);//todo
        detailResult.setUrl(amdbData.getDataSource());
        detailResult.setUserName(amdbData.getTableUser());
        detailResult.setPwd(amdbData.getPassword());
        detailResult.setFileExtedn(amdbData.getAttachment());
        detailResult.setConfigJson(null);
        detailResult.setAgentSourceType(amdbData.getType());
        return detailResult;
    }

    private ApplicationDsCacheManageDetailResult buildCacheDetail(AppShadowDatabaseDTO amdbData,Long appId){
        ApplicationDsCacheManageDetailResult detailResult = new ApplicationDsCacheManageDetailResult();
        detailResult.setApplicationId(appId);
        detailResult.setApplicationName(amdbData.getAppName());
        detailResult.setCacheName(amdbData.getMiddlewareType());
        detailResult.setColony(amdbData.getDataSource());
        detailResult.setUserName(amdbData.getTableUser());
        detailResult.setPwd(amdbData.getPassword());
        detailResult.setFileExtedn(amdbData.getAttachment());
        detailResult.setType(RedisPatternEnum.getEnumByDesc(amdbData.getExtInfo()).getCode());
        detailResult.setConfigJson(null);//todo nf
        detailResult.setAgentSourceType(amdbData.getType());
        return detailResult;
    }

    private ApplicationDsV2Response dbBuild(ApplicationDsDbManageDetailResult dbDetail){
        ApplicationDsV2Response v2Response = new ApplicationDsV2Response();
        v2Response.setId(dbDetail.getId());
        v2Response.setApplicationId(dbDetail.getApplicationId());
        v2Response.setMiddlewareType(Type.MiddleWareType.LINK_POOL.value());
        v2Response.setDsType(DsTypeEnum.getEnumByCode(dbDetail.getDsType()).getDesc());
        v2Response.setUrl(dbDetail.getUrl());
        v2Response.setConnectionPool(dbDetail.getConnPoolName());
        v2Response.setSource(dbDetail.getStatus()==1?"手工添加": Strings.EMPTY);
        v2Response.setAgentSourceType(dbDetail.getAgentSourceType());
        return v2Response;
    }

    private ApplicationDsV2Response cacheBuild(ApplicationDsCacheManageDetailResult cacheDetail){
        ApplicationDsV2Response v2Response = new ApplicationDsV2Response();
        v2Response.setId(cacheDetail.getId());
        v2Response.setApplicationId(cacheDetail.getApplicationId());
        v2Response.setMiddlewareType(Type.MiddleWareType.CACHE.value());
        v2Response.setDsType(DsTypeEnum.getEnumByCode(cacheDetail.getDsType()).getDesc());
        v2Response.setUrl(cacheDetail.getColony()+"(此处为redis连接地址)");
        v2Response.setConnectionPool(cacheDetail.getCacheName());
        v2Response.setSource(cacheDetail.getStatus()==1?"手工添加": Strings.EMPTY);
        v2Response.setAgentSourceType(cacheDetail.getAgentSourceType());
        return v2Response;
    }

    private ApplicationDsV2Response v1Build(ApplicationDsResponse response){
        ApplicationDsV2Response v2Response = new ApplicationDsV2Response();
        v2Response.setId(response.getId());
        v2Response.setApplicationId(response.getApplicationId());
        v2Response.setMiddlewareType(MiddleWareTypeEnum.getValueByCode(response.getDbType().getValue()));
        v2Response.setDsType(DsTypeEnum.getEnumByCode(response.getDsType().getValue()).getDesc());
        v2Response.setUrl(response.getUrl()+"此处为"+response.getDbType().getLabel()+"连接地址)");
        v2Response.setConnectionPool("");
        v2Response.setSource("手工添加");
        return v2Response;
    }

}
