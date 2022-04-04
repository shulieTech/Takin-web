package io.shulie.takin.web.biz.pojo.request.report;


import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportPerformanceInterfaceRequest extends PagingDevice {

    /**
     * 报告Id
     */
    private Long reportId;
    /**
     * 场景Id
     */
    private Long sceneId;
    private String xpathMd5;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式
     */
    private String sortType;
}
