package io.shulie.takin.cloud.biz.notify.processor.pod;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.shulie.takin.cloud.biz.notify.CloudNotifyParam;
import io.shulie.takin.cloud.model.callback.basic.ResourceExample;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("102")
public class PodStopNotifyParam extends CloudNotifyParam {

    private ResourceExample data;
}
