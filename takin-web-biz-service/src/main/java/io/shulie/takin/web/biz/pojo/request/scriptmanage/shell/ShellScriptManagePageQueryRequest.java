package io.shulie.takin.web.biz.pojo.request.scriptmanage.shell;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/9 7:04 下午
 */
@Data
public class ShellScriptManagePageQueryRequest extends PagingDevice {
    private static final long serialVersionUID = 4907165876058485892L;

    /**
     * 脚本名称
     */
    @JsonProperty("scriptName")
    private String name;

    /**
     * tagId列表
     */
    @JsonProperty("tags")
    private List<Long> tagIds;

}
