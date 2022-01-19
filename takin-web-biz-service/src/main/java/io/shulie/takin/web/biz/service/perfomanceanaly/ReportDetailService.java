package io.shulie.takin.web.biz.service.perfomanceanaly;

import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.ReportTimeResponse;
import io.shulie.takin.web.data.param.application.ConfigReportInputParam;

/**
 * @author qianshui
 * @date 2020/11/9 下午4:40
 */
public interface ReportDetailService {

    ReportTimeResponse getReportTime(Long reportId);

    /**
     * 保存agent心跳配置
     * @param inputParam
     */
    void uploadConfigInfo(ConfigReportInputParam inputParam);

    /**
     * 清理过期的angent配置心跳数据
     */
    void clearExpiredData();

}
