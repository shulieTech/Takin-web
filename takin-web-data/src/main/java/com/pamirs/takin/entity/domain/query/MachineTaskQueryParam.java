package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/5/13 上午10:58
 */
@Data
public class MachineTaskQueryParam extends PagingDevice implements Serializable {
    private static final long serialVersionUID = -5236680598754497031L;
    private Integer taskType;
    private Integer taskStatus;
    private String startTime;
    private String endTime;
}
