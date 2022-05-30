package io.shulie.takin.web.biz.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.pamirs.takin.common.exception.ApiException;
import io.shulie.takin.adapter.api.entrypoint.scene.manage.CloudSceneManageApi;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.shulie.takin.web.biz.pojo.response.ApplicationEntryResponse;
import io.shulie.takin.web.biz.service.PressureService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.util.ActivityUtil.EntranceJoinEntity;
import io.shulie.takin.web.data.result.linkmange.BusinessLinkResult;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuchuan
 * @date 2021/10/25 2:51 下午
 */
@Service
public class PressureServiceImpl implements PressureService, AppConstants {

    @Autowired
    private ScriptManageService scriptManageService;

    @Autowired
    private CloudSceneManageApi cloudSceneManageApi;

    @Resource
    private ReportDao reportDao;

    @Override
    public List<ApplicationEntryResponse> getApplicationEntriesByJobId(Long jobId) {
        // 1. 根据 reportId 获得场景
        ReportResult report = reportDao.selectByJobId(jobId);
        if (Objects.isNull(report)) {
            throw ApiException.create(AppConstants.RESPONSE_CODE_FAIL, "报告id不存在!");
        }
        SceneManageIdReq sceneDetailRequest = new SceneManageIdReq();
        sceneDetailRequest.setReportId(report.getId());
        SceneManageWrapperResp sceneDetail = cloudSceneManageApi.getSceneDetailNoAuth(sceneDetailRequest);
        if (sceneDetail == null) {
            throw ApiException.create(AppConstants.RESPONSE_CODE_FAIL, "报告id对应的压测场景不存在!");
        }
        // 获得场景关联的脚本实例id
        // 根据脚本实例id获得业务活动
        TenantCommonExt ext = new TenantCommonExt();
        ext.setTenantId(sceneDetail.getTenantId());
        ext.setEnvCode(sceneDetail.getEnvCode());
        ext.setSource(ContextSourceEnum.AGENT.getCode());
        WebPluginUtils.setTraceTenantContext(ext);
        List<BusinessLinkResult> businessLinkResults = scriptManageService.listBusinessActivityByScriptDeployId(sceneDetail.getScriptId());
        if (businessLinkResults.isEmpty()) {
            return Collections.emptyList();
        }

        // 只处理 http, kafka 的
        // 3. 根据业务获得应用(列表接收)
        // 4. 返回应用的 名称, 入口, 请求方式
        return businessLinkResults.stream().filter(businessLinkResult -> {
            EntranceTypeEnum enumByType = EntranceTypeEnum.getEnumByType(businessLinkResult.getServerMiddlewareType());
            return EntranceTypeEnum.HTTP.name().equals(enumByType.name())
                || EntranceTypeEnum.KAFKA.name().equals(enumByType.name());
        }).map(businessLinkResult -> {
            ApplicationEntryResponse applicationEntryResponse = new ApplicationEntryResponse();
            Integer type = businessLinkResult.getType();
            applicationEntryResponse.setIsVirtual(type);
            EntranceJoinEntity entranceJoinEntity = ActivityUtil.getEntranceJoinEntityByEntranceAndType(
                businessLinkResult.getEntrace(), type);
            applicationEntryResponse.setApplicationName(entranceJoinEntity.getApplicationName());
            applicationEntryResponse.setMethodName(entranceJoinEntity.getMethodName());
            applicationEntryResponse.setServiceName(entranceJoinEntity.getServiceName());
            return applicationEntryResponse;
        }).collect(Collectors.toList());
    }

}
