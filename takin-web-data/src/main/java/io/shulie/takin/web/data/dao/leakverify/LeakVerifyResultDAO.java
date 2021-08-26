package io.shulie.takin.web.data.dao.leakverify;

import java.util.List;

import io.shulie.takin.web.data.param.leakverify.LeakVerifyResultCreateParam;
import io.shulie.takin.web.data.param.leakverify.LeakVerifyResultQueryParam;
import io.shulie.takin.web.data.result.leakverify.LeakVerifyResultResult;

/**
 * @author fanxx
 * @date 2021/1/5 8:14 下午
 */
public interface LeakVerifyResultDAO {
    Long insert(LeakVerifyResultCreateParam createParam);

    List<LeakVerifyResultResult> selectList(LeakVerifyResultQueryParam queryParam);

    Boolean querySceneIsLeaked(Long reportId);
}
