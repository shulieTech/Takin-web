package io.shulie.takin.cloud.biz.notify.processor.pod;

import io.shulie.takin.cloud.model.callback.basic.ResourceExample;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceExampleErrorInfo extends ResourceExample {
    private String errorMessage;
}
