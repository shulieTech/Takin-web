package io.shulie.takin.cloud.biz.notify.processor.jmeter;


import javax.annotation.Resource;

import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.notify.CloudNotifyProcessor;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.model.callback.basic.PressureExample;
import io.shulie.takin.web.common.util.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PressureStopNotifyProcessor extends AbstractIndicators
    implements CloudNotifyProcessor<PressureStopNotifyParam> {

    @Resource
    private RedisClientUtil redisClientUtil;

    @Override
    public String process(PressureStopNotifyParam param) {
        processStopped(param);
        return String.valueOf(param.getData().getResourceId());
    }

    private void processStopped(PressureStopNotifyParam param) {
        PressureExample data = param.getData();
        String resourceId = String.valueOf(data.getResourceId());
        if (!redisClientUtil.hasKey(PressureStartCache.getResourceKey(resourceId))) {
            return;
        }
        String podId = String.valueOf(data.getResourceExampleId());
        String jmeterId = String.valueOf(data.getPressureExampleId());
        ResourceContext context = getResourceContext(resourceId);
        if (redisClientUtil.lockStopFlagExpire(PressureStartCache.getStopFlag(resourceId), "jmeter停止")) {
            notifyStop(context);
        }
        removeSuccessKey(resourceId, podId, jmeterId, param.getTime());
    }

    @Override
    public CallbackType type() {
        return CallbackType.PRESSURE_EXAMPLE_STOP;
    }
}
