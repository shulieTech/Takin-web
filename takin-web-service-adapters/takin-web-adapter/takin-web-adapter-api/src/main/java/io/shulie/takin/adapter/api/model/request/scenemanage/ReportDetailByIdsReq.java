package io.shulie.takin.adapter.api.model.request.scenemanage;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2021/2/3 12:03 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportDetailByIdsReq extends ContextExt {
    @ApiModelProperty(name = "reportId", value = "报告ID")
    private List<Long> sceneIds;
}