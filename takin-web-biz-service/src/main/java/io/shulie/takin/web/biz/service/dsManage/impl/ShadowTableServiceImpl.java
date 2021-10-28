package io.shulie.takin.web.biz.service.dsManage.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Lists;
import com.pamirs.attach.plugin.dynamic.Type;
import com.pamirs.takin.common.constant.AppAccessTypeEnum;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.result.application.AppShadowDatabaseDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationBizTableDTO;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.convert.db.parser.DbTemplateParser;
import io.shulie.takin.web.biz.init.sync.ConfigSyncService;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsDeleteInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsEnableInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationDsDetailOutput;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.dsManage.AbstractDsService;
import io.shulie.takin.web.biz.utils.DsManageUtil;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDsDAO;
import io.shulie.takin.web.data.param.application.ApplicationDsCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationDsDeleteParam;
import io.shulie.takin.web.data.param.application.ApplicationDsEnableParam;
import io.shulie.takin.web.data.param.application.ApplicationDsQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationDsUpdateParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDsResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HengYu
 * @className ShadowTableServiceImpl
 * @date 2021/4/12 9:25 下午
 * @description 影子表存储服务
 */
@Component
@Slf4j
public class ShadowTableServiceImpl extends AbstractDsService {

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
    private ApplicationClient applicationClient;

    @Autowired
    private DbTemplateParser dbTemplateParser;

