package io.shulie.takin.cloud.biz.notify.processor.pod;

import java.util.Objects;

import javax.annotation.Resource;

import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.notify.CloudNotifyProcessor;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.web.common.util.RedisClientUtil;
import org.springframework.stereotype.Component;

@Component
public class PodErrorNotifyProcessor extends AbstractIndicators implements CloudNotifyProcessor<PodErrorNotifyParam> {

    @Resource
    private RedisClientUtil redisClientUtil;

    @Override
    public CallbackType type() {
        return CallbackType.RESOURCE_EXAMPLE_ERROR;
    }

    @Override
    public String process(PodErrorNotifyParam param) {
        processError(param);
        return String.valueOf(param.getData().getResourceId());
    }

    private void processError(PodErrorNotifyParam param) {
        ResourceExampleErrorInfo data = param.getData();
        String resourceId = String.valueOf(data.getResourceId());
        if (!redisClientUtil.hasKey(PressureStartCache.getResourceKey(resourceId))) {
            return;
        }
        updatePressureTaskMessageByResourceId(resourceId, data.getErrorMessage());
        String podId = String.valueOf(data.getResourceExampleId());
        if (redisClientUtil.lockNoExpire(PressureStartCache.getPodErrorFirstKey(resourceId), podId)) {
            callRunningFailedEvent(resourceId, data.getErrorMessage());
        }
        Long jmeterId = data.getPressureExampleId();
        removeSuccessKey(resourceId, podId, Objects.isNull(jmeterId) ? "" : String.valueOf(jmeterId), param.getTime());
    }
}
