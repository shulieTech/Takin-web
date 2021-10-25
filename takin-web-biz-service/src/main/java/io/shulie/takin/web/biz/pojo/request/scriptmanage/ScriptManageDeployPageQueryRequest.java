package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaoyong
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ScriptManageDeployPageQueryRequest extends PagingDevice {
    private static final long serialVersionUID = 4907165876058485892L;

    /**
     * 脚本id/版本id
     */
    @JsonProperty("scriptId")
    private Long scriptId;

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

    /**
     * 业务活动id
     */
    @JsonProperty("businessActivity")
    private String businessActivityId;

    /**
     * 业务流程id
     */
    @JsonProperty("businessFlow")
    private String businessFlowId;

}
