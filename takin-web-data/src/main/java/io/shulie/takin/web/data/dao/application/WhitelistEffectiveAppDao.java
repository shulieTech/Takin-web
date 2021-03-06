package io.shulie.takin.web.data.dao.application;

import java.util.List;

import io.shulie.takin.web.data.param.whitelist.WhitelistAddPartAppNameParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistEffectiveAppDeleteParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistEffectiveAppSearchParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistUpdatePartAppNameParam;
import io.shulie.takin.web.data.result.whitelist.WhitelistEffectiveAppResult;

/**
 * @author 无涯
 * @date 2021/4/14 10:15 上午
 */
public interface WhitelistEffectiveAppDao {

    /**
     * 查询生效应用
     * @param param
     * @return
     */
    List<WhitelistEffectiveAppResult> getList(WhitelistEffectiveAppSearchParam param);

    /**
     * 根据ids查询
     * @param ids
     * @return
     */
    List<WhitelistEffectiveAppResult> getListByWhiteIds(List<Long> ids);

    /**
     * 更新生效应用
     * @param params
     */
    void addPartAppName(List<WhitelistAddPartAppNameParam> params);

    /**
     * 更新生效应用
     * @param params
     */
    void updatePartAppName(List<WhitelistUpdatePartAppNameParam> params);

    /**
     *删除
     */
    void delete(WhitelistEffectiveAppDeleteParam param);

    /**
     * 批量
     * @param param
     */
    void batchDelete(WhitelistEffectiveAppDeleteParam param);
}
