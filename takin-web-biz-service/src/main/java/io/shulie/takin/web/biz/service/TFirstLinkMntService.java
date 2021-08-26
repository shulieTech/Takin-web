package io.shulie.takin.web.biz.service;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.common.util.PageInfo;
import com.pamirs.takin.entity.domain.entity.TFirstLinkMnt;

/**
 * @author shulie
 * @description 一级链路管理业务接口
 * @create 2018/7/11 13:54
 */
public interface TFirstLinkMntService {

    /**
     * 保存一级链路信息
     *
     * @param firstLinkMnt 一级链路信息
     * @throws TakinModuleException 保存失败抛出异常
     * @version v1.0
     * @Date:
     */
    void saveLink(TFirstLinkMnt firstLinkMnt) throws TakinModuleException;

    /**
     * 查询一级链路列表
     *
     * @param firstLinkName  一级链路名称
     * @param secondLinkName 二级链路名称
     * @param pageNum        当前页
     * @param pageSize       每页记录数
     * @return 一级链路列表
     * @throws TakinModuleException 查询失败抛出异常
     * @version v1.0
     * @Date:
     */
    PageInfo<TFirstLinkMnt> queryLinkList(String firstLinkName, String secondLinkName,
        Integer pageNum, Integer pageSize) throws TakinModuleException;

    /**
     * 根据链路id查询一级链路信息
     *
     * @param firstLinkId 链路id
     * @return 一级链路信息
     * @throws TakinModuleException 查询失败抛出异常
     * @version v1.0
     * @Date:
     */
    TFirstLinkMnt queryLinkByLinkId(String firstLinkId) throws TakinModuleException;

    /**
     * 查询一级链路详情（Map格式）
     *
     * @param firstLinkId 链路id
     * @return 一级链路详情
     * @throws TakinModuleException 查询失败抛出异常
     * @version v1.0
     * @Date:
     */
    Map<String, Object> queryLinkMapByLinkId(String firstLinkId) throws TakinModuleException;

    /**
     * 批量删除一级链路
     *
     * @param firstLinkIds 一级链路id列表
     * @throws TakinModuleException 删除失败抛出异常
     * @version v1.0
     * @Date:
     */
    void deleteLinkByLinkIds(String firstLinkIds) throws TakinModuleException;

    /**
     * 修改一级链路信息
     *
     * @param firstLinkMnt 一级链路信息
     * @throws TakinModuleException 修改失败抛出异常
     * @version v1.0
     * @Date:
     */
    void updateLinkinfo(TFirstLinkMnt firstLinkMnt) throws TakinModuleException;

    /**
     * 根据一级链路id获取链路拓扑图
     *
     * @param firstLinkId 一级链路id
     * @return 一级/二级/基础链路对应关系
     * @throws TakinModuleException 查询失败抛出异常
     * @version v1.0
     * @Date:
     */
    Map<String, Object> getLinkTopologyByFirstLinkId(String firstLinkId) throws TakinModuleException;

    /**
     * 根据二级链路id查询一级链路
     *
     * @param secondLinkId 二级链路id
     * @return 包含二级链路的一级链路列表
     * @version v1.0
     * @Date:
     */
    List<TFirstLinkMnt> queryLinkBySecondLinkId(String secondLinkId);
}
