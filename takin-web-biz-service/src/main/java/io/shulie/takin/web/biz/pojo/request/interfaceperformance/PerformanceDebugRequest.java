package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import lombok.Data;

@Data
public class PerformanceDebugRequest extends PerformanceConfigDebugInput {
    /**
     * 请求条数
     */
    private Integer requestCount;

    /**
     * 关联文件的最大条数
     */
    private Integer relateFileMaxCount = 0;
}
