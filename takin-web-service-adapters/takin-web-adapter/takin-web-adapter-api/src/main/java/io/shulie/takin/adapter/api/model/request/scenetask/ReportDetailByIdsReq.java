package io.shulie.takin.adapter.api.model.request.scenetask;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportDetailByIdsReq extends ContextExt {
    @ApiModelProperty(name = "reportId", value = "报告ID")
    private List<Long> sceneIds;
}