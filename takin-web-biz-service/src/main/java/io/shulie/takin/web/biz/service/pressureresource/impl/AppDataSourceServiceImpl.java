package io.shulie.takin.web.biz.service.pressureresource.impl;

import com.pamirs.takin.common.util.MD5Util;
import io.shulie.takin.web.biz.service.pressureresource.AppDataSourceService;
import io.shulie.takin.web.data.model.mysql.PressureResourceAppDataSourceEntity;
import io.shulie.takin.web.data.param.application.AppDatabaseInputParam;
import io.shulie.takin.web.data.param.application.ConfigReportInputParam;
import io.shulie.takin.web.data.dao.pressureresource.AppDataSourceDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:17 AM
 */
@Service
public class AppDataSourceServiceImpl implements AppDataSourceService {
    private static Logger logger = LoggerFactory.getLogger(AppDataSourceServiceImpl.class);

    @Resource
    private AppDataSourceDAO appDataSourceDAO;

    /**
     * 保存应用数据源信息
     *
     * @param param
     */
    @Override
    public void save(AppDatabaseInputParam param) {
        PressureResourceAppDataSourceEntity entity = new PressureResourceAppDataSourceEntity();
        BeanUtils.copyProperties(param, entity);

        // 生成唯一值
        String uniqueKey = MD5Util.getMD5(String.format("%s-%s-%s", param.getAppName(), param.getDataSource(), param.getTableUser()));
        entity.setUniqueKey(uniqueKey);
        entity.setGmtCreate(new Date());
        appDataSourceDAO.saveOrUpdate(Arrays.asList(entity));
    }
}
