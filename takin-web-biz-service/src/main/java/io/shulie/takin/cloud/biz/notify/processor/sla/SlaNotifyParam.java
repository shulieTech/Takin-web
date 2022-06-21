package io.shulie.takin.cloud.biz.notify.processor.sla;

import io.shulie.takin.cloud.biz.notify.CloudNotifyParam;
import io.shulie.takin.cloud.model.callback.Sla.SlaInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SlaNotifyParam extends CloudNotifyParam {

    private List<SlaInfo> data;
}
