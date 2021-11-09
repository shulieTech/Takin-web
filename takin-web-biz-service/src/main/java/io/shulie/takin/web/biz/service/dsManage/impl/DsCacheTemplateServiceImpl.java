package io.shulie.takin.web.biz.service.dsManage.impl;

import com.google.common.collect.Lists;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.service.dsManage.AbstractDsTemplateService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.application.CacheConfigTemplateDAO;
import io.shulie.takin.web.data.result.application.CacheConfigTemplateDetailResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author 南风
 * @date 2021/8/31 11:53 上午
 */
@Service
public class DsCacheTemplateServiceImpl extends AbstractDsTemplateService {

    @Autowired
    private CacheConfigTemplateDAO cacheConfigTemplateDAO;

    /**
     * 获取支持的隔离方案
     *
     * @param middlewareType 中间件类型
     * @param engName
     * @return 支持的隔离方案列表
     */
    @Override
    public List<SelectVO> queryDsType(String middlewareType, String engName) {
        CacheConfigTemplateDetailResult result = cacheConfigTemplateDAO.queryOne(middlewareType, engName);
        if (Objects.isNull(result)) {
            throw new TakinWebException(TakinWebExceptionEnum.SHADOW_CONFIG_CREATE_ERROR, "此缓存模式不支持");
        }
        List<SelectVO> vos = Lists.newArrayList();
        if (result.getShadowtdbEnable() == 1) {
            vos.add(new SelectVO(DsTypeEnum.SHADOW_REDIS_KEY.getDesc(), String.valueOf(DsTypeEnum.SHADOW_REDIS_KEY.getCode())));
        }
        if (result.getShadowttableEnable() == 1) {
            vos.add(new SelectVO(DsTypeEnum.SHADOW_REDIS_CLUSTER.getDesc(), String.valueOf(DsTypeEnum.SHADOW_REDIS_CLUSTER.getCode())));
        }
        return vos;
    }

    /**
     * 获取支持的版本
     *
     * @return 支持的版本列表
     */
    @Override
    public List<SelectVO> queryDsSupperName() {
        List<SelectVO> vos = Lists.newArrayList();
        List<CacheConfigTemplateDetailResult> results = cacheConfigTemplateDAO.queryList();
        if (CollectionUtils.isEmpty(results)) {
            return vos;
        }
        results.forEach(detail -> vos.add(new SelectVO(detail.getName(), detail.getName())));
        return vos;
    }
}
