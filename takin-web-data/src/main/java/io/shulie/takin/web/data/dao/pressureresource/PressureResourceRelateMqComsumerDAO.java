package io.shulie.takin.web.data.dao.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateMqConsumerEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceMqConsumerQueryParam;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:54 PM
 */
public interface PressureResourceRelateMqComsumerDAO {
    /**
     * 列表分页查询
     *
     * @param param
     * @return
     */
    PagingList<PressureResourceRelateMqConsumerEntity> pageList(PressureResourceMqConsumerQueryParam param);

    /**
     * 新增
     *
     * @param mqConsumerEntity
     */
    void add(PressureResourceRelateMqConsumerEntity mqConsumerEntity);

    /**
     * 内部条件查询
     *
     * @param param
     * @return
     */
    List<PressureResourceRelateMqConsumerEntity> queryList(PressureResourceMqConsumerQueryParam param);

    /**
     * 批量操作
     *
     * @param mqConsumerEntityList
     */
    void saveOrUpdate(List<PressureResourceRelateMqConsumerEntity> mqConsumerEntityList);
}
