package io.shulie.takin.web.biz.service.linkManage;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.entity.domain.entity.TWList;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.vo.whitelist.WhiteListVO;
import io.shulie.takin.web.common.vo.whitelist.WhitelistPartVO;
import com.pamirs.takin.entity.domain.dto.linkmanage.InterfaceVo;
import com.pamirs.takin.entity.domain.query.whitelist.WhiteListQueryVO;
import com.pamirs.takin.entity.domain.query.whitelist.WhiteListOperateVO;
import io.shulie.takin.web.biz.pojo.input.whitelist.WhitelistSearchInput;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import com.pamirs.takin.entity.domain.query.whitelist.WhiteListCreateListVO;
import io.shulie.takin.web.biz.pojo.request.whitelist.WhiteListDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.whitelist.WhiteListUpdateRequest;
import io.shulie.takin.web.biz.pojo.input.whitelist.WhitelistImportFromExcelInput;
import io.shulie.takin.web.biz.pojo.input.whitelist.WhitelistUpdatePartAppNameInput;

/**
 * @author mubai
 * @date 2020-04-20 19:12
 */
public interface WhiteListService {

    /**
     * excel导入
     */
    void importWhiteListFromExcel(List<WhitelistImportFromExcelInput> inputs);

    void saveWhitelist(WhiteListCreateListVO vo);

    void operateWhitelist(WhiteListOperateVO vo);

    /**
     * 全局白名单
     *
     * @return
     */
    List<String> getExistWhite(List<String> interfaceNames, List<ApplicationDetailResult> appDetailResults);

    PageInfo<WhiteListVO> queryWhitelist(WhiteListQueryVO vo);

    List<TWList> getAllEnableWhitelists(String applicationId);

    void updateWhitelist(WhiteListUpdateRequest request);

    void deleteWhitelist(WhiteListDeleteRequest request);

    /**
     * 应用名
     *
     * @return
     */
    WhitelistPartVO getPart(Long wlistId);

    /**
     * 局部，生效应用
     */
    void part(WhitelistUpdatePartAppNameInput input);

    /**
     * 全局
     */
    void global(Long wlistId);

    /*******************************白名单列表*********************************/
    /**
     * @return
     */
    PagingList<WhiteListVO> pagingList(WhitelistSearchInput input);
    /*******************************白名单列表*********************************/

    /**
     * 所有的 agent 上报的白名单
     *
     * @param appName 应用名称
     * @return 白名单
     */
    List<InterfaceVo> getAllInterface(String appName);
}
