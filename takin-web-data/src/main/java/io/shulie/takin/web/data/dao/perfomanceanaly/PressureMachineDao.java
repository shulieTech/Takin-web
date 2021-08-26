package io.shulie.takin.web.data.dao.perfomanceanaly;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.param.machine.PressureMachineDeleteParam;
import io.shulie.takin.web.data.param.machine.PressureMachineInsertParam;
import io.shulie.takin.web.data.param.machine.PressureMachineQueryParam;
import io.shulie.takin.web.data.param.machine.PressureMachineUpdateParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineResult;

/**
 * @author mubai
 * @date 2020-11-12 20:56
 */
public interface PressureMachineDao {
    /**
     * 新增机器
     * @param param
     */
    Integer insert(PressureMachineInsertParam param) ;

    /**
     * 查询机器
     * @param queryParam
     * @return
     */
    PagingList<PressureMachineResult> queryByExample(PressureMachineQueryParam queryParam);

    Integer getCountByIp (String ip);

    void update (PressureMachineUpdateParam param);

    void delete(PressureMachineDeleteParam param);

    PressureMachineResult getById(Long id);

    PressureMachineResult getByIp(String ip);

}
