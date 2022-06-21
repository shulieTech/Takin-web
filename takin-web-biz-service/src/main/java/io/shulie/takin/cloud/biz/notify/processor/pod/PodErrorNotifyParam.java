package io.shulie.takin.cloud.biz.notify.processor.pod;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.shulie.takin.cloud.biz.notify.CloudNotifyParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("103")
public class PodErrorNotifyParam extends CloudNotifyParam {

    private ResourceExampleErrorInfo data;
}
