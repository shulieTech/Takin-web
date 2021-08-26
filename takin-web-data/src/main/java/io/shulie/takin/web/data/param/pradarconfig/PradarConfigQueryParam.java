package io.shulie.takin.web.data.param.pradarconfig;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author junshao
 * @date 2021/07/08 2:35 下午
 */
@Data
public class PradarConfigQueryParam extends PagingDevice {

    private Long id;

    private String startTime;

    private String endTime;

    private String zkPath;

    private String type;

    private String value;

    private String remark;
}
