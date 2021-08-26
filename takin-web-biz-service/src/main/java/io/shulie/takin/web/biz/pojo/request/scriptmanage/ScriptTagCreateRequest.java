package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
@ApiModel(value = "场景标签创建模型")
public class ScriptTagCreateRequest implements Serializable {
    private static final long serialVersionUID = -1835666947708446002L;

    /**
     * 标签名称
     */
    @NotNull(message = "标签名称不能为空")
    @ApiModelProperty(value = "标签名称")
    private String tagName;


}
