package io.shulie.takin.web.biz.service.dsManage.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Lists;
import com.pamirs.attach.plugin.dynamic.Type;
import com.pamirs.attach.plugin.dynamic.template.RedisTemplate;
import com.pamirs.takin.common.constant.AppAccessTypeEnum;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import com.pamirs.takin.entity.domain.entity.DsModelWithBLOBs;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.convert.db.parser.RedisTemplateParser;
import io.shulie.takin.web.biz.init.sync.ConfigSyncService;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsDeleteInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsEnableInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationDsDetailOutput;
import io.shulie.takin.web.biz.pojo.output.application.ShadowServerConfigurationOutput;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.application.ShadowServerConfigurationResponse;
import io.shulie.takin.web.biz.pojo.response.application.SingleServerConfiguration;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.dsManage.AbstractDsService;
import io.shulie.takin.web.biz.utils.DsManageUtil;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.util.JsonUtil;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDsCacheManageDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDsDAO;
import io.shulie.takin.web.data.param.application.ApplicationDsCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationDsDeleteParam;
import io.shulie.takin.web.data.param.application.ApplicationDsEnableParam;
import io.shulie.takin.web.data.param.application.ApplicationDsQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationDsUpdateParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDsCacheManageDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDsResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author HengYu
 * @className AbstractDsService
 * @date 2021/4/12 9:25 下午
 * @description 数据源存储抽象服务
 */

@Component
public class ShadowRedisServerServiceImpl extends AbstractDsService {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ConfigSyncService configSyncService;

    @Autowired
    private ApplicationDsDAO applicationDsDAO;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private AgentConfigCacheManager agentConfigCacheManager;

    @Autowired
    private ApplicationDsCacheManageDAO cacheManageDAO;

    @Autowired
    private RedisTemplateParser redisTemplateParser;


    @Override
    public Response dsAdd(ApplicationDsCreateInput createRequest) {
        ApplicationDetailResult applicationDetailResult = applicationDAO.getApplicationById(
                createRequest.getApplicationId());
        Assert.notNull(applicationDetailResult, "应用不存在!");

        ApplicationDsQueryParam queryCurrentParam = new ApplicationDsQueryParam();
        queryCurrentParam.setApplicationId(createRequest.getApplicationId());
        queryCurrentParam.setIsDeleted(0);
        List<ApplicationDsResult> applicationDsResultList = applicationDsDAO.queryList(queryCurrentParam);
        List<Integer> dsTypeList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(applicationDsResultList)) {
            dsTypeList = applicationDsResultList.stream().map(ApplicationDsResult::getDsType)
                    .map(String::valueOf).map(Integer::parseInt)
                    .distinct()
                    .collect(Collectors.toList());
        }
        ApplicationDsCreateParam createParam = new ApplicationDsCreateParam();

        //新增影子server配置
        String url = parseShadowServerUrl(createRequest.getConfig());
        String parsedConfig = parseShadowServerConfig(createRequest.getConfig());
        createParam.setUrl(url);
        createParam.setParseConfig(parsedConfig);
        createParam.setConfig(createRequest.getConfig());

        // 等其他都成功, 再插入数据库
        createParam.setApplicationId(createRequest.getApplicationId());
        createParam.setApplicationName(applicationDetailResult.getApplicationName());
        createParam.setDbType(createRequest.getDbType());
        createParam.setDsType(Integer.parseInt(String.valueOf(createRequest.getDsType())));
        WebPluginUtils.fillUserData(createParam);

        //同步配置
        configSyncService.syncShadowDB(WebPluginUtils.traceTenantCommonExt(), createParam.getApplicationId(), null);

        //修改应用状态
        applicationService.modifyAccessStatus(String.valueOf(createParam.getApplicationId()),
                AppAccessTypeEnum.UNUPLOAD.getValue(), null);

        agentConfigCacheManager.evictShadowServer(applicationDetailResult.getApplicationName());

