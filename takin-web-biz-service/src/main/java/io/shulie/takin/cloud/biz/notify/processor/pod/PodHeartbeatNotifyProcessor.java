package io.shulie.takin.cloud.biz.notify.processor.pod;

import javax.annotation.Resource;

import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.notify.CloudNotifyProcessor;
import io.shulie.takin.cloud.model.callback.basic.PressureExample;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import org.springframework.stereotype.Component;

@Component
public class PodHeartbeatNotifyProcessor extends AbstractIndicators
    implements CloudNotifyProcessor<PodHeartbeatNotifyParam> {

    @Resource
    private RedisClientUtil redisClientUtil;

    @Override
    public String process(PodHeartbeatNotifyParam param) {
        PressureExample data = param.getData();
        String resourceId = String.valueOf(data.getResourceId());
        ResourceContext resourceContext = getResourceContext(resourceId);
        if (resourceContext != null) {
            String podId = String.valueOf(data.getPressureExampleId());
            redisClientUtil.hmset(PressureStartCache.getPodHeartbeatKey(resourceId), podId, System.currentTimeMillis());
        }
        return resourceId;
    }

    @Override
    public CallbackType type() {
        return CallbackType.RESOURCE_EXAMPLE_HEARTBEAT;
    }
}
