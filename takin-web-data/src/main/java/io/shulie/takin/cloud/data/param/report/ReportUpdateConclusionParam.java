package io.shulie.takin.cloud.data.param.report;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/2/1 7:18 下午
 */
@Data
public class ReportUpdateConclusionParam {
    private Long id;

    /**
     * 压测结论: 0/不通过，1/通过
     */
    private Integer conclusion;

    /**
     * 扩展字段，JSON数据格式
     */
    private String features;
}
