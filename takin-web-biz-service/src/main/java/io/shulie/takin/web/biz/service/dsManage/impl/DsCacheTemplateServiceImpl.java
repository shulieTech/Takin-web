package io.shulie.takin.web.biz.service.dsManage.impl;

import com.google.common.collect.Lists;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.service.dsManage.AbstractDsTemplateService;
import io.shulie.takin.web.data.dao.application.CacheConfigTemplateDAO;
import io.shulie.takin.web.data.result.application.CacheConfigTemplateDetailResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/8/31 11:53 上午
 */
@Service
public class DsCacheTemplateServiceImpl extends AbstractDsTemplateService {


    @Autowired
    private CacheConfigTemplateDAO cacheConfigTemplateDAO;

    /**
     * 获取中间件支持的隔离方案
     *
     * @param middlewareType
     * @param engName
     * @return
     */
    @Override
    public List<SelectVO> queryDsType(String middlewareType, String engName) {
        CacheConfigTemplateDetailResult result = cacheConfigTemplateDAO.queryOne(middlewareType, engName);
        List<SelectVO> vos  = Lists.newArrayList();
        if(result.getShadowtdbEnable() == 1){
            vos.add(new SelectVO(String.valueOf(DsTypeEnum.SHADOW_REDIS_KEY.getCode()),DsTypeEnum.SHADOW_REDIS_KEY.getDesc()));
        }
        if(result.getShadowttableEnable() == 1){
            vos.add(new SelectVO(String.valueOf(DsTypeEnum.SHADOW_REDIS_CLUSTER.getCode()),DsTypeEnum.SHADOW_REDIS_CLUSTER.getDesc()));
        }
        return vos;
    }
}
