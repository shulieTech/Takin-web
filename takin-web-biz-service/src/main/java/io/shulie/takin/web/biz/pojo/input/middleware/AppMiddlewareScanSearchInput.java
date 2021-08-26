package io.shulie.takin.web.biz.pojo.input.middleware;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/2/24 5:08 下午
 */
@Data
public class AppMiddlewareScanSearchInput extends PagingDevice {
    private String appName;
    private Integer status;
    private String type;
}
