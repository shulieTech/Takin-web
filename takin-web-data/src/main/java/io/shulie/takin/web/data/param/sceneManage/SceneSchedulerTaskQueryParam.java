package io.shulie.takin.web.data.param.sceneManage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;

/**
 * @author mubai
 * @date 2020-12-02 19:12
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SceneSchedulerTaskQueryParam extends TenantCommonExt {

    /**
     * 执行时间大于时间
     */
    private String startTime;

    /**
     * 执行时间小于时间
     */
    private String endTime;

}
