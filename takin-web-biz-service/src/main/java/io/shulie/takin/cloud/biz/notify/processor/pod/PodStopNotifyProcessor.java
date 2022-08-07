package io.shulie.takin.cloud.biz.notify.processor.pod;

import java.util.Objects;

import javax.annotation.Resource;

import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.notify.CloudNotifyProcessor;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.model.callback.basic.ResourceExample;
import io.shulie.takin.web.common.util.RedisClientUtil;
import org.springframework.stereotype.Component;

@Component
public class PodStopNotifyProcessor extends AbstractIndicators implements CloudNotifyProcessor<PodStopNotifyParam> {

    @Resource
    private RedisClientUtil redisClientUtil;

    @Override
    public CallbackType type() {
        return CallbackType.RESOURCE_EXAMPLE_STOP;
    }

    @Override
    public String process(PodStopNotifyParam param) {
        processStop(param);
        return String.valueOf(param.getData().getResourceId());
    }

    private void processStop(PodStopNotifyParam param) {
        ResourceExample data = param.getData();
        String resourceId = String.valueOf(data.getResourceId());
        if (!redisClientUtil.hasKey(PressureStartCache.getResourceKey(resourceId))) {
            return;
        }
        ResourceContext context = getResourceContext(resourceId);
        if (!redisClientUtil.hasKey(PressureStartCache.getJmeterStartFirstKey(resourceId))) {
            callStartFailedEvent(resourceId, "pod停止");
            return;
        }
        if (redisClientUtil.lockStopFlagExpire(PressureStartCache.getStopFlag(resourceId), "pod停止")) {
            notifyStop(context);
        }
        Long jmeterId = data.getPressureExampleId();
        removeSuccessKey(resourceId, String.valueOf(data.getResourceExampleId()),
            Objects.isNull(jmeterId) ? "" : String.valueOf(jmeterId), param.getTime());
    }
}
