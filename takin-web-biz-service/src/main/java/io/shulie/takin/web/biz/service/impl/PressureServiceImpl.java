package io.shulie.takin.web.biz.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.pamirs.takin.common.exception.ApiException;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.shulie.takin.web.biz.pojo.response.ApplicationEntryResponse;
import io.shulie.takin.web.biz.service.PressureService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.util.ActivityUtil.EntranceJoinEntity;
import io.shulie.takin.web.data.result.linkmange.BusinessLinkResult;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
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
    private SceneManageApi sceneManageApi;

    @Override
    public List<ApplicationEntryResponse> getApplicationEntriesByReportId(Long reportId) {
        // 1. 根据 reportId 获得场景
        SceneManageIdReq sceneDetailRequest = new SceneManageIdReq();
        sceneDetailRequest.setReportId(reportId);
        ResponseResult<SceneManageWrapperResp> sceneDetailResponse = sceneManageApi.getSceneDetail(sceneDetailRequest);
        if (sceneDetailResponse == null || sceneDetailResponse.getData() == null) {
            throw ApiException.create(AppConstants.RESPONSE_CODE_FAIL, "报告id对应的压测场景不存在!");
        }

        SceneManageWrapperResp sceneDetail = sceneDetailResponse.getData();
        // 获得场景关联的脚本实例id
        // 根据脚本实例id获得业务活动
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
