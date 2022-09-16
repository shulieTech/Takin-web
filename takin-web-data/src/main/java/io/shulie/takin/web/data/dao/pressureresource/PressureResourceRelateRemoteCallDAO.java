package io.shulie.takin.web.data.dao.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateRemoteCallEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceRemoteCallQueryParam;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:54 PM
 */
public interface PressureResourceRelateRemoteCallDAO {
    /**
     * 批量保存
     *
     * @param remoteCallEntityList
     */
    void saveOrUpdate(List<PressureResourceRelateRemoteCallEntity> remoteCallEntityList);

    /**
     * 分页
     *
     * @param param
     * @return
     */
    PagingList<PressureResourceRelateRemoteCallEntity> pageList(PressureResourceRemoteCallQueryParam param);

}
