package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 性能分析
 *
 * @author qianshui
 * @date 2020/11/4 上午11:55
 */
@Data
@ApiModel("线程分析入参")
@EqualsAndHashCode(callSuper = true)
public class PerformanceAnalyzeRequest extends PerformanceCommonRequest {
    private static final long serialVersionUID = 729055355150957920L;

    /**
     * 根据报告id，确定起止时间
     */
    private Long reportId;

}
