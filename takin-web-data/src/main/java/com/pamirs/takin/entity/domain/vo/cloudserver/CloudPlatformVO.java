package com.pamirs.takin.entity.domain.vo.cloudserver;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-05-09 20:45
 */

@Data
public class CloudPlatformVO implements Serializable {

    private static final long serialVersionUID = 5809341599213407921L;

    @ApiModelProperty(name = "id", value = "平台id")
    private Long id;

    @ApiModelProperty(name = "name", value = "平台名称")
    private String name;

    @ApiModelProperty(name = "jarName", value = "jar包名称")
    private String jarName;

    @ApiModelProperty(name = "classPath", value = "实现类全路径")
    private String classPath;

    @ApiModelProperty(name = "status", value = "是否启用")
    private Boolean status;

    @ApiModelProperty(name = "isDelete", value = "是否删除")
    private Boolean isDelete;

    @ApiModelProperty(name = "authorizeParam", value = "认证参数")
    private String authorizeParam;

    @ApiModelProperty(name = "authorizeTemplate", value = "认证参数模版")
    private String authorizeTemplate;

    private String gmtCreate;

    private String gmtUpdate;

}
