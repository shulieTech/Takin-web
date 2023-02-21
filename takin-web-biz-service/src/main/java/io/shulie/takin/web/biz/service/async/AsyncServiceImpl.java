package io.shulie.takin.web.biz.service.async;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import io.shulie.takin.adapter.api.model.common.RuleBean;
import io.shulie.takin.adapter.api.model.common.SlaBean;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.adapter.api.model.request.report.WarnCreateReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp.SceneSlaRefResp;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.service.report.impl.ReportApplicationService;
import io.shulie.takin.web.biz.service.risk.util.DateUtil;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.common.util.RedisHelper;
import io.shulie.takin.web.data.dao.baseserver.BaseServerDao;
import io.shulie.takin.web.data.dao.perfomanceanaly.PerformanceBaseDataDAO;
import io.shulie.takin.web.data.param.baseserver.BaseServerParam;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseDataParam;
import io.shulie.takin.web.data.result.baseserver.BaseServerResult;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author qianshui
 * @date 2020/11/9 下午9:01
 */
@Service
@Slf4j
public class AsyncServiceImpl implements AsyncService {

    @Autowired
    private PerformanceBaseDataDAO performanceBaseDataDAO;

    @Async("agentDataThreadPool")
    @Override
    public void savePerformanceBaseData(PerformanceBaseDataParam param) {
        // 补充header
        param.setSource(ContextSourceEnum.AGENT.getCode());
        WebPluginUtils.setTraceTenantContext(param);
        String redisKey = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3, WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode(),
            String.format(WebRedisKeyConstant.PTING_APPLICATION_KEY, "*"));
        Set<String> keys = RedisHelper.keys(redisKey);
        if(keys.size() == 0) {
            return;
        }
        // 应用是否属于压测中
        List<String> applications = Lists.newArrayList();
        keys.forEach(key -> {
            applications.addAll((List<String>) RedisHelper.getValueByKey(key));
        });
        if(applications.contains(param.getAppName())) {
            performanceBaseDataDAO.insert(param);
        }
    }

}
