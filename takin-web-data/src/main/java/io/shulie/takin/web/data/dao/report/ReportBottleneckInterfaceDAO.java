package io.shulie.takin.web.data.dao.report;

import java.util.List;

import io.shulie.takin.web.data.param.report.ReportLocalQueryParam;
import io.shulie.takin.web.data.param.report.ReportBottleneckInterfaceCreateParam;
import io.shulie.takin.web.data.result.report.ReportBottleneckInterfaceResult;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.data.dao.report
 * @ClassName: TReportBottleneckInterfaceDAO
 * @Description: TODO
 * @Date: 2021/11/23 10:49
 */
public interface ReportBottleneckInterfaceDAO {

    /**
     * 批量插入
     * @param params
     * @return
     */
    void insertBatch(List<ReportBottleneckInterfaceCreateParam> params);

    /**
     *
     * @param queryParam
     * @return
     */
    List<ReportBottleneckInterfaceResult> selectByExample(ReportLocalQueryParam queryParam);

    /**
     * 根据报告id查询
     * @param reportId
     * @return
     */
    Long selectCountByReportId(Long reportId);

    /**
     * 根据报告id删除
     * @param reportId
     */
    void deleteByReportId(Long reportId);
}
