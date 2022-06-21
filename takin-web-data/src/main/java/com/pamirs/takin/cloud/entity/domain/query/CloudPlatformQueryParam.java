package com.pamirs.takin.cloud.entity.domain.query;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author mubai
 * @date 2020-05-11 11:34
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class CloudPlatformQueryParam extends PagingDevice {

    private Long id;

    private String name;

    private Boolean status;

    private String jarName;

}
