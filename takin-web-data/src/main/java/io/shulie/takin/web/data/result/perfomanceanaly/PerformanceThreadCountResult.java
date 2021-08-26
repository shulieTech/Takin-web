package io.shulie.takin.web.data.result.perfomanceanaly;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/11/10 下午3:01
 */
@Data
public class PerformanceThreadCountResult  {

    @JsonProperty("base_id")
    private String baseId;
    /**
     * 这个时间戳，是时间格式化过的，不是Long
     */
    private String timestamp;

    @JsonProperty("thread_count")
    private Integer threadCount;
}
