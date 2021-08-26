package io.shulie.takin.web.data.dao.leakcheck;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigCreateParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigDeleteParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigQueryParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigSingleQueryParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigUpdateParam;
import io.shulie.takin.web.data.result.leakcheck.LeakCheckConfigResult;

/**
 * @author fanxx
 * @date 2020/12/31 2:29 下午
 */
public interface LeakCheckConfigDAO {
    /**
     * 分页查询漏数配置
     *
     * @param queryParam
     * @return
     */
    PagingList<LeakCheckConfigResult> selectPage(LeakCheckConfigQueryParam queryParam);

    /**
     * 新增漏数配置
     *
     * @param createParam
     * @return
     */
    int insert(LeakCheckConfigCreateParam createParam);

    /**
     * 更新漏数配置
     *
     * @param updateParam
     * @return
     */
    int update(LeakCheckConfigUpdateParam updateParam);

    /**
     * 删除漏数配置
     *
     * @param deleteParam
     * @return
     */
    int delete(LeakCheckConfigDeleteParam deleteParam);

    /**
     * 查询单个配置
     *
     * @param singleQueryParam
     * @return
     */
    LeakCheckConfigResult selectSingle(LeakCheckConfigSingleQueryParam singleQueryParam);

    /**
     * 批量查询配置
     *
     * @param queryParam
     * @return
     */
    List<LeakCheckConfigResult> selectList(LeakCheckConfigQueryParam queryParam);
}
