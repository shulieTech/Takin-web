package io.shulie.takin.web.data.result.perfomanceanaly;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/11/10 下午3:56
 */
@Data
public class PerformanceThreadDataResult {

    @JsonProperty("base_id")
    private Long baseId;

    private String timestamp;

    @JsonProperty("thread_name")
    private String threadName;

    @JsonProperty("thread_status")
    private String threadStatus;

    @JsonProperty("thread_cpu_use_rate")
    private Double threadCpuUseRate;

    @JsonProperty("thread_stack_link")
    private Long threadStackLink;

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        PerformanceThreadDataResult result = (PerformanceThreadDataResult)o;
        return threadName.equals(result.threadName) &&
            threadStatus.equals(result.threadStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(threadName, threadStatus);
    }
}
