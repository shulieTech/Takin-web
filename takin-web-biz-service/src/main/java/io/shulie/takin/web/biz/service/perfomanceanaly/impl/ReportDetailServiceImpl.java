package io.shulie.takin.web.biz.service.perfomanceanaly.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.convert.Convert;
import com.pamirs.takin.common.util.http.DateUtil;
import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.ReportTimeResponse;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.perfomanceanaly.ReportDetailService;
import io.shulie.takin.web.biz.service.report.impl.ReportApplicationService;
import io.shulie.takin.web.data.dao.application.AppAgentConfigReportDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.param.application.ConfigReportInputParam;
import io.shulie.takin.web.data.param.application.CreateAppAgentConfigReportParam;
import io.shulie.takin.web.data.param.application.UpdateAppAgentConfigReportParam;
import io.shulie.takin.web.data.result.application.AppAgentConfigReportDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author qianshui
 * @date 2020/11/9 下午4:40
 */
@Service
@Slf4j
public class ReportDetailServiceImpl implements ReportDetailService {

    @Autowired
    private ReportApplicationService reportApplicationService;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private AppAgentConfigReportDAO agentConfigReportDAO;

    @Autowired
    private ApplicationService applicationService;

    @Override
    public ReportTimeResponse getReportTime(Long reportId) {
        ReportDetailDTO reportDetail = reportApplicationService.getReportApplication(reportId).getReportDetail();
        ReportTimeResponse response = new ReportTimeResponse();
        response.setStartTime(reportDetail.getStartTime());
        if (reportDetail.getEndTime() != null) {
            response.setEndTime(DateUtil.getYYYYMMDDHHMMSS(reportDetail.getEndTime()));
        }
        return response;
    }

    /**
     * agent上报配置信息
     *
     * @param inputParam
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void uploadConfigInfo(ConfigReportInputParam inputParam) {
        if (inputParam == null) {
            log.error("uploadConfigInfo 上传参数不能为空");
            return;
        }
        Long applicationId = applicationService.queryApplicationIdByAppName(inputParam.getApplicationName());
        if (Objects.isNull(applicationId)) {
            log.error("uploadConfigInfo 应用【{}】不存在", inputParam.getApplicationName());
            return;
        }

        List<CreateAppAgentConfigReportParam> saveList = new ArrayList<>();

        List<ConfigReportInputParam.ConfigInfo> globalConf = inputParam.getGlobalConf();
        List<ConfigReportInputParam.ConfigInfo> appConf = inputParam.getAppConf();
        List<ConfigReportInputParam.ConfigInfo> newConfigInfo = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(appConf)) {
            newConfigInfo.addAll(appConf);
        }
        if (CollectionUtils.isNotEmpty(globalConf)) {
            newConfigInfo.addAll(globalConf);
        }
        if (CollectionUtils.isNotEmpty(newConfigInfo)) {
            newConfigInfo.forEach(global -> {
                CreateAppAgentConfigReportParam reportParam = new CreateAppAgentConfigReportParam();
                reportParam.setAgentId(inputParam.getAgentId());
                reportParam.setApplicationId(applicationId);
                reportParam.setApplicationName(inputParam.getApplicationName());
                reportParam.setConfigType(global.getBizType());
                reportParam.setConfigKey(global.getKey());
                reportParam.setConfigValue(global.getValue());
                reportParam.setTenantId(WebPluginUtils.traceTenantId());
                reportParam.setEnvCode(WebPluginUtils.traceEnvCode());
                reportParam.setUserId(WebPluginUtils.traceUserId());
                saveList.add(reportParam);
            });
        }
        List<AppAgentConfigReportDetailResult> dbResults = agentConfigReportDAO.listByAppId(applicationId);

        Map<String, Map<Integer, List<AppAgentConfigReportDetailResult>>> dbMap = CollStreamUtil.groupBy2Key(dbResults,
                AppAgentConfigReportDetailResult::getAgentId, AppAgentConfigReportDetailResult::getConfigType);

        Map<String, Map<Integer, List<CreateAppAgentConfigReportParam>>> reportMap = CollStreamUtil.groupBy2Key(saveList,
                CreateAppAgentConfigReportParam::getAgentId, CreateAppAgentConfigReportParam::getConfigType);

        List<UpdateAppAgentConfigReportParam> updateList = new ArrayList<>();

        dbMap.forEach((agentId, report2TypeForDbMap) -> {
            if (!reportMap.containsKey(agentId)) {
                return;
            }
            Map<Integer, List<CreateAppAgentConfigReportParam>> report2TypeForReportMap = reportMap.get(agentId);

            report2TypeForDbMap.forEach((configType, reports) -> {
                if (!report2TypeForReportMap.containsKey(configType)) {
                    return;
                }
                Map<String, CreateAppAgentConfigReportParam> report2KeyForParamsMap = report2TypeForReportMap.get(configType)
                        .stream().collect(Collectors.toMap(CreateAppAgentConfigReportParam::getConfigKey, Function.identity()));

                Map<String, AppAgentConfigReportDetailResult> report2KeyForDbMap = reports.stream()
                        .collect(Collectors.toMap(AppAgentConfigReportDetailResult::getConfigKey, Function.identity()));

                report2KeyForDbMap.forEach((configKey, data) -> {
                    if (!report2KeyForParamsMap.containsKey(configKey)) {
                        return;
                    }
                    CreateAppAgentConfigReportParam removeObj = report2KeyForParamsMap.get(configKey);
                    saveList.remove(removeObj);
                    UpdateAppAgentConfigReportParam convert = Convert.convert(UpdateAppAgentConfigReportParam.class, removeObj);
                    convert.setId(data.getId());
                    updateList.add(convert);

                });
            });
        });


//        Map<Integer, List<AppAgentConfigReportDetailResult>> dbMap = CollStreamUtil.groupByKey(dbResults,
//            AppAgentConfigReportDetailResult::getConfigType);
//        Map<Integer, List<CreateAppAgentConfigReportParam>> reportMap = CollStreamUtil.groupByKey(saveList,
//            CreateAppAgentConfigReportParam::getConfigType);
//
//        List<UpdateAppAgentConfigReportParam> updateList = new ArrayList<>();
//
//        dbMap.forEach((k, v) -> {
//            if (reportMap.containsKey(k)) {
//                List<CreateAppAgentConfigReportParam> params = reportMap.get(k);
//                Map<String, AppAgentConfigReportDetailResult> m1 = v.stream().
//                    collect(Collectors.toMap(AppAgentConfigReportDetailResult::getConfigKey, Function.identity()));
//                Map<String, CreateAppAgentConfigReportParam> m2 = params.stream().collect(
//                    Collectors.toMap(CreateAppAgentConfigReportParam::getConfigKey, Function.identity()));
//
//                m1.forEach((key, data) -> {
//                    if (m2.containsKey(key)) {
//                        saveList.remove(m2.get(key));
//                        UpdateAppAgentConfigReportParam convert = Convert.convert(UpdateAppAgentConfigReportParam.class,
//                            m2.get(key));
//                        convert.setId(data.getId());
//                        updateList.add(convert);
//                    }
//                });
//            }
//        });

        agentConfigReportDAO.batchSave(saveList);
        agentConfigReportDAO.batchUpdate(updateList);
    }

    /**
     * 清理过期的agent配置心跳数据
     */
    @Override
    public void clearExpiredData() {
        agentConfigReportDAO.clearExpiredData();
    }
}
