package io.shulie.takin.cloud.biz.input.report;

import io.shulie.takin.adapter.api.model.common.SlaBean;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/2/1 6:04 下午
 */
@Data
public class UpdateReportSlaDataInput {
    private Long sceneId;
    private Long reportId;
    private SlaBean slaBean;
}
