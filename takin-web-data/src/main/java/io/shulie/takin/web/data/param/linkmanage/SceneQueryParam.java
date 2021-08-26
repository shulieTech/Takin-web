package io.shulie.takin.web.data.param.linkmanage;

import io.shulie.takin.web.ext.entity.AuthQueryParamCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/15 11:03 上午
 */
@Data
public class SceneQueryParam extends AuthQueryParamCommonExt {
    private String sceneName;
}
