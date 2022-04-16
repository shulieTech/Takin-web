package io.shulie.takin.cloud.biz.notify.processor.sla;

import io.shulie.takin.cloud.biz.notify.CloudNotifyProcessor;
import io.shulie.takin.cloud.biz.service.sla.SlaService;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.model.callback.Sla.SlaInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SlaNotifyProcessor implements CloudNotifyProcessor<SlaNotifyParam> {

    @Autowired
    private SlaService slaService;

    @Override
    public CallbackType type() {
        return CallbackType.SLA;
    }

    @Override
    public String process(SlaNotifyParam param) {
        List<SlaInfo> data = param.getData();
        String resourceId = "";
        if (CollectionUtils.isNotEmpty(data)) {
            slaService.detection(param.getData());
            resourceId = String.valueOf(data.get(0).getResourceId());
        }
        return resourceId;
    }
}
