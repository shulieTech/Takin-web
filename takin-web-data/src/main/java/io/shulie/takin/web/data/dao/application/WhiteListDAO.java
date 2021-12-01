package io.shulie.takin.web.data.dao.application;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pamirs.takin.entity.domain.entity.TPradaHttpData;
import com.pamirs.takin.entity.domain.entity.TWList;
import com.pamirs.takin.entity.domain.query.TWListVo;
import com.pamirs.takin.entity.domain.vo.TApplicationInterface;
import com.pamirs.takin.entity.domain.vo.TUploadInterfaceDataVo;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.vo.whitelist.WhiteListVO;
import io.shulie.takin.web.data.model.mysql.WhiteListEntity;
import io.shulie.takin.web.data.param.whitelist.WhitelistGlobalOrPartParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistSaveOrUpdateParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistSearchParam;
import io.shulie.takin.web.data.result.whitelist.WhitelistResult;

/**
 * 白名单配置 dao 层
 *
 * @author liuchuan
 * @date 2021/4/8 10:50 上午
 */
public interface WhiteListDAO extends IService<WhiteListEntity> {

    /**
     * 批量插入或者更新
     * @param params
     */
    void batchSaveOrUpdate(List<WhitelistSaveOrUpdateParam> params);

    /**
     * 通过应用id, 获得白名单配置
     *
     * @param applicationId 应用id
     * @return 名单列表
     */
    List<WhitelistResult> listByApplicationId(Long applicationId);

    /**
     * 查询所有
     * @param param
     * @return
     */
    List<WhitelistResult> getList(WhitelistSearchParam param);

    /**
     * 根据id查询
     * @param wlistId
     * @return
     */
    WhitelistResult selectById(Long wlistId);

    /**
     * 白名单列表
     * @param param
     * @return
     */
    PagingList<WhiteListVO> pagingList(WhitelistSearchParam param);

    /**
     * 更新全局状态还是局部状态
     */
    void updateWhitelistGlobal(WhitelistGlobalOrPartParam param);



    // ---- 迁移 -----
    /**
     * 说明: 根据应用id删除应用信息接口(删除应用下的服务)
     *
     * @param applicationIdLists 应用id集合
     * @author shulie
     */
    void deleteApplicationInfoRelatedInterfaceByIds(List<Long> applicationIdLists);

    /**
     * 说明: 判断该白名单接口是否已经存在
     *
     * @param interfaceName 接口名称
     * @return 大于0表示已经存在, 反之不存在
     * @author shulie
     */
    int whiteListExist(String appId, String interfaceName, String useYn
    );

    /**
     * 说明: 批量加入白名单状态
     *
     * @param whiteListIdList 白名单ID列表
     * @return -
     */
    int batchEnableWhiteList(List<Long> whiteListIdList);

    /**
     * 说明: 批量移出白名单状态
     *
     * @param whiteListIdList 白名单ID列表
     * @return -
     */
    int batchDisableWhiteList( List<Long> whiteListIdList);

    /**
     * 说明:  添加白名单接口信息
     *
     * @param tWhiteList 白名单实体类
     * @author shulie
     */
    public void addWhiteList(TWList tWhiteList);



    /**
     * 说明: 根据id查询单个白名单信息
     *
     * @param wlistId 白名单id
     * @return 单条白名单信息
     * @author shulie
     */
    TWList querySingleWhiteListById(String wlistId);


    void updateSelective(TWList tWhiteList);

    /**
     * 说明: 删除白名单接口
     *
     * @param whitelistIds 白名单ids
     * @author shulie
     */
    public void deleteWhiteListByIds(List<String> whitelistIds);

    /**
     * 说明: 删除白名单接口
     *
     * @param whiteListIds 白名单ids
     * @author shulie
     */
    void deleteByIds( List<Long> whiteListIds);

    /**
     * 说明: 根据id列表批量查询白名单信息
     *
     * @param whiteListIds 白名单ids
     * @return 白名单集合
     * @author shulie
     * @date 2018/11/5 15:13
     */
    List<TWList> queryWhiteListByIds( List<String> whiteListIds);

    /**
     * 说明: 根据id列表批量查询白名单信息
     *
     * @date 2018/11/5 15:13
     */
    List<TWList> getWhiteListByIds(List<Long> whiteListIds);

    /**
     * 说明: 查询白名单列表
     *
     * @param applicationName 应用名称，可选
     * @return 白名单列表
     * @author shulie
     */
    List<Map<String, Object>> queryWhiteListList( String applicationName);

    /**
     * 说明: 当链路应用服务查询不到时,查询白名单列表接口 默认时间倒序
     *
     * @param applicationName 应用名称
     * @param principalNo     负责人工号
     * @param type            白名单类型
     * @return 白名单列表
     * @author shulie
     */
    List<TApplicationInterface> queryOnlyWhiteList(String applicationName, String principalNo,String type, String whiteListUrl, List<String> wlistIds, Long applicationId);

    List<TWList> queryWhiteListTotalByApplicationId( Long applicationId);

    //@DataAuth

    /**
     * 去除用户隔离级别权限
     *
     * @return -
     */

    List<TWList> queryDistinctWhiteListTotalByApplicationId(Long applicationId);

    /**
     * 说明: 根据白名单id查询是否在基础链路中使用
     *
     * @param wListId 白名单id
     * @return 存在的基础链路个数和基础链路名称
     * @author shulie
     * @date 2018/7/10 11:07
     */
    Map<String, Object> queryWhiteListRelationBasicLinkByWhiteListId(String wListId);




    /**
     * 说明: 根据应用和接口名称查询应用下http接口数据
     *
     * @param applicationName 表名
     * @param interfaceName   接口名称
     * @author shulie
     * @date 2019/3/4 20:37
     */
    List<TPradaHttpData> queryInterfaceByAppNameByTPHD(String applicationName, String type, String interfaceName);

    /**
     * 说明: 根据应用和接口名称查询应用下dubbo/job接口数据
     *
     * @param applicationName 表名
     * @param interfaceName   接口名称
     * @author shulie
     * @date 2019/3/4 20:37
     */
    List<TUploadInterfaceDataVo> queryInterfaceByAppNameFromTUID(String applicationName, String type, String interfaceName);

    /**
     * 说明: 批量增加白名单
     *
     * @author shulie
     * @date 2019/4/3 20:01
     */
    void batchAddWhiteList( List<TWList> twLists);

    /**
     * 说明: 根据MQ信息查询白名单数量
     *
     * @return int
     * @author shulie
     * @date 2019/4/3 20:02
     */
    int queryWhiteListCountByMqInfo(TWListVo twListVo);

    /**
     * 给链路提供白名单列表，增加到链路服务中
     *
     * @return -
     */
    List<Map<String, String>> getWhiteListForLink();

    /**
     * 查询白名单列表
     *
     * @param applicationId 应用ID
     * @return -
     */
    List<Map<String, Object>> queryWhiteListByAppId(String applicationId);

    /**
     * 查询白名单
     *
     * @return -
     */
    TWList getWhiteListByParam(Map<String, String> queryMap);

    List<Map<String, Object>> getWhiteListByAppIds(List<String> ids);

    List<TWList> getWhiteListByApplicationId(Long applicationId);

    List<TWList> getAllEnableWhitelists(String applicationId);


}
