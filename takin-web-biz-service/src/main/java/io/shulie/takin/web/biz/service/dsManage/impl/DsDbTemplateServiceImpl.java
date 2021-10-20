package io.shulie.takin.web.biz.service.dsManage.impl;

import com.google.common.collect.Lists;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.service.dsManage.AbstractDsTemplateService;
import io.shulie.takin.web.data.dao.application.CacheConfigTemplateDAO;
import io.shulie.takin.web.data.dao.application.ConnectpoolConfigTemplateDAO;
import io.shulie.takin.web.data.result.application.CacheConfigTemplateDetailResult;
import io.shulie.takin.web.data.result.application.ConnectpoolConfigTemplateDetailResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/8/31 11:53 上午
 */
@Service
public class DsDbTemplateServiceImpl extends AbstractDsTemplateService {


    @Autowired
    private ConnectpoolConfigTemplateDAO connectpoolConfigTemplateDAO;

    /**
     * 获取中间件支持的隔离方案
     *
     * @param middlewareType
     * @param engName
     * @return
     */
    @Override
    public List<SelectVO> queryDsType(String middlewareType, String engName) {
        ConnectpoolConfigTemplateDetailResult result = connectpoolConfigTemplateDAO.queryOne(middlewareType, engName);
        List<SelectVO> vos  = Lists.newArrayList();
        if(result.getShadowtableEnable() == 1){
            vos.add(new SelectVO(DsTypeEnum.SHADOW_TABLE.getDesc(),String.valueOf(DsTypeEnum.SHADOW_TABLE.getCode())));
        }
        if(result.getShadowdbEnable() == 1){
            vos.add(new SelectVO(DsTypeEnum.SHADOW_DB.getDesc(),String.valueOf(DsTypeEnum.SHADOW_DB.getCode())));
        }
        if(result.getShadowdbwithshadowtableEnable() == 1){
            vos.add(new SelectVO("影子库影子表",String.valueOf(DsTypeEnum.SHADOW_REDIS_SERVER.getCode())));
        }
        return vos;
    }

    /**
     * 获取支持的版本
     *
     * @return
     */
    @Override
    public List<SelectVO> queryDsSupperName() {
        List<SelectVO> vos = Lists.newArrayList();
        List<ConnectpoolConfigTemplateDetailResult> results = connectpoolConfigTemplateDAO.queryList();
        if(CollectionUtils.isEmpty(results)){
            return vos;
        }
        results.forEach(detail -> {
            vos.add(new SelectVO(detail.getName(),detail.getName()));
        });
        return vos;
    }
}