        // 新增配置
        createParam.setStatus(createRequest.getStatus());
        applicationDsDAO.insert(createParam);
        return Response.success();
    }

    @Override
    public Response dsUpdate(ApplicationDsUpdateInput updateRequest) {
        ApplicationDsResult dsResult = applicationDsDAO.queryByPrimaryKey(updateRequest.getId());
        if (Objects.isNull(dsResult)) {
            return Response.fail("0", "该配置不存在");
        }

        ApplicationDsUpdateParam updateParam = new ApplicationDsUpdateParam();
        String config = updateRequest.getConfig();

        // config 替换
        config = this.getOriginConfigFromServer(config, dsResult.getConfig());
        updateParam.setUrl(parseShadowServerUrl(config));
        updateParam.setConfig(config);
        updateParam.setParseConfig(parseShadowServerConfig(config));

        configSyncService.syncShadowDB(WebPluginUtils.traceTenantCommonExt(), dsResult.getApplicationId(),
            dsResult.getApplicationName());
        configSyncService.syncShadowDB(WebPluginUtils.getTenantUserAppKey(), dsResult.getApplicationId(),
                dsResult.getApplicationName());

        agentConfigCacheManager.evictShadowServer(dsResult.getApplicationName());

        updateParam.setId(updateRequest.getId());
        updateParam.setStatus(updateRequest.getStatus());
        applicationDsDAO.update(updateParam);
        return Response.success();
    }

    @Override
    public Response<ApplicationDsDetailOutput> dsQueryDetail(Long dsId, boolean isOldVersion) {
        ApplicationDsResult dsResult = applicationDsDAO.queryByPrimaryKey(dsId);

        if (Objects.isNull(dsResult)) {
            return Response.fail("该影子库表配置不存在");
        }

        ApplicationDsDetailOutput dsDetailResponse = new ApplicationDsDetailOutput();
        dsDetailResponse.setId(dsResult.getId());
        dsDetailResponse.setApplicationId(dsResult.getApplicationId());
        dsDetailResponse.setApplicationName(dsResult.getApplicationName());
        dsDetailResponse.setDbType(dsResult.getDbType());
        dsDetailResponse.setDsType(dsResult.getDsType());
        dsDetailResponse.setUrl(dsResult.getUrl());

        String config = dsResult.getConfig();
        // 影子 server
        // json 解析, 密码脱敏
        config = this.getSafeConfigFromServer(config);

        dsDetailResponse.setConfig(config);
        return Response.success(dsDetailResponse);
    }

    @Override
    public Response enableConfig(ApplicationDsEnableInput enableRequest) {
        ApplicationDsResult dsResult = applicationDsDAO.queryByPrimaryKey(enableRequest.getId());
        if (Objects.isNull(dsResult)) {
            return Response.fail("0", "该配置不存在");
        }
        ApplicationDsEnableParam enableParam = new ApplicationDsEnableParam();
        enableParam.setId(enableRequest.getId());
        enableParam.setStatus(enableRequest.getStatus());
        applicationDsDAO.enable(enableParam);
        configSyncService.syncShadowDB(WebPluginUtils.traceTenantCommonExt(), dsResult.getApplicationId(),
            dsResult.getApplicationName());
        configSyncService.syncShadowDB(WebPluginUtils.getTenantUserAppKey(), dsResult.getApplicationId(),
                dsResult.getApplicationName());
        agentConfigCacheManager.evictShadowServer(dsResult.getApplicationName());

        return Response.success();
    }

    @Override
    public Response dsDelete(ApplicationDsDeleteInput dsDeleteRequest) {
        ApplicationDsResult dsResult = applicationDsDAO.queryByPrimaryKey(dsDeleteRequest.getId());
        if (Objects.isNull(dsResult)) {
            return Response.fail("0", "该配置不存在");
        }
        ApplicationDsDeleteParam deleteParam = new ApplicationDsDeleteParam();
        deleteParam.setIdList(Collections.singletonList(dsDeleteRequest.getId()));
        applicationDsDAO.delete(deleteParam);

        configSyncService.syncShadowDB(WebPluginUtils.traceTenantCommonExt(), dsResult.getApplicationId(),
            dsResult.getApplicationName());
        configSyncService.syncShadowDB(WebPluginUtils.getTenantUserAppKey(), dsResult.getApplicationId(),
                dsResult.getApplicationName());

        agentConfigCacheManager.evictShadowServer(dsResult.getApplicationName());
        return Response.success();
    }

    public List<ShadowServerConfigurationOutput> getShadowServerConfigs(String appName) {
        List<ShadowServerConfigurationOutput> responseList = new ArrayList<>();
        TApplicationMnt applicationMnt = applicationService.queryTApplicationMntByName(appName);
        if (applicationMnt != null) {
            List<DsModelWithBLOBs> dsModels = applicationDsDAO.selectByAppIdForAgent(applicationMnt.getApplicationId());
            if (CollectionUtils.isNotEmpty(dsModels)) {
                dsModels = dsModels.stream()
                        .filter(
                                dsModel -> dsModel.getDsType().equals(new Byte(String.valueOf(DsTypeEnum.SHADOW_REDIS_SERVER
                                        .getCode()))))
                        .collect(Collectors.toList());
            }
            for (DsModelWithBLOBs dsModel : dsModels) {
                ShadowServerConfigurationOutput configurationResponse
                        = JSON.parseObject(dsModel.getParseConfig(), ShadowServerConfigurationOutput.class);
                responseList.add(configurationResponse);
            }
            List<ShadowServerConfigurationOutput> newData = this.compatibleNewVersion(applicationMnt.getApplicationId());
            responseList.addAll(newData);
        }
        return responseList;
    }

    /**
     * 获得 server 原有的, 带有明文密码的 json
     * 详情传入的 json, 密码可能被脱敏
     *
     * @param json       json 配置
     * @param originJson 原有的 json 配置
     * @return 原有的 json
     */
    private String getOriginConfigFromServer(String json, String originJson) {
        // 匹配是否没有脱敏的密码
        // 没有, 说明能直接更新
        String safePasswordField;
        if (StringUtils.isBlank(json)
                || !json.contains(safePasswordField = DsManageUtil.getSafePasswordFieldAboutJson())) {
            return json;
        }

        // 到这里, 说明有脱敏的密码
        // 需要替换原有的密码, 再进行更新
        // 解析
        ShadowServerConfigurationResponse response = JsonUtil.json2bean(originJson,
                ShadowServerConfigurationResponse.class);
        String password = response.getDataSourceBusinessPerformanceTest().getPassword();
        String originPasswordField = DsManageUtil.getOriginPasswordFieldAboutJson(password);
        // 脱敏的更换成明文密码的, 为了更新
        return json.replace(safePasswordField, originPasswordField);
    }

    public String parseShadowServerUrl(String config) {
        ShadowServerConfigurationResponse configurations = parseJsonConfigurations(config);
        SingleServerConfiguration singleServerConfiguration = configurations.getDataSourceBusinessPerformanceTest();
        return singleServerConfiguration.getNodes();
    }

    private String parseShadowServerConfig(String config) {
        ShadowServerConfigurationResponse configurations = parseJsonConfigurations(config);
        return JSON.toJSONString(configurations);
    }

    public ShadowServerConfigurationResponse parseJsonConfigurations(String jsonStr) {
        ShadowServerConfigurationResponse shadowServerConfigurationResponse = new ShadowServerConfigurationResponse();
        Object object = JSONObject.parse(jsonStr);
        Object dataSourceBusinessObj = ((JSONObject) object).get("dataSourceBusiness");
        SingleServerConfiguration dataSourceBusiness = new SingleServerConfiguration();
        dataSourceBusiness.setNodes(String.valueOf(((JSONObject) dataSourceBusinessObj).get("nodes")));
        Object tmpMaster = ((JSONObject) dataSourceBusinessObj).get("master");
        if (tmpMaster != null) {
            dataSourceBusiness.setMaster(String.valueOf(tmpMaster));
        }
        shadowServerConfigurationResponse.setDataSourceBusiness(dataSourceBusiness);

        Object dataSourceBusinessPerformanceTestObj = ((JSONObject) object).get("dataSourceBusinessPerformanceTest");
        SingleServerConfiguration dataSourceBusinessPerformanceTest = new SingleServerConfiguration();
        dataSourceBusinessPerformanceTest.setNodes(
                String.valueOf(((JSONObject) dataSourceBusinessPerformanceTestObj).get("nodes")));
        dataSourceBusinessPerformanceTest.setDatabase(
                String.valueOf(((JSONObject) dataSourceBusinessPerformanceTestObj).get("database")));
        dataSourceBusinessPerformanceTest.setPassword(
                String.valueOf(((JSONObject) dataSourceBusinessPerformanceTestObj).get("password")));
        Object tmpTestMaster = ((JSONObject) dataSourceBusinessPerformanceTestObj).get("master");
        if (tmpTestMaster != null) {
            dataSourceBusinessPerformanceTest.setMaster(String.valueOf(tmpTestMaster));
        }
        shadowServerConfigurationResponse.setDataSourceBusinessPerformanceTest(dataSourceBusinessPerformanceTest);
        return shadowServerConfigurationResponse;
    }

    /**
     * json 配置, 密码脱敏, 替换
     *
     * @param json json 配置
     * @return 安全的 json
     */
    private String getSafeConfigFromServer(String json) {
        // 转对象
        if (StringUtils.isBlank(json)) {
            return "";
        }

        // 替换
        ShadowServerConfigurationResponse response = JsonUtil.json2bean(json, ShadowServerConfigurationResponse.class);

        // 转 json
        SingleServerConfiguration dataSourceBusinessPerformanceTest = response.getDataSourceBusinessPerformanceTest();
        // 这里只判断 pt 的 password (原 password, 不填写)
        if (dataSourceBusinessPerformanceTest.getPassword() == null) {
            return json;
        }

        // tips: 这里不使用 对象转json, 是因为换行格式, 都没了
        String originPasswordField = DsManageUtil.getOriginPasswordFieldAboutJson(
                dataSourceBusinessPerformanceTest.getPassword());
        String newPasswordField = DsManageUtil.getSafePasswordFieldAboutJson();
        return json.replace(originPasswordField, newPasswordField);
    }


    /**
     * 老数据映射成新的结构
     *
     * @param recordId
     * @return
     */
    @Override
    public ShadowDetailResponse convertDetailByTemplate(Long recordId) {
        ApplicationDsResult dsResult = applicationDsDAO.queryByPrimaryKey(recordId);

        if (Objects.isNull(dsResult)) {
            return null;
        }
        // 替换
        ShadowServerConfigurationResponse response = JsonUtil.json2bean(dsResult.getConfig(), ShadowServerConfigurationResponse.class);
        // 转 json
        SingleServerConfiguration bus = response.getDataSourceBusiness();
        SingleServerConfiguration test = response.getDataSourceBusinessPerformanceTest();

        ShadowDetailResponse shadowDetailResponse = new ShadowDetailResponse();
        shadowDetailResponse.setId(dsResult.getId());
        shadowDetailResponse.setApplicationId(String.valueOf(dsResult.getApplicationId()));
        shadowDetailResponse.setMiddlewareType(Type.MiddleWareType.CACHE.value());
        shadowDetailResponse.setDsType(DsTypeEnum.SHADOW_REDIS_CLUSTER.getCode());
        String url = bus.getMaster() + "," + bus.getNodes();
        shadowDetailResponse.setUrl(url);
        shadowDetailResponse.setConnectionPool(RedisTemplate.Client.jedis.toString());
        shadowDetailResponse.setCacheType("主从模式");
        shadowDetailResponse.setShadowInfo(JSON.toJSONString(test));
        return shadowDetailResponse;
    }


    private List<ShadowServerConfigurationOutput> compatibleNewVersion(Long applicationId) {
        List<ShadowServerConfigurationOutput> list = new ArrayList<>();
        ApplicationDsQueryParam param = new ApplicationDsQueryParam();
        param.setApplicationId(applicationId);
        param.setStatus(0);
        List<ApplicationDsCacheManageDetailResult> results = cacheManageDAO.selectList(param);
        if (results.isEmpty()) {
            return list;
        }

        list = results.stream().filter(
                detail -> detail.getDsType().equals(DsTypeEnum.SHADOW_REDIS_CLUSTER.getCode())
        ).map(detail -> {
            SingleServerConfiguration bus;
            if (StringUtils.isBlank(detail.getFileExtedn())) {
                bus = JSONObject.parseObject(detail.getColony(), SingleServerConfiguration.class);
            } else if(Objects.equals("老转新",detail.getConfigJson())){
                bus  = JSON.parseObject(detail.getFileExtedn(), ShadowServerConfigurationOutput.class).getDataSourceBusiness();
            }else{
                bus = JSONObject.parseObject(detail.getFileExtedn(), SingleServerConfiguration.class);
            }


            String shaDowFileExtedn = detail.getShaDowFileExtedn();
            JSONObject jsonObject = JSON.parseObject(shaDowFileExtedn);
            String shadowConfig = jsonObject.getString("shadowConfig");
            SingleServerConfiguration test = JSONObject.parseObject(shadowConfig, SingleServerConfiguration.class);
            Map<String, String> modelMap = redisTemplateParser.convertModel();
            if (Objects.isNull(bus)) {
                if (Objects.nonNull(modelMap)) {
                    test.setModel(modelMap.get(detail.getType()));
                    test.setClient(detail.getCacheName());
                }
            } else {
                test.setModel(StringUtils.isBlank(bus.getModel()) ? modelMap.get(detail.getType()) : bus.getModel());
                test.setClient(StringUtils.isBlank(bus.getClient()) ? detail.getCacheName() : bus.getClient());
            }
            bus.setClient(test.getClient());
            bus.setModel(test.getModel());
            return new ShadowServerConfigurationOutput(bus, test);
        }).collect(Collectors.toList());

        return list;
    }
}

