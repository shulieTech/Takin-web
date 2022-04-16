package io.shulie.takin.cloud.biz.notify.processor.jmeter;

import io.shulie.takin.cloud.model.callback.basic.JobExample;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JobExampleErrorInfo extends JobExample {
    private String errorMessage;
}
