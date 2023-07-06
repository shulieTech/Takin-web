package io.shulie.takin.web.biz.service.dsManage.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;

import com.pamirs.takin.common.constant.AppAccessTypeEnum;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsDeleteInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsEnableInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationDsDetailOutput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationEsDetailOutput;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.dsManage.AbstractDsService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDsDAO;
import io.shulie.takin.web.data.param.application.ApplicationDsCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationDsDeleteParam;
import io.shulie.takin.web.data.param.application.ApplicationDsEnableParam;
import io.shulie.takin.web.data.param.application.ApplicationDsUpdateParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDsResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author HengYu
 * @className ShadowEsServiceImpl
 * @date 2021/4/12 9:25 下午
 * @description Es数据源存储服务
 */
@Component
public class ShadowEsServiceImpl extends AbstractDsService {

    private Logger logger = LoggerFactory.getLogger(ShadowDbServiceImpl.class);

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationDsDAO applicationDsDAO;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private AgentConfigCacheManager agentConfigCacheManager;


    @Override
    public Response dsAdd(ApplicationDsCreateInput createRequest) {

        ApplicationDetailResult applicationDetailResult = applicationDAO.getApplicationById(
                createRequest.getApplicationId());

        Response fail = validator(createRequest, applicationDetailResult);
        if (fail != null) {
            return fail;
        }

        ApplicationDsCreateParam createParam = new ApplicationDsCreateParam();

        addParserConfig(createRequest, createParam);

        // 等其他都成功, 再插入数据库
        createParam.setApplicationId(createRequest.getApplicationId());
        createParam.setApplicationName(applicationDetailResult.getApplicationName());
        createParam.setDbType(createRequest.getDbType());
        createParam.setDsType(Integer.parseInt(String.valueOf(createRequest.getDsType())));
        WebPluginUtils.fillUserData(createParam);
        // 新增配置
        createParam.setStatus(createRequest.getStatus());
        createParam.setConfigType(createRequest.getConfigType());
        syncInfo(createRequest.getApplicationId(), applicationDetailResult.getApplicationName());

        applicationDsDAO.insert(createParam);
        return Response.success();
    }

    private void addParserConfig(ApplicationDsCreateInput createRequest, ApplicationDsCreateParam createParam) {
        String config = this.getCreateInputConfig(createRequest);
        Map<String, String> map = parseConfig(config);

        String businessNodes = map.get("businessNodes");
        createParam.setUrl(businessNodes);
        createParam.setConfig(config);
        createParam.setParseConfig(createRequest.getConfig());
    }

    private String getCreateInputConfig(ApplicationDsCreateInput createRequest) {
        if (StringUtils.isBlank(createRequest.getConfig())) {
            Map<String, String> jsonConfig = new HashMap<>();
            jsonConfig.put("businessNodes", createRequest.getBusinessNodes());
            jsonConfig.put("performanceTestNodes", createRequest.getPerformanceTestNodes());
            return JsonHelper.bean2Json(jsonConfig);
        }
        return createRequest.getConfig();
    }

    private Response validator(ApplicationDsCreateInput createRequest,
                               ApplicationDetailResult applicationDetailResult) {
        logger.warn("应用不存在! id:{}", createRequest.getApplicationId());
        if (applicationDetailResult == null) {
            return Response.fail("应用不存在!");
        }
        return null;
    }

    /**
     * 做些数据状态同步
     *
     * @param applicationId   应用ID
     * @param applicationName 应用名称
     */
    private void syncInfo(Long applicationId, String applicationName) {
        //修改应用状态
        applicationService.modifyAccessStatus(String.valueOf(applicationId),
                AppAccessTypeEnum.UNUPLOAD.getValue(), null);
        syncShadowEsConfig(applicationId);
        clearCache(applicationName);
    }

    private void syncShadowEsConfig(Long applicationId) {
        //todo 待核对是否有必要同步配置
        //configSyncService.syncShadowDB(WebPluginUtils.getTenantUserAppKey(), applicationId, null);
    }

