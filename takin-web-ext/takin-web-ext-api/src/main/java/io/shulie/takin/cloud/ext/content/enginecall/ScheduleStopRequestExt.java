package io.shulie.takin.cloud.ext.content.enginecall;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 莫问
 * @date 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleStopRequestExt extends ScheduleEventRequestExt {

    private String jobName;

    private String engineInstanceRedisKey;
}
