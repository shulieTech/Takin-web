package io.shulie.takin.web.data.param.scene;

import io.shulie.takin.web.ext.entity.AuthQueryParamCommonExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zhaoyong
 */
@Data
public class ScenePageQueryParam extends AuthQueryParamCommonExt {

    private String sceneName;

    private Integer ignoreType;

    @ApiModelProperty("过滤时间范围")
    private Date queryGmtModified;
}
