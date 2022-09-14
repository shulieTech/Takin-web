package io.shulie.takin.web.data.dao.pressureresource.impl;

import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateRemoteCallDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateRemoteCallMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateRemoteCallEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/14 11:09 AM
 */
@Service
public class PressureResourceRelateRemoteCallDAOImpl implements PressureResourceRelateRemoteCallDAO {
    @Resource
    private PressureResourceRelateRemoteCallMapper pressureResourceRelateRemoteCallMapper;

    /**
     * 存在则更新
     *
     * @param remoteCallEntityList
     */
    @Override
    public void saveOrUpdate(List<PressureResourceRelateRemoteCallEntity> remoteCallEntityList) {
        if (CollectionUtils.isEmpty(remoteCallEntityList)) {
            return;
        }
        pressureResourceRelateRemoteCallMapper.saveOrUpdate(remoteCallEntityList);
    }
}
