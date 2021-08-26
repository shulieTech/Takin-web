package io.shulie.takin.web.data.param.scriptmanage.shell;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/17 7:25 下午
 */
@Data
public class ShellExecuteParam extends PagingDevice {
    private Long scriptId;
}
