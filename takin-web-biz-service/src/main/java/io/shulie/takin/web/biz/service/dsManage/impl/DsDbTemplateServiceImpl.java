package io.shulie.takin.web.biz.service.dsManage.impl;

import com.google.common.collect.Lists;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.service.dsManage.AbstractDsTemplateService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.application.CacheConfigTemplateDAO;
import io.shulie.takin.web.data.dao.application.ConnectpoolConfigTemplateDAO;
import io.shulie.takin.web.data.result.application.CacheConfigTemplateDetailResult;
import io.shulie.takin.web.data.result.application.ConnectpoolConfigTemplateDetailResult;
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
public class DsDbTemplateServiceImpl extends AbstractDsTemplateService {

    @Autowired
    private ConnectpoolConfigTemplateDAO connectpoolConfigTemplateDAO;

    /**
     * 获取中间件支持的隔离方案
     *
     * @param middlewareType 中间件类型
     * @param engName
     * @return 支持的隔离方案
     */
    @Override
    public List<SelectVO> queryDsType(String middlewareType, String engName) {
        ConnectpoolConfigTemplateDetailResult result = connectpoolConfigTemplateDAO.queryOne(middlewareType, engName);
        if (Objects.isNull(result)) {
            throw new TakinWebException(TakinWebExceptionEnum.SHADOW_CONFIG_CREATE_ERROR, "此连接池模式不支持");
        }
        List<SelectVO> vos = Lists.newArrayList();
        if (result.getShadowtableEnable() == 1) {
            vos.add(new SelectVO(DsTypeEnum.SHADOW_TABLE.getDesc(), String.valueOf(DsTypeEnum.SHADOW_TABLE.getCode())));
        }
        if (result.getShadowdbEnable() == 1) {
            vos.add(new SelectVO(DsTypeEnum.SHADOW_DB.getDesc(), String.valueOf(DsTypeEnum.SHADOW_DB.getCode())));
        }
        if (result.getShadowdbwithshadowtableEnable() == 1) {
            vos.add(new SelectVO("影子库影子表", String.valueOf(DsTypeEnum.SHADOW_REDIS_SERVER.getCode())));
        }
        return vos;
    }

    /**
     * 获取支持的版本
     *
     * @return 支持的版本集合
     */
    @Override
    public List<SelectVO> queryDsSupperName() {
        List<SelectVO> vos = Lists.newArrayList();
        List<ConnectpoolConfigTemplateDetailResult> results = connectpoolConfigTemplateDAO.queryList();
        if (CollectionUtils.isEmpty(results)) {
            return vos;
        }
        results.forEach(detail -> {
            if (!"兼容老版本(影子库)".equals(detail.getName()) || !"兼容老版本(影子表)".equals(detail.getName())) {
                vos.add(new SelectVO(detail.getName(), detail.getName()));
            }
        });
        return vos;
    }
}
