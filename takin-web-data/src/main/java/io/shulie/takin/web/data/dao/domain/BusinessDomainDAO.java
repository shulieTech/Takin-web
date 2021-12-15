package io.shulie.takin.web.data.dao.domain;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.param.datasource.DataSourceCreateParam;
import io.shulie.takin.web.data.param.datasource.DataSourceDeleteParam;
import io.shulie.takin.web.data.param.datasource.DataSourceQueryParam;
import io.shulie.takin.web.data.param.datasource.DataSourceSingleQueryParam;
import io.shulie.takin.web.data.param.datasource.DataSourceUpdateParam;
import io.shulie.takin.web.data.param.domain.BusinessDomainCreateParam;
import io.shulie.takin.web.data.param.domain.BusinessDomainDeleteParam;
import io.shulie.takin.web.data.param.domain.BusinessDomainQueryParam;
import io.shulie.takin.web.data.param.domain.BusinessDomainUpdateParam;
import io.shulie.takin.web.data.result.BusinessDomainDetailResult;
import io.shulie.takin.web.data.result.BusinessDomainListResult;
import io.shulie.takin.web.data.result.datasource.DataSourceResult;
import org.apache.ibatis.annotations.Select;

/**
 * 业务域表(BusinessDomain)表数据库 dao 层
 *
 * @author liuchuan
 * @date 2021-12-07 15:59:49
 */
public interface BusinessDomainDAO {
    /**
     * 分页查询业务域列表
     *
     * @param queryParam
     * @return
     */
    PagingList<BusinessDomainListResult> selectPage(BusinessDomainQueryParam queryParam);

    /**
     * 新增业务域
     *
     * @param createParam
     * @return
     */
    int insert(BusinessDomainCreateParam createParam);

    /**
     * 更新业务域
     *
     * @param updateParam
     * @return
     */
    int update(BusinessDomainUpdateParam updateParam);

    /**
     * 删除数据源
     *
     * @param deleteParam
     * @return
     */
    int delete(BusinessDomainDeleteParam deleteParam);

    /**
     * 批量查询数据源信息
     *
     * @param queryParam
     * @return
     */
    List<BusinessDomainListResult> selectList(BusinessDomainQueryParam queryParam);

    /**
     * 查询业务域详情
     * @param id
     * @return
     */
    BusinessDomainDetailResult selectById(Long id);

    /**
     * 获取业务域编码最大值
     * @return
     */
    int selectMaxDomainCode();

}

