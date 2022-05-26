package io.shulie.takin.cloud.biz.input.report;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/2/1 6:04 下午
 */
@Data
public class UpdateReportConclusionInput {
    private Long id;
    private String errorMessage;
    private Integer conclusion;
}
