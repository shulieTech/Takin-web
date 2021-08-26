package io.shulie.takin.web.biz.service.report;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.entity.domain.dto.report.ReportTraceDTO;
import io.shulie.takin.web.amdb.bean.query.trace.EntranceRuleDTO;
import io.shulie.takin.web.biz.pojo.response.report.ReportLinkDetailResponse;

/**
 * @author qianshui
 * @date 2020/8/17 下午8:22
 */
public interface ReportRealTimeService {

    PageInfo<ReportTraceDTO> getReportLinkList(Long reportId, Long sceneId, Long startTime, Integer type,
        int current, int pageSize);

    PageInfo<ReportTraceDTO> getReportLinkListByReportId(Long reportId, Integer type, int current, int pageSize);

    /**
     * 报告链路详情
     *
     * @param traceId           traceId
     * @param amdbReportTraceId amdb 报告踪迹详情的主键id
     * @return 报告链路详情
     */
    ReportLinkDetailResponse getLinkDetail(String traceId, Integer amdbReportTraceId);

    List<EntranceRuleDTO> getEntryListByBusinessActivityIds(List<Long> businessActivityIds);
}
