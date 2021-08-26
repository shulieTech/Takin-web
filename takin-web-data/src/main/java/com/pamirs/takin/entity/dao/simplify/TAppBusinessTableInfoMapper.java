/* https://github.com/orange1438 */
package com.pamirs.takin.entity.dao.simplify;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.simplify.AppBusinessTableInfo;
import com.pamirs.takin.entity.domain.query.agent.AppBusinessTableQuery;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: com.pamirs.takin.entity.dao.simplify
 * @date 2020-03-25 15:09
 */
public interface TAppBusinessTableInfoMapper {

    int insert(AppBusinessTableInfo record);

    int insertBatch(List<AppBusinessTableInfo> records);

    AppBusinessTableInfo selectByUserIdAndUrl(AppBusinessTableInfo record);

    AppBusinessTableInfo selectByUserIdAndUrl(Long id);

    List<AppBusinessTableInfo> selectList(AppBusinessTableQuery query);

    Long selectCountByUserIdAndUrl(AppBusinessTableInfo record);

    int update(AppBusinessTableInfo record);
}
