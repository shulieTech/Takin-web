package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ScriptTagCreateRefRequest implements Serializable {
    private static final long serialVersionUID = -1835666947708446002L;

    /**
     * 脚本发布实例id
     */

    @JsonProperty("scriptId")
    @NotNull(message = "脚本id不能为空")
    private Long scriptDeployId;

    /**
     * 标签id列表
     */
    private List<String> tagNames;
}