    @Override
    public Response dsUpdate(ApplicationDsUpdateInput updateRequest) {
        ApplicationDsResult dsResult = getApplicationDsResult(updateRequest.getId());
        if (Objects.isNull(dsResult)) {
            return Response.fail("0", "该配置不存在");
        }

        ApplicationDsUpdateParam updateParam = new ApplicationDsUpdateParam();

        updateParserConfig(updateRequest, updateParam);

        updateParam.setId(updateRequest.getId());
        updateParam.setStatus(updateRequest.getStatus());
        updateParam.setConfigType(updateRequest.getConfigType());
        syncInfo(dsResult.getApplicationId(), dsResult.getApplicationName());
        applicationDsDAO.update(updateParam);
        return Response.success();
    }

    private void updateParserConfig(ApplicationDsUpdateInput updateRequest, ApplicationDsUpdateParam updateParam) {
        String config = this.getCreateInputConfig(updateRequest);
        Map<String, String> map = parseConfig(config);
        String businessNodes = map.get("businessNodes");
        updateParam.setUrl(businessNodes);
        updateParam.setConfig(config);
        updateParam.setParseConfig(config);
    }

    private Map<String, String> parseConfig(String config) {
        // config 解密密码
        return JSON.parseObject(config, Map.class);
    }

    @Override
    public Response<ApplicationDsDetailOutput> dsQueryDetail(Long dsId, boolean isOldVersion) {
        ApplicationDsResult dsResult = getApplicationDsResult(dsId);
        if (Objects.isNull(dsResult)) {
            return Response.fail("该影子配置不存在");
        }

        ApplicationEsDetailOutput dsDetailResponse = new ApplicationEsDetailOutput();
        queryParserConfig(dsResult, dsDetailResponse);
        return Response.success(dsDetailResponse);
    }

    private void queryParserConfig(ApplicationDsResult dsResult, ApplicationEsDetailOutput dsDetailResponse) {
        dsDetailResponse.setId(dsResult.getId());
        dsDetailResponse.setApplicationId(dsResult.getApplicationId());
        dsDetailResponse.setApplicationName(dsResult.getApplicationName());
        dsDetailResponse.setDbType(dsResult.getDbType());
        dsDetailResponse.setDsType(dsResult.getDsType());
        dsDetailResponse.setUrl(dsResult.getUrl());
        String config = dsResult.getConfig();
        dsDetailResponse.setConfig(config);
        dsDetailResponse.setConfigType(dsResult.getConfigType());
        Map<String, String> map = this.parseConfig(config);
        if (CollectionUtil.isNotEmpty(map)) {
            dsDetailResponse.setBusinessNodes(map.get("businessNodes"));
            dsDetailResponse.setPerformanceTestNodes(map.get("performanceTestNodes"));
        }
    }

    @Override
    public Response enableConfig(ApplicationDsEnableInput enableRequest) {
        ApplicationDsResult dsResult = getApplicationDsResult(enableRequest.getId());

        if (Objects.isNull(dsResult)) {
            return Response.fail("0", "该配置不存在");
        }
        ApplicationDsEnableParam enableParam = new ApplicationDsEnableParam();
        enableParam.setId(enableRequest.getId());
        enableParam.setStatus(enableRequest.getStatus());
        applicationDsDAO.enable(enableParam);

        syncInfo(dsResult.getApplicationId(), dsResult.getApplicationName());
        return Response.success();
    }

    @Override
    public Response dsDelete(ApplicationDsDeleteInput dsDeleteRequest) {
        ApplicationDsResult dsResult = getApplicationDsResult(dsDeleteRequest.getId());
        if (Objects.isNull(dsResult)) {
            return Response.fail("0", "该配置不存在");
        }
        ApplicationDsDeleteParam deleteParam = new ApplicationDsDeleteParam();
        deleteParam.setIdList(Collections.singletonList(dsDeleteRequest.getId()));
        applicationDsDAO.delete(deleteParam);
        syncInfo(dsResult.getApplicationId(), dsResult.getApplicationName());
        return Response.success();
    }

    private void clearCache(String applicationName) {
        agentConfigCacheManager.evictShadowEsServers(applicationName, true);
    }

    private ApplicationDsResult getApplicationDsResult(Long id) {
        return applicationDsDAO.queryByPrimaryKey(id);
    }

}