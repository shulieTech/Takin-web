package io.shulie.takin.web.data.param.application;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author fanxx
 * @date 2020/10/16 11:04 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationErrorQueryParam extends PagingDevice {

    private String applicationName;

}