    @Override
    public Response dsAdd(ApplicationDsCreateInput createRequest) {
        ApplicationDetailResult applicationDetailResult = applicationDAO.getApplicationById(
                createRequest.getApplicationId());

        Response response = validator(applicationDetailResult, createRequest);
        if (response != null) {
            return response;
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
        syncInfo(applicationDetailResult.getApplicationId(), createParam.getApplicationName());
        applicationDsDAO.insert(createParam);
        return Response.success();
    }

    private void addParserConfig(ApplicationDsCreateInput createRequest, ApplicationDsCreateParam createParam) {
        String url = DsManageUtil.parseShadowTableUrl(createRequest.getUrl());
        String config = createRequest.getConfig();
        String parsedConfig = parseShadowTableConfig(config);
        createParam.setUrl(url);
        createParam.setConfig(parsedConfig);
        createParam.setParseConfig(parsedConfig);
    }

    private void syncInfo(Long applicationId, String applicationName) {
        //同步配置
        configSyncService.syncShadowDB(WebPluginUtils.getTenantUserAppKey(), applicationId, null);
        //修改应用状态
        applicationService.modifyAccessStatus(String.valueOf(applicationId),
                AppAccessTypeEnum.UNUPLOAD.getValue(), null);

        agentConfigCacheManager.evictShadowDb(applicationName);
    }

    private Response validator(
            ApplicationDetailResult applicationDetailResult,
            ApplicationDsCreateInput createRequest) {
        if (applicationDetailResult == null) {
            return Response.fail("0", "应用不存在!");
        }
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

        //新增影子表配置
        if (dsTypeList.contains(DsTypeEnum.SHADOW_DB.getCode())) {
            return Response.fail("0", "创建影子库表配置失败, 不能出现不同类型(库/表)");
        }

        String url = DsManageUtil.parseShadowTableUrl(createRequest.getUrl());

        ApplicationDsQueryParam queryParam = new ApplicationDsQueryParam();
        queryParam.setApplicationId(createRequest.getApplicationId());
        queryParam.setUrl(url);
        List<ApplicationDsResult> currentDsResultList = applicationDsDAO.queryList(queryParam);
        if (CollectionUtils.isNotEmpty(currentDsResultList)) {
            return Response.fail("0", "影子表配置已存在");
        }

        return null;
    }

    @Override
    public Response dsUpdate(ApplicationDsUpdateInput updateRequest) {
        ApplicationDsResult dsResult = applicationDsDAO.queryByPrimaryKey(updateRequest.getId());
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

    private void updateParserConfig(ApplicationDsUpdateInput updateRequest, ApplicationDsUpdateParam updateParam) {
        String config = updateRequest.getConfig();
        String parsedConfig = parseShadowTableConfig(config);
        updateParam.setUrl(DsManageUtil.parseShadowTableUrl(updateRequest.getUrl()));
        updateParam.setConfig(parsedConfig);
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

        queryParserConfig(dsResult, dsDetailResponse);
        return Response.success(dsDetailResponse);
    }

    private void queryParserConfig(ApplicationDsResult dsResult, ApplicationDsDetailOutput dsDetailResponse) {
        String config = dsResult.getConfig();
        String configStr = dsResult.getConfig();
        if (StringUtils.isNotBlank(configStr)) {
            StringBuilder rawConfigBuilder = new StringBuilder();
            String[] configItems = configStr.split(",");
            for (String item : configItems) {
                String table = item.trim();
                table = table.toUpperCase();
                table = "PT_" + table;
                rawConfigBuilder.append(table);
                rawConfigBuilder.append(",");
            }
            // configItems空数组，导致rawConfigBuilder为空串报错
            if (StringUtils.isNotBlank(rawConfigBuilder.toString())) {
                config = rawConfigBuilder.substring(0, rawConfigBuilder.length() - 1);
            }
        }
        dsDetailResponse.setConfig(config);
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

        syncInfo(dsResult.getApplicationId(), dsResult.getApplicationName());

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

        syncInfo(dsResult.getApplicationId(), dsResult.getApplicationName());
        return Response.success();
    }

    private String parseShadowTableConfig(String config) {
        StringBuilder parsedConfigBuilder = new StringBuilder();
        String[] configItems = config.split(",");
        for (String item : configItems) {
            String table = item.trim();
            table = table.toUpperCase();
            if (table.startsWith("`")) {
                table = table.substring(1);
            }
            if (table.endsWith("`")) {
                table = table.substring(0, table.length() - 1);
            }
            if (table.startsWith("pt_") || table.startsWith("PT_")) {
                table = table.substring(3);
            }
            parsedConfigBuilder.append(table);
            parsedConfigBuilder.append(",");
        }
        String parsedConfig = parsedConfigBuilder.toString();
        if (parsedConfig.endsWith(",")) {
            parsedConfig.substring(0, parsedConfigBuilder.length() - 1);
        }
        return parsedConfig;
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

        Map<String, ShadowDetailResponse.TableInfo> map = new HashMap<>();
        String configStr = dsResult.getConfig();
        if (StringUtils.isNotBlank(configStr)) {
            String[] configItems = configStr.split(",");
            for (String item : configItems) {
                ShadowDetailResponse.TableInfo info = new ShadowDetailResponse.TableInfo();
                info.setBizTableName(item);
                info.setIsCheck(true);
                info.setIsManual(true);
                info.setShaDowTableName(PREFIX + info.getBizTableName());
                map.put(item, info);
            }
        }

        List<ShadowDetailResponse.TableInfo> list = new ArrayList<>();
        list.addAll(map.values());

        ShadowDetailResponse response = new ShadowDetailResponse();
        response.setId(recordId);
        response.setApplicationId(String.valueOf(dsResult.getApplicationId()));
        response.setMiddlewareType(Type.MiddleWareType.LINK_POOL.value());
        response.setDsType(dsResult.getDsType());
        response.setUrl(dsResult.getUrl());
        String poolName = "兼容老版本(影子表)";
        response.setConnectionPool(poolName);
        response.setUsername("-");
        response.setPassword("");
        response.setTables(list);

        String shadowInfo = "";
        List<AppShadowDatabaseDTO> amdbInfo = applicationClient.getApplicationShadowDataBaseInfo(dsResult.getApplicationName(), dsResult.getUrl());
        if (CollectionUtils.isNotEmpty(amdbInfo)) {
            AppShadowDatabaseDTO dto = amdbInfo.get(0);
            shadowInfo = dbTemplateParser.convertData(dto.getAttachment(), "druid");
        }
        response.setShadowInfo(shadowInfo);
        return response;
    }

}
