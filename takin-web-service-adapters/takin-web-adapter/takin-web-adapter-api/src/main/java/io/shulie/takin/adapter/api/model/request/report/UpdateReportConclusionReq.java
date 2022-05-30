package io.shulie.takin.adapter.api.model.request.report;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2021/2/1 6:04 下午
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
public class UpdateReportConclusionReq extends ContextExt {
    private Long id;
    private String errorMessage;
    private Integer conclusion;
}
