package io.shulie.takin.web.biz.pojo.input.scriptmanage;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ShellScriptManagePageQueryInput extends PagingDevice {
    private static final long serialVersionUID = 4907165876058485892L;

    /**
     * 脚本名称
     */
    private String name;

    /**
     * tagId列表
     */
    private List<Long> tagIds;

    /**
     * 脚本类型
     */
    private Integer scriptType;


}
