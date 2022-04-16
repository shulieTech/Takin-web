package com.pamirs.takin.cloud.entity.domain.query;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author mubai
 * @date 2020-05-11 11:35
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class CloudAccountQueryParam extends PagingDevice {

    /**
     * 平台id
     */
    private Long platformId;

}
