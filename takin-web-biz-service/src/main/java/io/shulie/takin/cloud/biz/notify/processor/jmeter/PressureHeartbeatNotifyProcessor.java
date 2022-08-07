package io.shulie.takin.cloud.biz.notify.processor.jmeter;

import javax.annotation.Resource;

import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.notify.CloudNotifyProcessor;
import io.shulie.takin.cloud.model.callback.basic.PressureExample;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import org.springframework.stereotype.Component;

@Component
public class PressureHeartbeatNotifyProcessor extends AbstractIndicators
    implements CloudNotifyProcessor<PressureHeartbeatNotifyParam> {

    @Resource
    private RedisClientUtil redisClientUtil;

    @Override
    public String process(PressureHeartbeatNotifyParam param) {
        PressureExample data = param.getData();
        String resourceId = String.valueOf(data.getResourceId());
        ResourceContext resourceContext = getResourceContext(resourceId);
        if (resourceContext != null) {
            redisClientUtil.hmset(PressureStartCache.getJmeterHeartbeatKey(resourceId),
                String.valueOf(data.getPressureExampleId()), System.currentTimeMillis());
        }
        return resourceId;
    }

    @Override
    public CallbackType type() {
        return CallbackType.PRESSURE_EXAMPLE_HEARTBEAT;
    }
}
