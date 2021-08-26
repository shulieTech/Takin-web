package io.shulie.takin.web.data.dao.pradar;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;

import io.shulie.takin.web.data.param.pradarconfig.PradarConfigCreateParam;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigQueryParam;
import io.shulie.takin.web.data.result.pradarzkconfig.PradarZKConfigResult;
import org.apache.ibatis.annotations.Param;

/**
 * @author junshao
 * @date 2021/07/08 2:35 下午
 */
public interface PradarZkConfigDAO {

    /**
     * 分页查询zk配置列表
     *
     * @param queryParam
     * @return
     */
    PagingList<PradarZKConfigResult> selectPage(@Param("queryParam") PradarConfigQueryParam queryParam);

    int insert(PradarConfigCreateParam createParam);

    int update(PradarConfigCreateParam updateParam);

    int delete(PradarConfigCreateParam deleteParam);

    List<PradarZKConfigResult> selectList();

    PradarZKConfigResult selectById(Long id);
}
