package io.shulie.takin.web.data.dao.leakverify;

import java.util.List;

import io.shulie.takin.web.data.param.leakverify.LeakVerifyDetailCreateParam;
import io.shulie.takin.web.data.param.leakverify.LeakVerifyDetailQueryParam;
import io.shulie.takin.web.data.result.leakverify.LeakVerifyDetailResult;

/**
 * @author fanxx
 * @date 2021/1/5 8:15 下午
 */
public interface LeakVerifyDetailDAO {
    int insert(LeakVerifyDetailCreateParam createParam);

    int insertBatch(List<LeakVerifyDetailCreateParam> createParamList);

    List<LeakVerifyDetailResult> selectList(LeakVerifyDetailQueryParam queryParam);

}
