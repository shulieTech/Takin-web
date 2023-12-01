package io.shulie.takin.web.biz.checker;

import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import com.pamirs.takin.cloud.entity.dao.report.TReportBusinessActivityDetailMapper;
import com.pamirs.takin.cloud.entity.dao.scene.manage.TSceneBusinessActivityRefMapper;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.mapper.mysql.ReportBusinessActivityDetailMapper;
import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneBusinessActivityRefEntity;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.web.biz.pojo.input.sresla.CollectorSlaRequest;
import io.shulie.takin.web.biz.pojo.input.sresla.SreSlaParamReq;
import io.shulie.takin.web.biz.pojo.input.sresla.SreSyncLinkReq;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse;
import io.shulie.takin.web.biz.utils.SreHelper;
import io.shulie.takin.web.common.SreResponse;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.data.mapper.mysql.BusinessLinkManageTableMapper;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 设置sre的sla
 */
@Slf4j
@Component
public class SreSlaStatusChecker extends AbstractIndicators implements StartConditionChecker {

    @Resource
    private BusinessLinkManageTableMapper businessLinkManageTableMapper;
    @Resource
    private ReportBusinessActivityDetailMapper detailMapper;
    @Resource
    private ReportDao reportDao;

    @Value("${collector.host: localhost:10086}")
    private String collectorHost;

    @Value("${sre.host: localhost:10086}")
    private String sreHost;

    @Override
    public CheckResult check(StartConditionCheckerContext context) throws TakinCloudException {
        SceneManageWrapperOutput sceneData = context.getSceneData();
        Long reportId = context.getReportId();
        try {
            doCheck(sceneData, reportId);
        } catch (Exception e) {
            return CheckResult.fail(type(), e.getMessage());
        }
        return CheckResult.success(type());
    }

