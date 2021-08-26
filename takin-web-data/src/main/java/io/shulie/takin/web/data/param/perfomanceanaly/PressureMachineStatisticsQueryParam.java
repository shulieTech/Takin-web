package io.shulie.takin.web.data.param.perfomanceanaly;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;


/**
 * @author mubai
 * @date 2020-11-13 11:46
 */

@Data
public class PressureMachineStatisticsQueryParam extends PagingDevice {

    private String startTime;

    private String endTime;


}
