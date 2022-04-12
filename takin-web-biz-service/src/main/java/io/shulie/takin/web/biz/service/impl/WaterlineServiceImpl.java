package io.shulie.takin.web.biz.service.impl;

import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.biz.pojo.response.waterline.Metrics;
import io.shulie.takin.web.biz.pojo.response.waterline.TendencyChart;
import io.shulie.takin.web.biz.service.WaterlineService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.waterline.WaterlineDao;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class WaterlineServiceImpl implements WaterlineService {

    @Autowired
    private WaterlineDao waterlineDao;

    @Autowired
    private AmdbClientProperties properties;

    private static final String GET_APPLICATION_NODES = "/amdb/db/api/waterline/getAllApplicationWithMetrics";
    private static final String GET_TENDENCY_CHART = "/amdb/db/api/waterline/getTendencyChart";

    @Resource
    private SceneManageApi sceneManageApi;

    @Override
    public List<String> getAllActivityNames() {
        ActivityQueryParam param = new ActivityQueryParam();
        WebPluginUtils.fillQueryParam(param);
        return waterlineDao.getAllActivityNames(param);
    }

    @Override
    public List<String> getAllApplicationsByActivity(String activityName) {
        ActivityQueryParam param = new ActivityQueryParam();
        WebPluginUtils.fillQueryParam(param);
        return waterlineDao.getAllApplicationsByActivity(param, activityName);
    }

    @Override
    public List<Metrics> getAllApplicationWithMetrics(List<String> names, String startTime) throws ParseException {
        return doGetAllApplicationWithMetrics(names, startTime);
    }

    @Override
    public List<String> getAllApplicationsWithSceneId(Long sceneId) {
        List<String> ids = new ArrayList<>();
        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(sceneId);
        ResponseResult<SceneManageWrapperResp> sceneDetail = sceneManageApi.getSceneDetail(req);
        if (null != sceneDetail && sceneDetail.getSuccess()) {
            SceneManageWrapperResp resp = sceneDetail.getData();
            List<SceneManageWrapperResp.SceneBusinessActivityRefResp> configs = resp.getBusinessActivityConfig();
            if (CollectionUtils.isNotEmpty(configs)) {
                configs.forEach(config -> {
                    String applicationIds = config.getApplicationIds();
                    if (StringUtils.hasText(applicationIds)) {
                        String[] idArray = applicationIds.split(",");
                        Collections.addAll(ids, idArray);
                    }
                });
            }
        }
        return ids;
    }

    @Override
    public List<String> getApplicationNamesWithIds(List<String> ids) {
        return waterlineDao.getApplicationNamesWithIds(ids);
    }

    @Override
    public void getApplicationNodesAmount(List<Metrics> metrics) {
        if (CollectionUtils.isNotEmpty(metrics)) {
            metrics.forEach(m -> {
                List<String> nodes = waterlineDao.getAllNodesByApplicationName(m.getApplicationName());
                if (CollectionUtils.isNotEmpty(nodes)) {
                    m.setNodesNumber(nodes.size());
                }
            });

        }
    }

    @Override
    public List getApplicationTags(List<Metrics> metrics, String tagName) {
        if (CollectionUtils.isNotEmpty(metrics)) {
            List<Metrics> cacheMetrics = null;
            if (StringUtils.hasText(tagName)) {
                cacheMetrics = new ArrayList<>();
            }
            List<Metrics> finalMetrics_ = cacheMetrics;
            metrics.forEach(m -> {
                List<String> tags = waterlineDao.getApplicationTags(m.getApplicationName());
                if (StringUtils.hasText(tagName) && tags.contains(tagName)) {
                    finalMetrics_.add(m);
                }
                if (CollectionUtils.isNotEmpty(tags)) {
                    m.setTags(tags);
                }
            });
            if (CollectionUtils.isNotEmpty(finalMetrics_)) {
                return finalMetrics_;
            }
        }
        return null;
    }

    @Override
    public List<TendencyChart> getTendencyChart(String applicationName, String startTime, String endTime, List<String> nodes) throws ParseException {
        return doGetTendencyChart(applicationName,startTime,endTime,nodes);
    }

    @Override
    public List<String> getApplicationNodes(String applicationName) {
        return waterlineDao.getAllNodesByApplicationName(applicationName);
    }

    private List<TendencyChart> doGetTendencyChart(String applicationName, String startTime, String endTime, List<String> nodes) throws ParseException {
        String url = properties.getUrl().getAmdb() + GET_TENDENCY_CHART;
        Map request = new HashMap();
        request.put("applicationName", applicationName);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        request.put("startTime", sdf.parse(startTime).getTime());
        request.put("endTime",sdf.parse(endTime).getTime());
        request.put("nodes",nodes);
        request.put("tenantAppKey",WebPluginUtils.traceTenantAppKey());
        request.put("envCode",WebPluginUtils.traceEnvCode());
        try {
            AmdbResult<List<TendencyChart>> appDataResult = AmdbHelper.builder().httpMethod(
                            HttpMethod.GET)
                    .url(url)
                    .param(request)
                    .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                    .eventName("根据应用名称查询大数据性能数据")
                    .list(TendencyChart.class);
            List data = appDataResult.getData();
            if (CollectionUtils.isNotEmpty(data)){
                return data;
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }

    private List<Metrics> doGetAllApplicationWithMetrics(List<String> names, String startTime) throws ParseException {
        String url = properties.getUrl().getAmdb() + GET_APPLICATION_NODES;
        Map request = new HashMap();
        request.put("names", names);
        long time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime).getTime();
        request.put("startTime", time);
        request.put("tenantAppKey",WebPluginUtils.traceTenantAppKey());
        request.put("envCode",WebPluginUtils.traceEnvCode());
        try {
            AmdbResult<List<Metrics>> appDataResult = AmdbHelper.builder().httpMethod(
                            HttpMethod.GET)
                    .url(url)
                    .param(request)
                    .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                    .eventName("根据应用名称查询大数据性能数据")
                    .list(Metrics.class);
            List data = appDataResult.getData();
            if (CollectionUtils.isNotEmpty(data)){
                return data;
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }
}
