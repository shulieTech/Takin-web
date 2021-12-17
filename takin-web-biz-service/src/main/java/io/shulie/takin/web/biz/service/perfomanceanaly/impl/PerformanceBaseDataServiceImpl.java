package io.shulie.takin.web.biz.service.perfomanceanaly.impl;

import java.util.List;

import io.shulie.takin.web.biz.convert.performace.PerformanceBaseInputConvert;
import io.shulie.takin.web.biz.pojo.input.PerformanceBaseDataCreateInput;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.ReportTimeResponse;
import io.shulie.takin.web.biz.service.async.AsyncService;
import io.shulie.takin.web.biz.service.perfomanceanaly.PerformanceBaseDataService;
import io.shulie.takin.web.biz.service.perfomanceanaly.ReportDetailService;
import io.shulie.takin.web.data.dao.perfomanceanaly.PerformanceBaseDataDAO;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseDataParam;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseQueryParam;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author qianshui
 * @date 2020/11/4 下午2:44
 */
@Service
@Slf4j
public class PerformanceBaseDataServiceImpl implements PerformanceBaseDataService {

    @Autowired
    private PerformanceBaseDataDAO performanceBaseDataDAO;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private ReportDetailService reportDetailService;

    @Override
    public void cache(PerformanceBaseDataCreateInput input) {
        PerformanceBaseDataParam param = PerformanceBaseInputConvert.INSTANCE.inputToParam(input);
        // 补充header头
        WebPluginUtils.transferTenantParam(WebPluginUtils.traceTenantCommonExt(),param);
        asyncService.savePerformanceBaseData(param);
    }

    @Override
    public List<String> getProcessName(Long reportId, String appName) {
        PerformanceBaseQueryParam param = new PerformanceBaseQueryParam();
        param.setAppName(appName);
        ReportTimeResponse response = reportDetailService.getReportTime(reportId);
        param.setStartTime(response.getStartTime());
        param.setEndTime(response.getEndTime());
        return performanceBaseDataDAO.getProcessNameList(param);
    }

}
