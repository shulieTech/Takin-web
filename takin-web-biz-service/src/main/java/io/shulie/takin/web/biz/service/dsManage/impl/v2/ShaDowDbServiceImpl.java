package io.shulie.takin.web.biz.service.dsManage.impl.v2;

import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInputV2;
import io.shulie.takin.web.biz.service.dsManage.AbstractShaDowManageService;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbManageDAO;
import io.shulie.takin.web.data.model.mysql.ApplicationDsDbManageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: 南风
 * @Date: 2021/9/2 5:51 下午
 *
 */
@Service
public class ShaDowDbServiceImpl extends AbstractShaDowManageService {

    @Autowired
    private ApplicationDsDbManageDAO  dbManageDAO;

    /**
     * 更新影子配置方案
     *
     * @param inputV2
     */
    @Override
    public void updateShadowProgramme(ApplicationDsUpdateInputV2 inputV2) {
        ApplicationDsDbManageEntity entity = this.buildEntity(inputV2);
        dbManageDAO.updateById(inputV2.getId(),entity);
    }



    private ApplicationDsDbManageEntity buildEntity(ApplicationDsUpdateInputV2 inputV2){
        ApplicationDsDbManageEntity entity = new ApplicationDsDbManageEntity();
        entity.setDsType(Integer.valueOf(inputV2.getDsType()));
        entity.setShaDowUrl(inputV2.getShaDowUrl());
        entity.setShaDowUserName(inputV2.getShaDowUserName());
        entity.setShaDowPwd(inputV2.getShaDowPassword());
        entity.setShaDowFileExtedn(inputV2.getExtInfo());
        entity.setConfigJson("");//todo nf
        return entity;
    }

}
