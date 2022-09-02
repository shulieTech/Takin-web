package io.shulie.takin.web.data.dao.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationAppEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceAppQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceQueryParam;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:54 PM
 */
public interface PressureResourceRelationAppDAO {
    /**
     * 分页
     *
     * @param param
     * @return
     */
    PagingList<PressureResourceRelationAppEntity> pageList(PressureResourceAppQueryParam param);

    /**
     * 内部查询
     *
     * @param param
     * @return
     */
    List<PressureResourceRelationAppEntity> queryList(PressureResourceAppQueryParam param);
}
