package io.shulie.takin.web.data.dao.leakcheck;

import java.util.List;

import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigDetailCreateParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigDetailDeleteParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigDetailQueryParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigDetailUpdateParam;
import io.shulie.takin.web.data.result.leakcheck.LeakCheckConfigBatchDetailResult;
import io.shulie.takin.web.data.result.leakcheck.LeakCheckConfigSingleDetailResult;

/**
 * @author fanxx
 * @date 2020/12/31 2:38 下午
 */
public interface LeakCheckConfigDetailDAO {
    /**
     * 查询漏数配置详情列表(单个数据源)
     *
     * @param queryParam
     * @return
     */
    LeakCheckConfigSingleDetailResult selectSingle(LeakCheckConfigDetailQueryParam queryParam);

    /**
     * 查询漏数配置详情列表(多个数据源)
     *
     * @param queryParam
     * @return
     */
    List<LeakCheckConfigBatchDetailResult> selectList(LeakCheckConfigDetailQueryParam queryParam);

    /**
     * 新增漏数配置详情
     *
     * @param createParam
     * @return
     */
    List<Long> insert(LeakCheckConfigDetailCreateParam createParam);

    /**
     * 更新漏数配置详情
     *
     * @param updateParam
     * @return
     */
    int update(LeakCheckConfigDetailUpdateParam updateParam);

    /**
     * 删除漏数配置详情
     *
     * @param deleteParam
     * @return
     */
    int delete(LeakCheckConfigDetailDeleteParam deleteParam);
}
