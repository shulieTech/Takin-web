package io.shulie.takin.web.biz.service.dsManage;

import io.shulie.takin.common.beans.component.SelectVO;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/8/31 11:48 上午
 */
public abstract class AbstractDsTemplateService {

    /**
     * 获取中间件支持的隔离方案
     * @param middlewareType
     * @param engName
     * @return
     */
    public abstract  List<SelectVO> queryDsType(String middlewareType,String engName);

    /**
     * 获取中间件支持的版本
     * @return
     */
    public abstract  List<SelectVO> queryDsSupperName();

}
