package io.shulie.takin.web.data.dao.linkmanage;

import java.util.List;

import io.shulie.takin.web.data.param.linkmanage.LinkManageCreateParam;
import io.shulie.takin.web.data.param.linkmanage.LinkManageQueryParam;
import io.shulie.takin.web.data.param.linkmanage.LinkManageUpdateParam;
import io.shulie.takin.web.data.result.linkmange.LinkManageResult;

/**
 * @author fanxx
 * @date 2020/10/19 7:43 下午
 */
public interface LinkManageDAO {

    /**
     * 根据系统流程id查看系统流程详情
     *
     * @param id link manage table 主键id
     * @return 详情
     */
    LinkManageResult selectLinkManageById(Long id);

    /**
     * 根据系条件批量查看系统流程
     *
     * @param queryParam
     * @return
     */
    List<LinkManageResult> selectList(LinkManageQueryParam queryParam);

    /**
     * 新增系统流程
     *
     * @param param
     * @return
     */
    int insert(LinkManageCreateParam param);

    /**
     * 指定责任人-系统流程
     *
     * @param param
     * @return
     */
    int allocationUser(LinkManageUpdateParam param);

}
