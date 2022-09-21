package io.shulie.takin.web.data.param.scene;

import io.shulie.takin.web.ext.entity.AuthQueryParamCommonExt;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author zhaoyong
 */
@Data
public class ScenePageQueryParam extends AuthQueryParamCommonExt {

    private String sceneName;

    /**
     * 创建时间
     */
    private String startTime;

    /**
     * 更新时间
     */
    private String endTime;

}
