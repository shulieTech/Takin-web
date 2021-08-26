package io.shulie.takin.web.biz.pojo.input.whitelist;

import lombok.Data;
import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2021/4/13 3:46 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WhitelistSearchInput extends PagingDevice {
    /**
     * 接口名称
     */
    private String interfaceName;
    /**
     * 应用名称
     */
    private String appName;

}
