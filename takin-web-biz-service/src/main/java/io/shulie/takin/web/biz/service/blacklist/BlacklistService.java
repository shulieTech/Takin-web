package io.shulie.takin.web.biz.service.blacklist;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.input.blacklist.BlacklistCreateInput;
import io.shulie.takin.web.biz.pojo.input.blacklist.BlacklistSearchInput;
import io.shulie.takin.web.biz.pojo.input.blacklist.BlacklistUpdateInput;
import io.shulie.takin.web.biz.pojo.output.blacklist.BlacklistOutput;
import io.shulie.takin.web.common.vo.blacklist.BlacklistVO;

/**
 * @author 无涯
 * @date 2021/4/6 2:15 下午
 */
public interface BlacklistService {
    /**
     * 新增
     */
    void insert(BlacklistCreateInput input);

    /**
     * 更新
     */
    void update(BlacklistUpdateInput input);

    /**
     * 启动禁用
     */
    void enable(BlacklistUpdateInput input);

    /**
     * 批量启动
     */
    void batchEnable(List<Long> ids, Integer useYn);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 删除
     */
    void batchDelete(List<Long> id);

    /**
     * 分页查询
     *
     * @return
     */
    PagingList<BlacklistVO> pageList(BlacklistSearchInput input);

    /**
     * 获取所有
     *
     * @return
     */
    List<BlacklistOutput> selectList(BlacklistSearchInput input);

    /**
     * 根据id查询
     *
     * @return
     */
    BlacklistOutput selectById(Long id);

}
