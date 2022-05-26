package com.pamirs.takin.cloud.entity.domain.query;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author fanxx
 * @date 2020/5/13 下午9:23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MachineTaskLogQueryParm extends PagingDevice {
    private Long taskId;
}
