/* https://github.com/orange1438 */
package com.pamirs.takin.entity.dao.simplify;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.simplify.TShadowJobConfig;
import com.pamirs.takin.entity.domain.query.ShadowJobConfigQuery;
import org.apache.ibatis.annotations.Param;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: com.pamirs.takin.entity.dao.simplify
 * @date 2020-03-17 15:40
 */
public interface TShadowJobConfigMapper {
    int delete(Long id);

    int insert(TShadowJobConfig record);

    TShadowJobConfig selectOneById(ShadowJobConfigQuery query);


    List<TShadowJobConfig> selectList(@Param("query")ShadowJobConfigQuery query,@Param("userIds") List<Long> userIds);

    int update(TShadowJobConfig record);

    int updateByPrimaryKeyWithBLOBs(TShadowJobConfig record);

    int updateByPrimaryKey(TShadowJobConfig record);

    List<TShadowJobConfig> getAllEnableShadowJobs(@Param("applicationId") long applicationId);
}
