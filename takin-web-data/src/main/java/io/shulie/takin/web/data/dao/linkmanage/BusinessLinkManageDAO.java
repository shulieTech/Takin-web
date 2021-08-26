package io.shulie.takin.web.data.dao.linkmanage;

import java.util.List;

import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.data.param.linkmanage.BusinessLinkManageCreateParam;
import io.shulie.takin.web.data.param.linkmanage.BusinessLinkManageQueryParam;
import io.shulie.takin.web.data.param.linkmanage.BusinessLinkManageUpdateParam;
import io.shulie.takin.web.data.result.linkmange.BusinessLinkResult;
import org.apache.ibatis.annotations.Param;

/**
 * 业务活动dao
 *
 * @author fanxx
 * @date 2020/10/16 5:21 下午
 */
public interface BusinessLinkManageDAO {

    BusinessLinkResult selectBussinessLinkById(@Param("id") Long id);

    List<BusinessLinkResult> selectBussinessLinkByIdList(@Param("ids") List<Long> ids);

    List<BusinessLinkResult> selectList(BusinessLinkManageQueryParam queryParam);

    int insert(BusinessLinkManageCreateParam param);

    /**
     * 指定责任人-业务活动
     *
     * @param param
     * @return
     */
    int allocationUser(BusinessLinkManageUpdateParam param);

    /**
     * 获取列表
     *
     * @return
     */
    List<BusinessLinkResult> getList();

    /**
     * 获取列表根据ids
     *
     * @return
     */
    List<BusinessLinkResult> getListByIds(List<Long> ids);

    /**
     * 根据业务流程id, 获得业务活动ids
     *
     * @param businessFlowId 业务流程id
     * @return 业务活动ids
     */
    List<Long> listIdsByBusinessFlowId(Long businessFlowId);

    /**
     * 通过ids获得列表
     *
     * @param businessActivityIds 业务活动ids
     * @return 业务活动列表
     */
    List<BusinessLinkManageTableEntity> listByIds(List<Long> businessActivityIds);

}
