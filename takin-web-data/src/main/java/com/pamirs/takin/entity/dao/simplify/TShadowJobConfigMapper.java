/* https://github.com/orange1438 */
package com.pamirs.takin.entity.dao.simplify;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.simplify.TShadowJobConfig;
import com.pamirs.takin.entity.domain.query.ShadowJobConfigQuery;
import io.shulie.takin.web.common.annocation.DataAuth;
import org.apache.ibatis.annotations.Param;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: com.pamirs.takin.entity.dao.simplify
 * @date 2020-03-17 15:40
 */
public interface TShadowJobConfigMapper {
    int delete(Long id);

    int insert(TShadowJobConfig record);

    TShadowJobConfig selectOneById(@Param("id") Long id);

    @DataAuth
    List<TShadowJobConfig> selectList(ShadowJobConfigQuery query);

    int update(TShadowJobConfig record);

    int updateByPrimaryKeyWithBLOBs(TShadowJobConfig record);

    int updateByPrimaryKey(TShadowJobConfig record);

    List<TShadowJobConfig> getAllEnableShadowJobs(@Param("applicationId") long applicationId);
}
