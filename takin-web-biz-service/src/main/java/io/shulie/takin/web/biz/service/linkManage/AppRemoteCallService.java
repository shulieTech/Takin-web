package io.shulie.takin.web.biz.service.linkManage;

import java.util.List;
import java.util.Map;

import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationRemoteCallDTO;
import io.shulie.takin.web.biz.pojo.input.application.AppRemoteCallQueryInput;
import io.shulie.takin.web.biz.pojo.input.application.AppRemoteCallUpdateInput;
import io.shulie.takin.web.biz.pojo.output.application.AppRemoteCallOutput;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
import io.shulie.takin.web.common.vo.application.AppRemoteCallListVO;

/**
 * @author 无涯
 * @date 2021/5/29 12:31 上午
 */
public interface AppRemoteCallService {

    /**
     * 更新
     */
    void update(AppRemoteCallUpdateInput input);

    /**
     * 根据id获取
     *
     * @return
     */
    AppRemoteCallOutput getById(Long id);

    /**
     * 根据id删除
     */
    void deleteById(Long id);

    /**
     * 分页查询
     *
     * @return
     */
    PagingList<AppRemoteCallListVO> pagingList(AppRemoteCallQueryInput input);

    /**
     * 获取异常远程配置
     *
     * @return
     */
    String getException(Long applicationId);

    /**
     * 根据配置类型筛选数据
     *
     * @return
     */
    List<SelectVO> getConfigSelect(Integer interfaceType, String serverAppName);

    /**
     * agent获取数据
     *
     * @return
     */
    AgentRemoteCallVO agentSelect(String appName);

    /**
     * 同步amdb数据
     */
    void syncAmdb();

    /**
     * 获取服务端应用
     *
     * @return
     */
    Map<String, List<ApplicationRemoteCallDTO>> getServerAppListMap(String upNames);

    /**
     * 根据应用删除
     */
    void deleteByApplicationIds(List<Long> applicationIds);
}
