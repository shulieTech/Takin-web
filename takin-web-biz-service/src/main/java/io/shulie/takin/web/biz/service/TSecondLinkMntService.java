package io.shulie.takin.web.biz.service;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.common.util.PageInfo;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import com.pamirs.takin.entity.domain.entity.TSecondLinkMnt;

/**
 * @author shulie
 * @description 二级链路管理业务接口
 * @create 2018/6/18 16:09
 */
public interface TSecondLinkMntService {

    /**
     * 保存二级链路
     *
     * @param secondLinkMnt 二级链路信息
     * @version v1.0
     * @Date:
     */
    void saveSecondLink(TSecondLinkMnt secondLinkMnt) throws Exception;

    /**
     * 查询二级链路列表
     *
     * @param linkName     二级链路名称
     * @param baseLinkName 基础链路名称
     * @param pageNum      当前页
     * @param pageSize     每页记录数
     * @return 二级链路列表
     * @throws TakinModuleException 查询出错抛出异常
     * @version v1.0
     * @Date:
     */
    PageInfo<TSecondLinkMnt> queryLinkList(String linkName, String baseLinkName,
        Integer pageNum, Integer pageSize) throws TakinModuleException;

    /**
     * 根据二级链路id查询链路信息(Map格式)
     *
     * @param linkId 链路id
     * @return 二级链路详情
     * @throws TakinModuleException 查询出错抛出异常
     * @version v1.0
     * @Date:
     */
    Map<String, Object> queryLinkMapByLinkId(String linkId) throws TakinModuleException;

    List<List<Map<String, Object>>> getBasicLinkBySecondLinkId(String secondLinkId);

    /**
     * 根据二级链路id查询链路信息
     *
     * @param linkId 链路id
     * @return 二级链路详情
     * @throws TakinModuleException 查询出错抛出异常
     * @version v1.0
     * @Date:
     */
    TSecondLinkMnt queryLinkByLinkId(String linkId) throws TakinModuleException;

    /**
     * 批量删除二级链路
     *
     * @param linkIds 二级链路id列表,逗号分隔
     * @throws TakinModuleException 删除失败抛出异常
     * @version v1.0
     * @Date:
     */
    void deleteLinkByLinkIds(String linkIds) throws TakinModuleException;

    /**
     * 更新二级链路信息
     *
     * @param secondLinkMnt 二级链路信息
     * @throws TakinModuleException 修改失败抛出异常
     * @version v1.0
     * @Date:
     */
    void updateLinkinfo(TSecondLinkMnt secondLinkMnt) throws TakinModuleException;

    /**
     * 更新二级链路的测试状态
     *
     * @param secondLinkId 二级链路信息
     * @param testStatus   状态值
     * @throws TakinModuleException 修改失败抛出异常
     * @version v1.0
     * @Date:
     */
    void updateSecondLinkStatus(String secondLinkId, String testStatus);

    /**
     * 根据链路信息查询应用列表
     *
     * @param linkId    链路id
     * @param linkLevel 链路级别（一级/二级）
     * @return 应用列表
     * @throws TakinModuleException 查询出错抛出异常
     * @version v1.0
     * @Date:
     */
    Map<String, List<TApplicationMnt>> queryApplicationListByLinkInfo(String linkId, String linkLevel)
        throws TakinModuleException;
}
