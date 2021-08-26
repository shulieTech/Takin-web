package io.shulie.takin.web.data.param.machine;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-16 11:31
 */

@Data
public class PressureMachineLogQueryParam extends PagingDevice {

    private String startTime;

    private String endTime;

    private Long machineId ;
}
