package io.shulie.takin.web.data.param.scene;

import io.shulie.takin.web.ext.entity.AuthQueryParamCommonExt;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ScenePageQueryParam extends AuthQueryParamCommonExt {

    private String sceneName;

    private Integer ignoreType;
}
