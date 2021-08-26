package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/18 5:17 下午
 */
@Data
public class SupportJmeterPluginNameRequest {
    /**
     * 关联类型(业务活动)
     */
    @ApiModelProperty("关联类型")
    @JsonProperty("relatedType")
    private String refType;
    @NotNull
    private String relatedType;

    /**
     * 关联值(活动id)
     */
    @ApiModelProperty("关联值")
    @JsonProperty("relatedId")
    private String refValue;
    @NotNull
    private String relatedId;
}
