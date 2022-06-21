package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import lombok.Data;

@Data
public class PerformanceDebugRequest extends PerformanceConfigDebugInput {
    /**
     * 请求条数
     */
    private Long requestCount;

    /**
     * 关联文件的最大条数,默认为10000条，如果没有文件的时候,有个判断走客户设置的条数
     */
    private Long relateFileMaxCount = 10000L;
}
