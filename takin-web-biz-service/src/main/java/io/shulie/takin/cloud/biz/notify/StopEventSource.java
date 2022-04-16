package io.shulie.takin.cloud.biz.notify;

import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators.ResourceContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StopEventSource {

    private ResourceContext context;
    private String message;
}
