package com.pamirs.takin.cloud.entity.domain.query;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author fanxx
 * @date 2020/5/13 上午10:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MachineTaskQueryParam extends PagingDevice {
    private Integer taskType;
    private Integer taskStatus;
    private String startTime;
    private String endTime;
}