    private void doCheck(SceneManageWrapperOutput sceneData, Long reportId) {
        List<SceneManageWrapperOutput.SceneBusinessActivityRefOutput> businessActivityConfig = sceneData.getBusinessActivityConfig();
        if (CollectionUtils.isEmpty(businessActivityConfig)) {
            log.info("压测启动，关联关系表中没有找到对应的业务活动，不需要进行sla设置");
            return;
        }
        List<Long> ids = businessActivityConfig.stream().map(SceneManageWrapperOutput.SceneBusinessActivityRefOutput::getBusinessActivityId).collect(Collectors.toList());
        List<BusinessLinkManageTableEntity> businessLinkManageTableEntities = businessLinkManageTableMapper.selectBatchIds(ids);
        if (CollectionUtils.isEmpty(businessLinkManageTableEntities)) {
            log.info("压测启动，业务活动表中没有找到对应的业务活动，不需要进行sla设置");
            return;
        }
        //去除掉没有应用名的业务活动，也就是虚拟业务活动
        businessLinkManageTableEntities = businessLinkManageTableEntities.stream().filter(o -> StringUtils.isNotBlank(o.getApplicationName())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(businessLinkManageTableEntities)) {
            log.info("压测启动，业务活动表中没有找到非虚拟的业务活动，不需要进行sla设置");
            return;
        }
        List<ReportBusinessActivityDetailEntity> detailsByReportId = reportDao.getReportBusinessActivityDetailsByReportId(reportId, NodeTypeEnum.SAMPLER);
        //是否自动设置sla
        List<SreSlaParamReq> params = new ArrayList<>();
        List<SreSyncLinkReq> sreSyncLinkReqs = new ArrayList<>();
        List<CollectorSlaRequest> collectorParam = getCollectorParam(businessLinkManageTableEntities, businessActivityConfig, true);
        collectorParam.forEach(collectorSlaRequest -> {
            TypeToken<SreResponse<List<SreSlaParamReq>>> typeToken = new TypeToken<SreResponse<List<SreSlaParamReq>>>() {
            };
            SreResponse<List<SreSlaParamReq>> response = SreHelper.builder().url(collectorHost + "/api/clickhouse/getSlaParams")
                    .httpMethod(HttpMethod.POST).param(collectorParam).queryList(typeToken);

            if (!response.isSuccess()) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR, "设置sla请求trace出现异常:" + response.getErrorMsg());
            }

            if (CollectionUtils.isNotEmpty(response.getData())) {
                //设置参数
                params.addAll(response.getData());
                //将chainCode设置到压测报告业务活动中
                String chainCode = response.getData().get(0).getChainCode();
                ReportBusinessActivityDetailEntity detailEntity = detailsByReportId.stream().filter(o -> o.getBusinessActivityId().equals(collectorSlaRequest.getRefId())).findFirst().get();
                ActivityResponse activityResponse = new ActivityResponse();
                if (StringUtils.isNotBlank(detailEntity.getReportJson())) {
                    activityResponse = JSON.parseObject(detailEntity.getReportJson(), ActivityResponse.class);
                }
                activityResponse.setChainCode(chainCode);
                detailEntity.setReportJson(JSON.toJSONString(activityResponse));
                detailMapper.updateById(detailEntity);

                SreSyncLinkReq sreSyncLinkReq = new SreSyncLinkReq();
                sreSyncLinkReq.setChainCode(chainCode);
                sreSyncLinkReq.setChainName(sceneData.getPressureTestSceneName() + chainCode);
                sreSyncLinkReq.setChainCnName(sceneData.getPressureTestSceneName() + "-" + collectorSlaRequest.getRpc());
                sreSyncLinkReq.setBiz("sync");
                sreSyncLinkReq.setSystem("sysEye");
                sreSyncLinkReq.setEntranceServiceId(collectorSlaRequest.getRpc());
                sreSyncLinkReq.setEntranceAppName(collectorSlaRequest.getAppName());
                sreSyncLinkReq.setOwner("takin-web");
                sreSyncLinkReq.setImportant(true);
                sreSyncLinkReq.setBizPriority("P1");
                sreSyncLinkReq.setCostTarget(200);
                sreSyncLinkReq.setTenantCode(sceneData.getTenantCode());
                sreSyncLinkReq.setEnvCode(sceneData.getEnvCode());
                sreSyncLinkReqs.add(sreSyncLinkReq);
            }
        });
        //设置sla
        TypeToken<SreResponse<Object>> typeToken = new TypeToken<SreResponse<Object>>() {
        };
        SreResponse<Object> response = SreHelper.builder().url(sreHost + "/takin-sre/api/risk/pressure/config/sla")
                .httpMethod(HttpMethod.POST).param(params).queryList(typeToken);
        if (!response.isSuccess()) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR, "设置sla失败:" + response.getErrorMsg());
        }
        //同步链路接口,sreSyncLinkReqs
        TypeToken<SreResponse<Object>> syncLink = new TypeToken<SreResponse<Object>>() {
        };
        SreResponse<Object> syncLinkResponse = SreHelper.builder().url(sreHost + "/takin-sre/api/risk/pressure/config/chain")
                .httpMethod(HttpMethod.POST).param(sreSyncLinkReqs).queryList(syncLink);
        if (!syncLinkResponse.isSuccess()) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR, "设置sla失败:" + syncLinkResponse.getErrorMsg());
        }
    }

    private List<CollectorSlaRequest> getCollectorParam(List<BusinessLinkManageTableEntity> entities, List<SceneManageWrapperOutput.SceneBusinessActivityRefOutput> activityRefOutputs, boolean autoSetSla) {
        List<CollectorSlaRequest> collectorSlaRequests = new ArrayList<>();
        Map<Long, List<SceneManageWrapperOutput.SceneBusinessActivityRefOutput>> longListMap = activityRefOutputs.stream().collect(Collectors.groupingBy(SceneManageWrapperOutput.SceneBusinessActivityRefOutput::getBusinessActivityId));
        entities.forEach(entity -> {
            CollectorSlaRequest collectorSlaRequest = new CollectorSlaRequest();
            collectorSlaRequest.setAppName(entity.getApplicationName());
            ActivityUtil.EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertEntrance(entity.getEntrace());
            collectorSlaRequest.setRpc(entranceJoinEntity.getServiceName());
            SceneManageWrapperOutput.SceneBusinessActivityRefOutput sceneBusinessActivityRefOutput = longListMap.get(entity.getLinkId()).get(0);
            collectorSlaRequest.setRefId(sceneBusinessActivityRefOutput.getId());
            if (autoSetSla) {
                Calendar instance = Calendar.getInstance();
                instance.add(Calendar.HOUR_OF_DAY, -24);
                collectorSlaRequest.setStartDate(instance.getTime());
                collectorSlaRequest.setEndDate(new Date());
            } else {
                collectorSlaRequest.setStartDate(sceneBusinessActivityRefOutput.getSlaStartTime());
                collectorSlaRequest.setEndDate(sceneBusinessActivityRefOutput.getSlaEndTime());
            }
            collectorSlaRequests.add(collectorSlaRequest);
        });
        return collectorSlaRequests;
    }


    @Override
    public String type() {
        return "sla";
    }

    @Override
    public int getOrder() {
        return 5;
    }
}
