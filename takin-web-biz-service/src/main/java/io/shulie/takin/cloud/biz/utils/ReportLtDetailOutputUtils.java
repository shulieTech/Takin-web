package io.shulie.takin.cloud.biz.utils;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.adapter.api.model.response.scenemanage.BusinessActivitySummaryBean;
import io.shulie.takin.web.biz.pojo.output.report.BusinessActivityReportOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportLtDetailOutput;
import io.shulie.takin.web.common.util.ActivityUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReportLtDetailOutputUtils {

    public static ReportLtDetailOutput convertToLt(ReportDetailOutput output) {
        ReportLtDetailOutput lt = new ReportLtDetailOutput();
        if(output == null) {
            return lt;
        }
        lt.setSceneId(output.getSceneId());
        lt.setSceneName(output.getSceneName());
        lt.setStartTime(output.getStartTime());
        lt.setEndTime(DateUtil.formatDateTime(output.getEndTime()));
        lt.setPressureTestTime(output.getTestTotalTime());
        lt.setAmount(output.getAmount());
        lt.setReportId(output.getId());
        lt.setConclusion(output.getConclusion());
        lt.setConclusionRemark(output.getConclusionRemark());
        lt.setTotalRequest(output.getTotalRequest());
        lt.setMaxConcurrent(output.getConcurrent());
        lt.setAvgConcurrent(output.getAvgConcurrent());
        lt.setRealTps(output.getAvgTps());
        lt.setTargetTps(output.getTps());
        lt.setAvgRt(output.getAvgRt());
        lt.setSuccessRate(output.getSuccessRate());
        lt.setSa(output.getSa());
        lt.setMaxTps(output.getMaxTps());
        lt.setMaxRt(output.getMaxRt());
        //解析业务活动信息
        List<BusinessActivitySummaryBean> beanList = output.getBusinessActivity();
        Set<String> appIdSet = new HashSet<>();
        List<BusinessActivityReportOutput> activityList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(beanList)) {
            for(BusinessActivitySummaryBean bean : beanList) {
                String[] appIds = StringUtils.split(bean.getApplicationIds(), ",");
                if(appIds != null && appIds.length > 0) {
                    for(String appId : appIds) {
                        if(StringUtils.isNotBlank(appId)) {
                            appIdSet.add(appId);
                        }
                    }
                }
                BusinessActivityReportOutput activity = new BusinessActivityReportOutput();
                activity.setBusinessActivityId(bean.getBusinessActivityId());
                activity.setBusinessActivityName(bean.getBusinessActivityName());
                JSONObject jsonObject = JSON.parseObject(bean.getFeatures());
                if(jsonObject != null) {
                    String entrance = jsonObject.getString("entrance");
                    ActivityUtil.EntranceJoinEntity entity = ActivityUtil.covertEntranceV2(entrance);
                    activity.setServiceName(entity.getServiceName());
                    activity.setRequestMethod(entity.getMethodName());
                }
                activity.setXpathMd5(bean.getBindRef());
                activityList.add(activity);
            }
        }
        lt.setAppNames(new ArrayList<>(appIdSet));
        lt.setBusinessActivities(activityList);
        return lt;
    }
}
