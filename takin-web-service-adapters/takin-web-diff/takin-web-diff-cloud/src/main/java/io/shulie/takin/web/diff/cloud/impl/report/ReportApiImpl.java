package io.shulie.takin.web.diff.cloud.impl.report;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.shulie.takin.cloud.entrypoint.report.CloudReportApi;
import io.shulie.takin.cloud.sdk.model.request.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.report.*;
import io.shulie.takin.cloud.sdk.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.cloud.sdk.model.response.report.ReportDetailResp;
import io.shulie.takin.cloud.sdk.model.response.report.ReportTrendResp;
import io.shulie.takin.cloud.sdk.model.response.report.ScriptNodeTreeResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.diff.api.report.ReportApi;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 无涯
 * @date 2020/12/17 1:10 下午
 */
@Service
public class ReportApiImpl implements ReportApi {

    @Resource(type = CloudReportApi.class)
    private CloudReportApi cloudReportApi;

    @Resource
    private RedisTemplate redisTemplate;

    private LoadingCache<String, Object> cloudReportCache = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build(new CacheLoader<String, Object>() {

        @Override
        public @Nullable Object load(@NonNull String key) {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                return value;
            }
            String id = key.split(":")[1];
            if (key.startsWith("ReportApi#tempReportDetail")) {
                ReportDetailBySceneIdReq req = new ReportDetailBySceneIdReq();
                req.setSceneId(Long.parseLong(id));
                value = cloudReportApi.tempReportDetail(req);
            } else if (key.startsWith("ReportApi#getReportByReportId")) {
                ReportDetailByIdReq req = new ReportDetailByIdReq();
                req.setReportId(Long.parseLong(id));
                value = cloudReportApi.getReportByReportId(req);
            } else if (key.startsWith("ReportApi#getSummaryList")) {
                ReportDetailByIdReq req = new ReportDetailByIdReq();
                req.setReportId(Long.parseLong(id));
                WebPluginUtils.fillCloudUserData(req);
                value = cloudReportApi.summary(req);
            }
            return value;
        }
    });

    @Override
    public ResponseResult<List<Long>> queryListRunningReport(CloudCommonInfoWrapperReq req) {
        try {
            return ResponseResult.success(cloudReportApi.queryListRunningReport(req));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ReportDetailResp tempReportDetail(ReportDetailBySceneIdReq req) {
        String key = String.format("ReportApi#tempReportDetail:%d", req.getSceneId());
        return (ReportDetailResp) cloudReportCache.get(key);
    }


    @Override
    public ReportDetailResp getReportByReportId(ReportDetailByIdReq req) {
        String key = String.format("ReportApi#getReportByReportId:%d", req.getReportId());
        return (ReportDetailResp) cloudReportCache.get(key);
    }


    @Override
    public List<ScriptNodeTreeResp> scriptNodeTree(ScriptNodeTreeQueryReq req) {
        return cloudReportApi.queryNodeTree(req);
    }

    @Override
    public ReportTrendResp tmpReportTrend(ReportTrendQueryReq req) {
        return cloudReportApi.tempTrend(req);
    }

    @Override
    public ReportTrendResp reportTrend(ReportTrendQueryReq req) {
        return cloudReportApi.trend(req);
    }

    @Override
    public NodeTreeSummaryResp getSummaryList(Long reportId) {
        String key = String.format("ReportApi#getSummaryList:%d", reportId);
        NodeTreeSummaryResp object = (NodeTreeSummaryResp) cloudReportCache.get(key);
        return object;
    }

    @Override
    public String getJtlDownLoadUrl(Long reportId) {
        JtlDownloadReq req = new JtlDownloadReq();
        req.setReportId(reportId);
        return cloudReportApi.getJtlDownLoadUrl(req);
    }
}
