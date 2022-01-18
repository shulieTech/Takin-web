package io.shulie.takin.web.data.dao.application;

import io.shulie.takin.web.data.param.application.ShadowJobCreateParam;
import io.shulie.takin.web.data.param.application.ShadowJobUpdateUserParam;

/**
 * @author fanxx
 * @date 2020/11/9 9:01 下午
 */
public interface ApplicationShadowJobDAO {
    int insert(ShadowJobCreateParam param);

    int allocationUser(ShadowJobUpdateUserParam param);

    /**
     * 是否重名
     * @param shadowJobCreateParam
     * @return
     */
    Boolean exist( ShadowJobCreateParam shadowJobCreateParam);

}
