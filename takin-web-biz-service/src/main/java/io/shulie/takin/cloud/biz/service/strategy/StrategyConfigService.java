package io.shulie.takin.cloud.biz.service.strategy;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.cloud.entity.domain.dto.strategy.StrategyConfigDetailDTO;
import com.pamirs.takin.cloud.entity.domain.vo.strategy.StrategyConfigAddVO;
import com.pamirs.takin.cloud.entity.domain.vo.strategy.StrategyConfigQueryVO;
import com.pamirs.takin.cloud.entity.domain.vo.strategy.StrategyConfigUpdateVO;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyConfigExt;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyOutputExt;
import org.apache.ibatis.annotations.Param;

/**
 * @author qianshui
 * @date 2020/5/9 下午3:16
 */
public interface StrategyConfigService {
    /**
     * 添加
     *
     * @param addVO 入参
     * @return 操作结果
     */
    Boolean add(StrategyConfigAddVO addVO);

    /**
     * 更新
     *
     * @param updateVO 入参
     * @return 操作结果
     */
    Boolean update(StrategyConfigUpdateVO updateVO);

    /**
     * 删除
     *
     * @param id 数据主键
     * @return 操作结果
     */
    Boolean delete(@Param("id") Long id);

    /**
     * 分页查询列表
     *
     * @param queryVO 查询条件
     * @return 数据列表
     */
    PageInfo<StrategyConfigExt> queryPageList(StrategyConfigQueryVO queryVO);

    /**
     * 获取详情
     *
     * @param id 数据主键
     * @return 详情信息
     */
    StrategyConfigDetailDTO getDetail(Long id);

    /**
     * 根据最大并发获得策略结果
     *
     * @param expectThroughput -
     * @param tpsNum           -
     * @return -
     */
    StrategyOutputExt getStrategy(Integer expectThroughput, Integer tpsNum);

    /**
     * 获取默认策略
     *
     * @return -
     */
    StrategyConfigExt getDefaultStrategyConfig();

    /**
     * 获取当前使用的策略
     *
     * @return
     */
    StrategyConfigExt getCurrentStrategyConfig();
}
