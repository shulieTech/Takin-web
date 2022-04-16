package io.shulie.takin.adapter.api.model.request.statistics;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2020/11/30 9:23 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PressureTotalReq extends ContextExt {
    private String type;
    private String startTime;
    private String endTime;
    /**
     * 压测脚本
     */
    private List<Long> scriptIds;
}
