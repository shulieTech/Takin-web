package io.shulie.takin.cloud.biz.notify.processor.jmeter;

import javax.annotation.Resource;

import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.notify.CloudNotifyProcessor;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.web.common.util.RedisClientUtil;
import org.springframework.stereotype.Component;

@Component
public class PressureErrorNotifyProcessor extends AbstractIndicators
    implements CloudNotifyProcessor<PressureErrorNotifyParam> {

    @Resource
    private RedisClientUtil redisClientUtil;

    @Override
    public String process(PressureErrorNotifyParam param) {
        processError(param);
        return String.valueOf(param.getData().getResourceId());
    }

    private void processError(PressureErrorNotifyParam param) {
        PressureExampleErrorInfo data = param.getData();
        String resourceId = String.valueOf(data.getResourceId());
        if (!redisClientUtil.hasKey(PressureStartCache.getResourceKey(resourceId))) {
            return;
        }
        updatePressureTaskMessageByResourceId(resourceId, data.getErrorMessage());
        String jmeterId = String.valueOf(data.getPressureExampleId());
        if (redisClientUtil.lockNoExpire(PressureStartCache.getJmeterErrorFirstKey(resourceId), jmeterId)) {
            callRunningFailedEvent(String.valueOf(data.getResourceId()), data.getErrorMessage());
        }
        removeSuccessKey(resourceId, String.valueOf(data.getResourceExampleId()), jmeterId, param.getTime());
    }

    @Override
    public CallbackType type() {
        return CallbackType.PRESSURE_EXAMPLE_ERROR;
    }
}
