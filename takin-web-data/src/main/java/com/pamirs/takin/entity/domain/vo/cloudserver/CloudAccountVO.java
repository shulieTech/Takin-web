package com.pamirs.takin.entity.domain.vo.cloudserver;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-05-09 21:20
 */

@Data
@ApiModel(description = "授权账号vo")
public class CloudAccountVO implements Serializable {

    private static final long serialVersionUID = 3474926756007127572L;

    @ApiModelProperty(name = "id", value = "账号id")
    private Long id;

    @ApiModelProperty(name = "platformId", value = "云平台id")
    private Long platformId;

    @ApiModelProperty(name = "platformName", value = "平台名称")
    private String platformName;

    @ApiModelProperty(name = "account", value = "账号")
    private String account;

    @ApiModelProperty(name = "status", value = "是否启用")
    private Boolean status;

    @ApiModelProperty(name = "isDelete", value = "是否删除")
    private Boolean isDelete;

    @ApiModelProperty(name = "authorizeParam", value = "认证参数")
    private String authorizeParam;

    private Date gmtCreate;

    private Date gmtUpdate;

}
