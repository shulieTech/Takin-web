package io.shulie.takin.web.biz.pojo.response.leakverify;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
@ApiModel(value = "LeakVerifyDeployDetailResponse", description = "漏数实例详情")
@Deprecated
public class LeakVerifyDeployDetailResponse implements Serializable {
    private static final long serialVersionUID = 2203568093674770367L;

    @ApiModelProperty(value = "漏数实例详情id")
    private Long id;

    /**
     * 漏数验证实例id
     */
    @ApiModelProperty(value = "漏数验证实例id")
    private Long leakVerifyDeployId;

    /**
     * 漏数类型
     */
    @ApiModelProperty(value = "漏数类型")
    private String leakType;

    /**
     * 漏数数量
     */
    @ApiModelProperty(value = "漏数数量")
    private Long leakCount;

    /**
     * 漏数内容
     */
    @ApiModelProperty(value = "漏数内容")
    private String leakContent;

}
