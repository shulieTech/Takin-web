package io.shulie.takin.web.biz.service.dsManage.impl.v2;

import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInputV2;
import io.shulie.takin.web.biz.service.dsManage.AbstractShaDowManageService;
import io.shulie.takin.web.data.dao.application.ApplicationDsCacheManageDAO;
import io.shulie.takin.web.data.model.mysql.ApplicationDsCacheManageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: 南风
 * @Date: 2021/9/2 5:51 下午
 */
@Service
public class ShaDowCacheServiceImpl extends AbstractShaDowManageService {

    @Autowired
    private ApplicationDsCacheManageDAO cacheManageDAO;

    /**
     * 更新影子配置方案
     *
     * @param inputV2
     */
    @Override
    public void updateShadowProgramme(ApplicationDsUpdateInputV2 inputV2) {
        ApplicationDsCacheManageEntity entity = this.buildEntity(inputV2);
        cacheManageDAO.updateById(inputV2.getId(),entity);
    }

    private ApplicationDsCacheManageEntity buildEntity(ApplicationDsUpdateInputV2 inputV2){
        ApplicationDsCacheManageEntity entity = new  ApplicationDsCacheManageEntity();
        entity.setDsType(entity.getDsType());
        entity.setShaDowFileExtedn(inputV2.getExtInfo());
        return entity;
    }
}
