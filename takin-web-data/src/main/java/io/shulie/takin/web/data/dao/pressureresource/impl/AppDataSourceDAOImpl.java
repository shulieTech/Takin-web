package io.shulie.takin.web.data.dao.pressureresource.impl;

import io.shulie.takin.web.data.dao.pressureresource.AppDataSourceDAO;
import io.shulie.takin.web.data.mapper.mysql.AppDataSourceMapper;
import io.shulie.takin.web.data.model.mysql.PressureResourceAppDataSourceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:23 AM
 */
@Service
public class AppDataSourceDAOImpl implements AppDataSourceDAO {
    private static Logger logger = LoggerFactory.getLogger(AppDataSourceDAOImpl.class);

    @Resource
    private AppDataSourceMapper appDataSourceMapper;

    @Override
    public void saveOrUpdate(List<PressureResourceAppDataSourceEntity> entitys) {
        appDataSourceMapper.saveOrUpdate(entitys);
    }
}
