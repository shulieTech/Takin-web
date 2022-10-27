package io.shulie.takin.web.data.dao.pressureresource;

import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateDsEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateDsEntityV2;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDsQueryParam;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:54 PM
 */
public interface PressureResourceRelateDsDAO {
    /**
     * 新增
     *
     * @param dsEntitys
     */
    void add(List<PressureResourceRelateDsEntity> dsEntitys);

    /**
     * 新增
     *
     * @param dsEntitys
     */
    void add_v2(List<PressureResourceRelateDsEntityV2> dsEntitys);

    /**
     * 按条件查询列表页
     *
     * @param param
     * @return
     */
    List<PressureResourceRelateDsEntity> queryByParam(PressureResourceDsQueryParam param);

    /**
     * 按条件查询列表页
     *
     * @param param
     * @return
     */
    List<PressureResourceRelateDsEntity> queryByParam_v2(PressureResourceDsQueryParam param);

    /**
     * 保存或更新
     *
     * @param list
     */
    void saveOrUpdate(List<PressureResourceRelateDsEntity> list);
}
