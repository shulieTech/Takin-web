package io.shulie.takin.web.biz.pojo.response.report;

import java.util.List;

import com.pamirs.takin.entity.domain.dto.report.ReportTraceDetailDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/25 10:36 上午
 */
@Data
@ApiModel("出参类-报告链路详情出参")
public class ReportLinkDetailResponse {

    /**
     * 入口根ip
     */
    private String entryHostIp;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 追踪链路
     */
    private List<ReportTraceDetailDTO> traces;

    /**
     * 总耗时
     */
    private Long totalCost;

    /**
     * 是否是压测流量, 默认 true
     */
    private boolean clusterTest;
}
