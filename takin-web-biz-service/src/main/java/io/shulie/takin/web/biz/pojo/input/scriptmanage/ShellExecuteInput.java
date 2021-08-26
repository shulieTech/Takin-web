package io.shulie.takin.web.biz.pojo.input.scriptmanage;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/17 7:25 下午
 */
@Data
public class ShellExecuteInput extends PagingDevice {
    private Long scriptDeployId;
    private String type;

}
