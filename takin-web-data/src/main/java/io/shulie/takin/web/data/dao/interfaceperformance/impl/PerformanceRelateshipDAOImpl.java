package io.shulie.takin.web.data.dao.interfaceperformance.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.shulie.takin.web.data.dao.interfaceperformance.PerformanceRelateshipDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigSceneRelateShipMapper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigSceneRelateShipEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/6/7 6:54 下午
 */
@Service
public class PerformanceRelateshipDAOImpl implements PerformanceRelateshipDAO {
    @Resource
    private InterfacePerformanceConfigSceneRelateShipMapper interfacePerformanceConfigSceneRelateShipMapper;

    /**
     * 根据Id获取关连关系
     *
     * @param apiId
     * @return
     */
    @Override
    public InterfacePerformanceConfigSceneRelateShipEntity relationShipEntityById(Long apiId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("api_id", apiId);
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.last("limit 1");
        InterfacePerformanceConfigSceneRelateShipEntity entity = interfacePerformanceConfigSceneRelateShipMapper.selectOne(queryWrapper);
        return entity;
    }
}
