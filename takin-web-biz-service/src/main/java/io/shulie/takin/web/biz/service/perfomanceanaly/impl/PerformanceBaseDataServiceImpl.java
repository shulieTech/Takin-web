package io.shulie.takin.web.biz.service.perfomanceanaly.impl;

import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.date.DateUtil;
import org.springframework.stereotype.Service;
import io.shulie.takin.web.biz.service.async.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import io.shulie.takin.web.biz.pojo.input.PerformanceBaseDataCreateInput;
import io.shulie.takin.web.biz.service.perfomanceanaly.ReportDetailService;
import io.shulie.takin.web.data.dao.perfomanceanaly.PerformanceBaseDataDAO;
import io.shulie.takin.web.biz.convert.performace.PerformanceBaseInputConvert;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseDataParam;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.ReportTimeResponse;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseQueryParam;
import io.shulie.takin.web.biz.service.perfomanceanaly.PerformanceBaseDataService;

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

    @Override
    public void clearData(Integer time) {
        performanceBaseDataDAO.clearData(DateUtil.offsetSecond(new Date(), -time).toString());
    }
}
