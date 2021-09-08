package io.shulie.takin.web.amdb.api;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationErrorQueryDTO;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationInterfaceQueryDTO;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationNodeQueryDTO;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationQueryDTO;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationRemoteCallQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.AppShadowDatabaseDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationErrorDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationInterfaceDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeProbeInfoDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationRemoteCallDTO;

/**
 * @author shiyajian
 * create: 2020-10-13
 */
public interface ApplicationClient {

    /**
     * 查询一个应用对外提供的接口信息
     * 白名单就是基于梳理的接口信息，进行选择并加入
     */
    List<ApplicationInterfaceDTO> listInterfaces(ApplicationInterfaceQueryDTO query);

    /**
     * 分页
     * 查询一个应用对外提供的接口信息
     * 白名单就是基于梳理的接口信息，进行选择并加入
     *
     * @return
     */
    PagingList<ApplicationInterfaceDTO> pageInterfaces(ApplicationInterfaceQueryDTO query);

    PagingList<ApplicationDTO> pageApplications(ApplicationQueryDTO query);

    /**
     * 应用节点列表, 分页
     *
     * @param dto 查询参数
     * @return 应用节点列表
     */
    PagingList<ApplicationNodeDTO> pageApplicationNodes(ApplicationNodeQueryDTO dto);

    List<ApplicationErrorDTO> listErrors(ApplicationErrorQueryDTO query);

    /**
     * 远程调用查询
     *
     * @return
     */
    PagingList<ApplicationRemoteCallDTO> listApplicationRemoteCalls(ApplicationRemoteCallQueryDTO query);

    /**
     * 应用节点列表, 分页
     *
     * @param dto 查询参数
     * @return 应用节点列表
     */
    PagingList<ApplicationNodeDTO> pageApplicationNode(ApplicationNodeQueryDTO dto);

    /**
     * 应用节点列表, 分页
     *
     * @param dto 查询参数
     * @return 应用节点列表
     */
    PagingList<ApplicationNodeDTO> pageApplicationNodeV2(ApplicationNodeQueryDTO dto);

    /**
     * 应用节点探针统计信息
     *
     * @param dto 所需入参
     * @return 应用节点探针统计信息
     */
    ApplicationNodeProbeInfoDTO getApplicationNodeProbeInfo(ApplicationNodeQueryDTO dto);

    /**
     * 影子库表查询
     * @param appName
     * @return
     */
    List<AppShadowDatabaseDTO> getApplicationShadowDataBaseInfo(String appName);

}
