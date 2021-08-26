package io.shulie.takin.web.biz.service.perfomanceanaly;

import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.ReportTimeResponse;

/**
 * @author qianshui
 * @date 2020/11/9 下午4:40
 */
public interface ReportDetailService {

    ReportTimeResponse getReportTime(Long reportId);
}
