package io.shulie.takin.web.biz.pojo.request.scriptmanage.shell;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/17 7:25 下午
 */
@Data
public class ShellExecuteRequest extends PagingDevice {
    @JsonProperty("scriptDeployId")
    private Long scriptDeployId;
    /**
     * 0:实时 1：历史执行记录
     */
    @JsonProperty("type")
    private String type;

}
