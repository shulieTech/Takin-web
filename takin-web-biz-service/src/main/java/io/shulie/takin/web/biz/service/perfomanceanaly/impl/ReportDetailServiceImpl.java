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
import com.pamirs.takin.entity.dao.confcenter.TApplicationMntDao;
import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.ReportTimeResponse;
import io.shulie.takin.web.biz.service.perfomanceanaly.ReportDetailService;
import io.shulie.takin.web.biz.service.report.impl.ReportApplicationService;
import io.shulie.takin.web.data.dao.application.AppAgentConfigReportDAO;
import io.shulie.takin.web.data.param.application.ConfigReportInputParam;
import io.shulie.takin.web.data.param.application.CreateAppAgentConfigReportParam;
import io.shulie.takin.web.data.param.application.UpdateAppAgentConfigReportParam;
import io.shulie.takin.web.data.result.application.AppAgentConfigReportDetailResult;
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
    private TApplicationMntDao tApplicationMntDao;

    @Autowired
    private AppAgentConfigReportDAO agentConfigReportDAO;

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
        TApplicationMnt info = tApplicationMntDao.queryApplicationinfoByName(inputParam.getApplicationName());
        if (Objects.isNull(info)) {
            log.error("uploadConfigInfo 应用【{}】不存在", inputParam.getApplicationName());
            return;
        }

        List<CreateAppAgentConfigReportParam> saveList = new ArrayList<>();

        List<ConfigReportInputParam.ConfigInfo> globalConf = inputParam.getGlobalConf();
        List<ConfigReportInputParam.ConfigInfo> appConf = inputParam.getAppConf();
        if (CollectionUtils.isNotEmpty(globalConf)) {
            globalConf.forEach(global -> {
                CreateAppAgentConfigReportParam reportParam = new CreateAppAgentConfigReportParam();
                reportParam.setAgentId(inputParam.getAgentId());
                reportParam.setApplicationId(info.getApplicationId());
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

        if (CollectionUtils.isNotEmpty(appConf)) {
            appConf.forEach(appConfig -> {
                CreateAppAgentConfigReportParam reportParam = new CreateAppAgentConfigReportParam();
                reportParam.setAgentId(inputParam.getAgentId());
                reportParam.setApplicationId(info.getApplicationId());
                reportParam.setApplicationName(inputParam.getApplicationName());
                reportParam.setConfigType(appConfig.getBizType());
                reportParam.setConfigKey(appConfig.getKey());
                reportParam.setConfigValue(appConfig.getValue());
                reportParam.setTenantId(WebPluginUtils.traceTenantId());
                reportParam.setEnvCode(WebPluginUtils.traceEnvCode());
                reportParam.setUserId(WebPluginUtils.traceUserId());
                saveList.add(reportParam);
            });
        }


        List<AppAgentConfigReportDetailResult> dbResults = agentConfigReportDAO.listByAppId(info.getApplicationId());



        Map<String, Map<Integer, List<AppAgentConfigReportDetailResult>>> dbMap = CollStreamUtil.groupBy2Key(dbResults,
                AppAgentConfigReportDetailResult::getAgentId, AppAgentConfigReportDetailResult::getConfigType);

        Map<String, Map<Integer, List<CreateAppAgentConfigReportParam>>> reportMap = CollStreamUtil.groupBy2Key(saveList,
                CreateAppAgentConfigReportParam::getAgentId, CreateAppAgentConfigReportParam::getConfigType);

        List<UpdateAppAgentConfigReportParam> updateList = new ArrayList<>();

        dbMap.forEach((agentId, report2TypeForDbMap) -> {
            if (reportMap.containsKey(agentId)) {
                Map<Integer, List<CreateAppAgentConfigReportParam>> report2TypeForReportMap = reportMap.get(agentId);
                report2TypeForDbMap.forEach((configType, reports) -> {
                    if (report2TypeForReportMap.containsKey(configType)) {
                        List<CreateAppAgentConfigReportParam> params = report2TypeForReportMap.get(configType);
                        Map<String, CreateAppAgentConfigReportParam> report2KeyForParamsMap = params.stream()
                                .collect(Collectors
                                        .toMap(CreateAppAgentConfigReportParam::getConfigKey, Function.identity()));


                        Map<String, AppAgentConfigReportDetailResult> report2KeyForDbMap = reports.stream()
                                .collect(Collectors
                                        .toMap(AppAgentConfigReportDetailResult::getConfigKey, Function.identity()));


                        report2KeyForDbMap.forEach((configKey, data) -> {
                            if (report2KeyForParamsMap.containsKey(configKey)) {
                                CreateAppAgentConfigReportParam removeObj = report2KeyForParamsMap.get(configKey);
                                saveList.remove(removeObj);
                                UpdateAppAgentConfigReportParam convert = Convert.convert(UpdateAppAgentConfigReportParam.class,
                                        removeObj);
                                convert.setId(data.getId());
                                updateList.add(convert);
                            }
                        });
                    }
                });
            }
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
}
