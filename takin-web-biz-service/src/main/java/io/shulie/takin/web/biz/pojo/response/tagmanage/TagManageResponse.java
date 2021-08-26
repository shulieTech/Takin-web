package io.shulie.takin.web.biz.pojo.response.tagmanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author shulie
 */
@Data
public class TagManageResponse implements Serializable {
    private static final long serialVersionUID = -6568827266908491681L;

    @ApiModelProperty(value = "标签id")
    private Long id;

    /**
     * 标签名称
     */
    @ApiModelProperty(value = "标签名称")
    private String tagName;
}
