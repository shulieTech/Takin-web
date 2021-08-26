package io.shulie.takin.web.biz.init.fix;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.shulie.takin.web.data.dao.application.WhiteListDAO;
import io.shulie.takin.web.data.dao.application.WhitelistEffectiveAppDao;
import io.shulie.takin.web.data.param.whitelist.WhitelistEffectiveAppSearchParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistSearchParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistUpdatePartAppNameParam;
import io.shulie.takin.web.data.result.whitelist.WhitelistEffectiveAppResult;
import io.shulie.takin.web.data.result.whitelist.WhitelistResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author 无涯
 * @date 2021/4/7 12:00 下午
 */
@Component
@Slf4j
public class WhitelistEffectAppNameDataFixer {

    @Autowired
    private WhitelistEffectiveAppDao whitelistEffectiveAppDao;
    @Autowired
    private WhiteListDAO whiteListDAO;

    public void fix() {
        log.info("开始订正白名单生效应用的白名单类型字段");
        WhitelistEffectiveAppSearchParam searchParam = new WhitelistEffectiveAppSearchParam();
        List<WhitelistEffectiveAppResult> appResults = whitelistEffectiveAppDao.getList(searchParam);

        if (appResults.stream().noneMatch(e -> e.getType() == null)) {
            log.info("没有可订正白名单生效应用的白名单类型字段的数据");
            return;
        }
        List<Long> ids = appResults.stream().map(WhitelistEffectiveAppResult::getId).distinct().collect(Collectors.toList());
        WhitelistSearchParam param = new WhitelistSearchParam();
        param.setIds(ids);
        List<WhitelistResult> whitelistResults = whiteListDAO.getList(param);
        Map<Long, List<WhitelistResult>> whitelistResultsMap = whitelistResults.stream().collect(Collectors.groupingBy(WhitelistResult::getWlistId));
        List<WhitelistUpdatePartAppNameParam> updatePartAppNameParams = appResults.stream().map(result -> {
            WhitelistUpdatePartAppNameParam partAppNameParam = new WhitelistUpdatePartAppNameParam();
            List<WhitelistResult> results = whitelistResultsMap.get(result.getWlistId());
            partAppNameParam.setId(result.getId());
            if (!CollectionUtils.isEmpty(results)) {
                partAppNameParam.setType(results.get(0).getType());
            }
            return partAppNameParam;
        }).collect(Collectors.toList());
        whitelistEffectiveAppDao.updatePartAppName(updatePartAppNameParams);
    }
}
