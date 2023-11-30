package io.shulie.takin.web.biz.checker;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.web.amdb.util.HttpClientUtil;
import io.shulie.takin.web.biz.pojo.input.sresla.CollectorSlaRequest;
import io.shulie.takin.web.biz.pojo.input.sresla.SlaParamResponse;
import io.shulie.takin.web.biz.service.scene.ApplicationBusinessActivityService;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.data.mapper.mysql.BusinessLinkManageTableMapper;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设置sre的sla
 */
@Slf4j
@Component
public class SreSlaStatusChecker extends AbstractIndicators implements StartConditionChecker {

    @Resource
    private BusinessLinkManageTableMapper businessLinkManageTableMapper;

    @Value("${collector.host: localhost:10086}")
    private String collectorHost;

    @Value("${sre.host: localhost:10086}")
    private String sreHost;

    @Override
    public CheckResult check(StartConditionCheckerContext context) throws TakinCloudException {
        String resourceId = context.getResourceId();
        SceneManageWrapperOutput sceneData = context.getSceneData();
        doCheck(sceneData);
//        return StringUtils.isBlank(resourceId) ? firstCheck(context) : getResourceStatus(resourceId);
        return null;
    }

    private void doCheck(SceneManageWrapperOutput sceneData) {
        List<SceneManageWrapperOutput.SceneBusinessActivityRefOutput> businessActivityConfig = sceneData.getBusinessActivityConfig();
        if (CollectionUtils.isEmpty(businessActivityConfig)){
            return;
        }
        List<Long> ids = businessActivityConfig.stream().map(SceneManageWrapperOutput.SceneBusinessActivityRefOutput::getBusinessActivityId).collect(Collectors.toList());
        List<BusinessLinkManageTableEntity> businessLinkManageTableEntities = businessLinkManageTableMapper.selectBatchIds(ids);
        if (CollectionUtils.isEmpty(businessLinkManageTableEntities)){
            return;
        }

        List<CollectorSlaRequest> collectorParam = getCollectorParam(businessLinkManageTableEntities, businessActivityConfig);
        String sendPost = HttpClientUtil.sendPost(collectorHost + "/api/clickhouse/getSlaParams", collectorParam);
        List<SlaParamResponse> slaParamResponses = JSONObject.parseArray(sendPost, SlaParamResponse.class);
        String sre = HttpClientUtil.sendPost(collectorHost + "/takin-sre/api/risk/pressure/config/sla", slaParamResponses);
    }

    private List<CollectorSlaRequest> getCollectorParam(List<BusinessLinkManageTableEntity> entities, List<SceneManageWrapperOutput.SceneBusinessActivityRefOutput> activityRefOutputs){
        Map<Long, List<SceneManageWrapperOutput.SceneBusinessActivityRefOutput>> longListMap = activityRefOutputs.stream().collect(Collectors.groupingBy(SceneManageWrapperOutput.SceneBusinessActivityRefOutput::getBusinessActivityId));
        entities.forEach(entity -> {
            CollectorSlaRequest collectorSlaRequest = new CollectorSlaRequest();
            collectorSlaRequest.setStartDate(new Date());
            collectorSlaRequest.setEndDate(new Date());
            collectorSlaRequest.setAppName(entity.getApplicationName());
            collectorSlaRequest.setRpc(entity.getApplicationName());
            SceneManageWrapperOutput.SceneBusinessActivityRefOutput sceneBusinessActivityRefOutput = longListMap.get(entity.getLinkId()).get(0);

        });

        return null;
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
