package io.shulie.takin.web.biz.service.perfomanceanaly.impl;

import com.pamirs.takin.common.util.http.DateUtil;
import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.ReportTimeResponse;
import io.shulie.takin.web.biz.service.perfomanceanaly.ReportDetailService;
import io.shulie.takin.web.biz.service.report.impl.ReportApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author qianshui
 * @date 2020/11/9 下午4:40
 */
@Service
public class ReportDetailServiceImpl implements ReportDetailService {

    @Autowired
    private ReportApplicationService reportApplicationService;

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
}
