package io.shulie.takin.web.data.dao.blacklist;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.vo.blacklist.BlacklistVO;
import io.shulie.takin.web.data.param.blacklist.BlackListCreateParam;
import io.shulie.takin.web.data.param.blacklist.BlacklistCreateNewParam;
import io.shulie.takin.web.data.param.blacklist.BlacklistSearchParam;
import io.shulie.takin.web.data.param.blacklist.BlacklistUpdateParam;
import io.shulie.takin.web.data.result.blacklist.BlacklistResult;

/**
 * @author fanxx
 * @date 2020/11/4 8:17 下午
 */
public interface BlackListDAO {
    @Deprecated
    int insert(BlackListCreateParam param);

    /**
     * 新增
     * @param param
     */
    void newInsert(BlacklistCreateNewParam param);

    /**
     * 批量新增
     * @param params
     */
    void batchInsert(List<BlacklistCreateNewParam> params);

    /**
     * 更新
     * @param param
     */
    void update(BlacklistUpdateParam param);

    /**
     * 删除
     * @param id
     */
    void delete(Long id);

    /**
     * 删除
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 逻辑删
     * @param ids
     */
    @Deprecated
    void logicalDelete(List<Long> ids);

    /**
     * 分页查询
     * @return
     */
    PagingList<BlacklistVO> pageList(BlacklistSearchParam param);

    /**
     * 获取所有
     * @return
     */
    List<BlacklistResult> selectList(BlacklistSearchParam param);

    /**
     * 根据id查询
     * @return
     */
    BlacklistResult selectById(Long id);

    /**
     * 批量id查询
     * @return
     */
    List<BlacklistResult> selectByIds(List<Long> ids);

    /**
     * 说明: 查询所有启用的黑名单列表
     * @param applicationId
     * @return 黑名单列表
     * @author shulie
     */
    List<BlacklistResult> getAllEnabledBlockList(Long applicationId);



}
