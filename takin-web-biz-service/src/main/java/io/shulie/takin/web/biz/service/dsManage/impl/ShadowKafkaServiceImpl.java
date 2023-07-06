package io.shulie.takin.web.biz.service.dsManage.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSON;

import com.pamirs.takin.common.constant.AppAccessTypeEnum;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsDeleteInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsEnableInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationDsDetailOutput;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @description:影子kafka集群
 * @date 2021/4/21 10:16 上午
 */
@Component
public class ShadowKafkaServiceImpl extends AbstractDsService {

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

        ApplicationDetailResult applicationDetailResult = applicationDAO.getApplicationById(createRequest.getApplicationId());

        Response fail = validator(createRequest, applicationDetailResult);
        if (fail != null) {return fail;}

        ApplicationDsCreateParam createParam = new ApplicationDsCreateParam();

        addParserConfig(createRequest, createParam);

        createParam.setApplicationId(createRequest.getApplicationId());
        createParam.setApplicationName(applicationDetailResult.getApplicationName());
        createParam.setDbType(createRequest.getDbType());
        createParam.setDsType(Integer.parseInt(String.valueOf(createRequest.getDsType())));
        WebPluginUtils.fillUserData(createParam);
        createParam.setStatus(createRequest.getStatus());

        syncInfo(createRequest.getApplicationId(), createParam.getApplicationName());
        // 新增配置
        applicationDsDAO.insert(createParam);
        return Response.success();
    }

    private Response validator(ApplicationDsCreateInput createRequest,
        ApplicationDetailResult applicationDetailResult) {
        logger.warn("应用不存在! id:{}", createRequest.getApplicationId());
        if (applicationDetailResult == null) {
            return Response.fail("应用不存在!");
        }
        return null;
    }

    public void addParserConfig(ApplicationDsCreateInput createRequest, ApplicationDsCreateParam createParam) {
        String config = createRequest.getConfig();
        Map<String, String> map = parseConfig(config);
        String topic = map.get("topic");
        createParam.setUrl(topic);
        createParam.setConfig(config);
        createParam.setParseConfig(createRequest.getConfig());
    }

    private void syncInfo(Long applicationId, String applicationName) {
        //修改应用状态
        applicationService.modifyAccessStatus(String.valueOf(applicationId),
            AppAccessTypeEnum.UNUPLOAD.getValue(), null);
        syncShadowKafka(applicationId, null);
        clearCache(applicationName);
    }

    private void clearCache(String applicationName) {
        //todo agent改造点
        agentConfigCacheManager.evictShadowKafkaCluster(applicationName, true);
    }

    private void syncShadowKafka(Long applicationId, String o) {
        //todo 核对同步配置
        //configSyncService.syncShadowDB(RestContext.getUser().getKey(), applicationId, o);
    }

    private Map<String, String> parseConfig(String config) {
        // config 解密密码
        return JSON.parseObject(config, Map.class);
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
        syncInfo(dsResult.getApplicationId(), dsResult.getApplicationName());
        applicationDsDAO.update(updateParam);
        return Response.success();
    }

    public void updateParserConfig(ApplicationDsUpdateInput updateRequest, ApplicationDsUpdateParam updateParam) {
        String config = updateRequest.getConfig();
        Map<String, String> map = parseConfig(config);
        String topic = map.get("topic");
        updateParam.setUrl(topic);
        updateParam.setConfig(config);
        updateParam.setParseConfig(config);
    }

    @Override
    public Response<ApplicationDsDetailOutput> dsQueryDetail(Long dsId, boolean isOldVersion) {
        ApplicationDsResult dsResult = getApplicationDsResult(dsId);

        if (Objects.isNull(dsResult)) {
            return Response.fail("该影子配置不存在");
        }
        ApplicationDsDetailOutput dsDetailResponse = new ApplicationDsDetailOutput();
        dsDetailResponse.setId(dsResult.getId());
        dsDetailResponse.setApplicationId(dsResult.getApplicationId());
        dsDetailResponse.setApplicationName(dsResult.getApplicationName());
        dsDetailResponse.setDbType(dsResult.getDbType());
        dsDetailResponse.setDsType(dsResult.getDsType());

        queryParserConfig(dsResult, dsDetailResponse);
        return Response.success(dsDetailResponse);
    }

    private void queryParserConfig(ApplicationDsResult dsResult, ApplicationDsDetailOutput dsDetailResponse) {
        dsDetailResponse.setUrl(dsResult.getUrl());
        String config = dsResult.getConfig();
        dsDetailResponse.setConfig(config);
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

    private ApplicationDsResult getApplicationDsResult(Long id) {
        return applicationDsDAO.queryByPrimaryKey(id);
    }

}